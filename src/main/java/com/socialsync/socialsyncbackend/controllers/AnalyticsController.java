package com.socialsync.socialsyncbackend.controllers;


import com.socialsync.socialsyncbackend.dto.AnalyticsResponse;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import com.socialsync.socialsyncbackend.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Analytics", description = "View social media analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Get analytics for all connected accounts")
    public ResponseEntity<List<AnalyticsResponse>> getAnalytics(
            @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(analyticsService.getAnalytics(userId));
    }

    @GetMapping("/reports")
    @Operation(summary = "Generate and download performance reports")
    public ResponseEntity<byte[]> generateReport(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        // Implementation would return PDF/CSV report
        return ResponseEntity.ok(new byte[0]);
    }
}
