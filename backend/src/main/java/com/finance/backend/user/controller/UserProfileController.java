package com.finance.backend.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.backend.auth.security.AuthenticatedUserPrincipal;
import com.finance.backend.user.dto.UpdateUserProfileRequestDTO;
import com.finance.backend.user.dto.UserProfileResponseDTO;
import com.finance.backend.user.service.UserProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public UserProfileResponseDTO getCurrentUser(@AuthenticationPrincipal AuthenticatedUserPrincipal currentUser) {
        return userProfileService.getCurrentUserProfile(currentUser.getId());
    }

    @PutMapping("/me")
    public UserProfileResponseDTO updateCurrentUser(
            @AuthenticationPrincipal AuthenticatedUserPrincipal currentUser,
            @Valid @RequestBody UpdateUserProfileRequestDTO request) {
        return userProfileService.updateCurrentUserProfile(currentUser.getId(), request);
    }
}
