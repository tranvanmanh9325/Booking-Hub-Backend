package com.example.booking.controller;

import com.example.booking.dto.AuthRequest;
import com.example.booking.dto.AuthResponse;
import com.example.booking.dto.GoogleAuthRequest;
import com.example.booking.dto.RefreshTokenRequest;
import com.example.booking.dto.RegisterRequest;
import com.example.booking.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller xác thực người dùng.
 * Cung cấp các API cho đăng ký, đăng nhập, làm mới token và đăng xuất.
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    // Cookie duration matching refresh token validity (e.g., 7 days)
    // You might want to move this to properties or constant
    private static final long REFRESH_TOKEN_DURATION_SECONDS = 7 * 24 * 60 * 60;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Đăng ký người dùng mới.
     * 
     * @param request Thông tin đăng ký của người dùng
     * @return ResponseEntity chứa thông tin xác thực sau khi đăng ký thành công
     */
    @Operation(summary = "Register a new user", description = "Registers a new user and returns a JWT token and refresh token cookie.")
    @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    /**
     * Đăng nhập người dùng.
     * 
     * @param request Thông tin đăng nhập (email, password)
     * @return ResponseEntity chứa thông tin xác thực (token)
     */
    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token and refresh token cookie.")
    @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    /**
     * Đăng nhập bằng Google.
     * 
     * @param request Token xác thực từ Google
     * @return ResponseEntity chứa thông tin xác thực (token)
     */
    @Operation(summary = "Google Login", description = "Authenticates a user via Google and returns a JWT token and refresh token cookie.")
    @ApiResponse(responseCode = "200", description = "User logged in successfully with Google", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleAuth(@RequestBody GoogleAuthRequest request) {
        AuthResponse response = authService.googleAuth(request);
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    /**
     * Làm mới token truy cập (Refresh Token).
     * 
     * @param refreshToken Token làm mới từ cookie (tùy chọn)
     * @return ResponseEntity chứa thông tin xác thực mới
     */
    @Operation(summary = "Refresh Token", description = "Refreshes the access token using a valid refresh token cookie.")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        AuthResponse response = authService.refreshToken(request);

        // Rotate refresh token in cookie as well
        ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    /**
     * Đăng xuất người dùng.
     * Xóa cookie chứa refresh token.
     * 
     * @return ResponseEntity thông báo đăng xuất thành công
     */
    @Operation(summary = "Logout user", description = "Logs out the user by clearing the refresh token cookie.")
    @ApiResponse(responseCode = "200", description = "User logged out successfully")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

    /**
     * Yêu cầu đặt lại mật khẩu.
     * Gửi liên kết đặt lại mật khẩu qua email.
     * 
     * @param request Map chứa email của người dùng
     * @return ResponseEntity thông báo gửi email thành công
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        authService.forgotPassword(email);
        return ResponseEntity.ok().body("Password reset link sent to email");
    }

    /**
     * Đặt lại mật khẩu mới.
     * 
     * @param request Map chứa token đặt lại và mật khẩu mới
     * @return ResponseEntity thông báo đổi mật khẩu thành công
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok().body("Password has been reset successfully");
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // Set to true in production (checking if request is secure is better but this
                               // is explicit)
                .path("/")
                .maxAge(REFRESH_TOKEN_DURATION_SECONDS)
                .sameSite("Strict")
                .build();
    }
}