package com.example.booking.service;

import com.example.booking.dto.AuthRequest;
import com.example.booking.dto.AuthResponse;
import com.example.booking.dto.RefreshTokenRequest;
import com.example.booking.dto.GoogleAuthRequest;
import com.example.booking.dto.RegisterRequest;
import com.example.booking.model.User;
import com.example.booking.repository.UserRepository;
import com.example.booking.util.JwtUtil;

import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.ConflictException;
import com.example.booking.exception.TokenRefreshException;
import com.example.booking.exception.UnauthorizedException;
import com.example.booking.model.RefreshToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new ConflictException("Email already exists");
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()
                && userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("Phone number already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken.getToken(), "Bearer", user.getId(), user.getEmail(),
                user.getFullName());
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        logger.info("Authenticating user with email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Authentication failed - bad credentials for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken.getToken(), "Bearer", user.getId(), user.getEmail(),
                user.getFullName());
    }

    @Transactional
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        // Check if user exists by email
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            // Create new user for Google OAuth
            user = new User();
            user.setEmail(request.getEmail());
            user.setFullName(request.getName() != null ? request.getName() : request.getEmail());
            user.setAvatarUrl(request.getPicture());
            // Generate a random password for Google users (they won't use it)
            user.setPassword(passwordEncoder.encode("GOOGLE_OAUTH_" + System.currentTimeMillis()));

            user = userRepository.save(user);
        } else {
            // Update existing user's avatar if provided
            if (request.getPicture() != null && !request.getPicture().isEmpty()) {
                user.setAvatarUrl(request.getPicture());
            }
            // Update name if provided and different
            if (request.getName() != null && !request.getName().isEmpty()
                    && !request.getName().equals(user.getFullName())) {
                user.setFullName(request.getName());
            }
            user = userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(token, refreshToken.getToken(), "Bearer", user.getId(), user.getEmail(),
                user.getFullName());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail());
                    return new AuthResponse(token, requestRefreshToken, "Bearer", user.getId(), user.getEmail(),
                            user.getFullName());
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }
}