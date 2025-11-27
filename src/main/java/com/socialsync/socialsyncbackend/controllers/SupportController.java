package com.socialsync.socialsyncbackend.controllers;


import com.socialsync.socialsyncbackend.dto.SupportTicketRequest;
import com.socialsync.socialsyncbackend.dto.SupportTicketResponse;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import com.socialsync.socialsyncbackend.services.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Tag(name = "Support", description = "Customer support endpoints")
public class SupportController {

    private final SupportService supportService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create a support ticket")
    public ResponseEntity<SupportTicketResponse> createTicket(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SupportTicketRequest request) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(supportService.createTicket(userId, request));
    }

    @GetMapping("/tickets")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get user's support tickets")
    public ResponseEntity<List<SupportTicketResponse>> getTickets(
            @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(supportService.getUserTickets(userId));
    }

    @GetMapping("/faqs")
    @Operation(summary = "Get frequently asked questions")
    public ResponseEntity<List<String>> getFAQs() {
        return ResponseEntity.ok(supportService.getFAQs());
    }
}
