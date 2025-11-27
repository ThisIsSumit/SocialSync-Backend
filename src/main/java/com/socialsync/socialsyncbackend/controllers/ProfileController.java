package com.socialsync.socialsyncbackend.controllers;


import com.socialsync.socialsyncbackend.dto.ProfileUpdateRequest;
import com.socialsync.socialsyncbackend.entity.User;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import com.socialsync.socialsyncbackend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Profile", description = "User profile management")
public class ProfileController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Get user profile")
    public ResponseEntity<User> getProfile(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping
    @Operation(summary = "Update user profile")
    public ResponseEntity<User> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody ProfileUpdateRequest request) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }
}
