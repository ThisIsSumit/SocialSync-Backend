package com.socialsync.socialsyncbackend.controllers;

import com.socialsync.socialsyncbackend.dto.*;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import com.socialsync.socialsyncbackend.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user email with token")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/mfa/challenge")
    @Operation(summary = "Generate MFA challenge code after credential validation")
    public ResponseEntity<MfaChallengeResponse> createMfaChallenge(@Valid @RequestBody MfaChallengeRequest request) {
        return ResponseEntity.ok(authService.createMfaChallenge(request));
    }

    @PostMapping("/mfa/toggle")
    @Operation(summary = "Enable or disable MFA for logged in user")
    public ResponseEntity<String> toggleMfa(
            @RequestHeader("Authorization") String token,
            @RequestBody MfaToggleRequest request) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        boolean enabled = request.getEnabled() != null && request.getEnabled();
        return ResponseEntity.ok(authService.updateMfaStatus(userId, enabled));
    }
}