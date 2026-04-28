package com.finance.backend.user.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.auth.model.User;
import com.finance.backend.auth.repository.UserRepository;
import com.finance.backend.common.exception.BadRequestException;
import com.finance.backend.common.exception.ResourceNotFoundException;
import com.finance.backend.user.dto.UpdateUserProfileRequestDTO;
import com.finance.backend.user.dto.UserProfileResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponseDTO getCurrentUserProfile(Long userId) {
        User user = getUserById(userId);
        return mapToResponse(user);
    }

    @Transactional
    public UserProfileResponseDTO updateCurrentUserProfile(Long userId, UpdateUserProfileRequestDTO request) {
        User user = getUserById(userId);
        String normalizedEmail = normalizeEmail(request.email());

        if (!user.getEmail().equals(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered");
        }

        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);

        return mapToResponse(userRepository.save(user));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserProfileResponseDTO mapToResponse(User user) {
        return UserProfileResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
