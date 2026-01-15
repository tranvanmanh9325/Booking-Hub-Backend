package com.example.booking.service;

import com.example.booking.dto.UserProfileDTO;
import com.example.booking.dto.UserUpdateDTO;
import com.example.booking.exception.ConflictException;
import com.example.booking.model.User;
import com.example.booking.repository.HotelBookingRepository;
import com.example.booking.repository.HotelReviewRepository;
import com.example.booking.repository.MovieBookingRepository;
import com.example.booking.repository.RefreshTokenRepository;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final MovieBookingRepository movieBookingRepository;
    private final HotelReviewRepository hotelReviewRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    @SuppressWarnings("null")
    public User updateProfile(String email, UserProfileDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new ConflictException("Số điện thoại đã được sử dụng bởi tài khoản khác");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        return userRepository.save(user);
    }

    @Transactional
    public String uploadAvatar(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            String avatarUrl = cloudinaryService.uploadAvatar(file);
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
            return avatarUrl;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @SuppressWarnings("null")
    public User updateUser(Long id, UserUpdateDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new ConflictException("Số điện thoại đã được sử dụng bởi tài khoản khác");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return userRepository.save(user);
    }

    @Transactional
    @SuppressWarnings("null")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.deleteByUser(user);
        hotelBookingRepository.deleteByUser(user);
        movieBookingRepository.deleteByUser(user);
        hotelReviewRepository.deleteByUser(user);
        userRepository.delete(user);
    }
}