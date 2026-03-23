package com.socialsync.socialsyncbackend.controllers;


import com.socialsync.socialsyncbackend.dto.SchedulePostRequest;
import com.socialsync.socialsyncbackend.dto.ScheduledPostResponse;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import com.socialsync.socialsyncbackend.services.PostSchedulingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Posts", description = "Schedule and manage social media posts")
public class PostController {

    private final PostSchedulingService postService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Schedule a new post")
    public ResponseEntity<ScheduledPostResponse> schedulePost(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody SchedulePostRequest request) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(postService.schedulePost(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all scheduled posts")
    public ResponseEntity<List<ScheduledPostResponse>> getScheduledPosts(
            @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        return ResponseEntity.ok(postService.getScheduledPosts(userId));
    }

    @GetMapping("/calendar")
    @Operation(summary = "Get scheduled posts in calendar range")
    public ResponseEntity<List<ScheduledPostResponse>> getCalendarPosts(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusNanos(1);
        return ResponseEntity.ok(postService.getScheduledPostsForRange(userId, start, end));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete a scheduled post")
    public ResponseEntity<Void> deletePost(
            @RequestHeader("Authorization") String token,
            @PathVariable Long postId) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractUserId(jwt);
        postService.deleteScheduledPost(userId, postId);
        return ResponseEntity.noContent().build();
    }
}