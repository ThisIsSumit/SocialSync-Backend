package com.socialsync.socialsyncbackend.services;


import com.socialsync.socialsyncbackend.dto.AuthResponse;
import com.socialsync.socialsyncbackend.dto.LoginRequest;
import com.socialsync.socialsyncbackend.dto.MfaChallengeRequest;
import com.socialsync.socialsyncbackend.dto.MfaChallengeResponse;
import com.socialsync.socialsyncbackend.dto.SignUpRequest;
import com.socialsync.socialsyncbackend.entity.User;
import com.socialsync.socialsyncbackend.repositories.UserRepository;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
        private final NotificationService notificationService;

        @Value("${app.frontend.base-url:http://localhost:3000}")
        private String frontendBaseUrl;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .emailVerified(false)
                .mfaEnabled(false)
                .emailVerificationToken(UUID.randomUUID().toString())
                .emailVerificationExpiresAt(LocalDateTime.now().plusHours(24))
                .build();

        user = userRepository.save(user);

        String verificationLink = frontendBaseUrl + "/verify-email?token=" + user.getEmailVerificationToken();
        notificationService.sendVerificationEmail(user.getEmail(), verificationLink);

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

                if (!Boolean.TRUE.equals(user.getEmailVerified())) {
                        throw new RuntimeException("Email not verified. Verify your account before login.");
                }

                if (Boolean.TRUE.equals(user.getMfaEnabled())) {
                        if (request.getMfaCode() == null || request.getMfaCode().isBlank()) {
                                throw new RuntimeException("MFA code required");
                        }
                        if (user.getMfaCode() == null || user.getMfaCodeExpiresAt() == null
                                        || user.getMfaCodeExpiresAt().isBefore(LocalDateTime.now())
                                        || !user.getMfaCode().equals(request.getMfaCode())) {
                                throw new RuntimeException("Invalid or expired MFA code");
                        }

                        user.setMfaCode(null);
                        user.setMfaCodeExpiresAt(null);
                        userRepository.save(user);
                }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

        @Transactional
        public String verifyEmail(String token) {
                User user = userRepository.findByEmailVerificationToken(token)
                                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

                if (user.getEmailVerificationExpiresAt() == null || user.getEmailVerificationExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("Verification token expired");
                }

                user.setEmailVerified(true);
                user.setEmailVerificationToken(null);
                user.setEmailVerificationExpiresAt(null);
                userRepository.save(user);

                return "Email verified successfully";
        }

        @Transactional
        public MfaChallengeResponse createMfaChallenge(MfaChallengeRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );

                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String code = String.format("%06d", (int) (Math.random() * 1_000_000));
                LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
                user.setMfaCode(code);
                user.setMfaCodeExpiresAt(expiresAt);
                userRepository.save(user);

                                notificationService.sendMfaCodeEmail(user.getEmail(), code);
                                if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
                                        notificationService.sendMfaCodeSms(user.getPhoneNumber(), code);
                                }

                return MfaChallengeResponse.builder()
                                .message("MFA code generated")
                                .expiresAt(expiresAt)
                                .build();
        }

        @Transactional
        public String updateMfaStatus(Long userId, boolean enabled) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                user.setMfaEnabled(enabled);
                if (!enabled) {
                        user.setMfaCode(null);
                        user.setMfaCodeExpiresAt(null);
                }
                userRepository.save(user);

                return enabled ? "MFA enabled" : "MFA disabled";
        }
}