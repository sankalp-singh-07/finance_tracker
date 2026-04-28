package com.finance.backend.auth.service;

import java.util.Locale;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finance.backend.auth.dto.AuthResponseDTO;
import com.finance.backend.auth.dto.LoginRequestDTO;
import com.finance.backend.auth.dto.SignupRequestDTO;
import com.finance.backend.auth.model.User;
import com.finance.backend.auth.repository.UserRepository;
import com.finance.backend.auth.security.JwtUtil;
import com.finance.backend.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponseDTO signup(SignupRequestDTO request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .name(request.name().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .build();
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail());
        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        String normalizedEmail = normalizeEmail(request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
