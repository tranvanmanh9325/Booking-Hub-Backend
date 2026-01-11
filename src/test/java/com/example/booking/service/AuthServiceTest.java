package com.example.booking.service;

import com.example.booking.dto.AuthRequest;
import com.example.booking.dto.AuthResponse;
import com.example.booking.dto.RegisterRequest;
import com.example.booking.mapper.UserMapper;
import com.example.booking.model.RefreshToken;
import com.example.booking.model.User;
import com.example.booking.repository.UserRepository;
import com.example.booking.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserMapper userMapper;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil, refreshTokenService, emailService,
                userMapper);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFullName("Test User");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toUser(any(RegisterRequest.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyString())).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(refreshToken);

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("access-token");
        when(userMapper.toAuthResponse(any(User.class), anyString(), anyString(), anyString()))
                .thenReturn(expectedResponse);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        verify(emailService, times(1)).sendRegistrationConfirmation(anyString(), anyString());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString())).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(refreshToken);

        AuthResponse expectedResponse = new AuthResponse();
        expectedResponse.setToken("access-token");
        when(userMapper.toAuthResponse(any(User.class), anyString(), anyString(), anyString()))
                .thenReturn(expectedResponse);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
    }
}
