package com.example.booking.service;

import com.example.booking.dto.AuthRequest;
import com.example.booking.dto.AuthResponse;
import com.example.booking.dto.GoogleAuthRequest;
import com.example.booking.dto.RegisterRequest;
import com.example.booking.model.User;
import com.example.booking.repository.UserRepository;
import com.example.booking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        if (request.getPhone() != null && !request.getPhone().isEmpty() 
            && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName());
    }
    
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName());
    }
    
    @Transactional
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
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
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName());
    }
}