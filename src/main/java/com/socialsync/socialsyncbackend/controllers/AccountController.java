package com.socialsync.socialsyncbackend.controllers;

import com.socialsync.socialsyncbackend.dto.ConnectAccountRequest;
import com.socialsync.socialsyncbackend.dto.SocialAccountResponse;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import com.socialsync.socialsyncbackend.services.SocialMediaAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Social Media Accounts", description = "Manage connected social media accounts")
public class AccountController {

    private final SocialMediaAccountService accountService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Connect a new social media account")
    public ResponseEntity<SocialAccountResponse> connectAccount(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ConnectAccountRequest request) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(accountService.connectAccount(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all connected accounts")
    public ResponseEntity<List<SocialAccountResponse>> getAccounts(
            @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(accountService.getConnectedAccounts(userId));
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Disconnect a social media account")
    public ResponseEntity<Void> disconnectAccount(
            @RequestHeader("Authorization") String token,
            @PathVariable Long accountId) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        accountService.disconnectAccount(userId, accountId);
        return ResponseEntity.noContent().build();
    }
}
