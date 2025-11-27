package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.dto.*;
import com.socialsync.socialsyncbackend.entity.*;
import com.socialsync.socialsyncbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostSchedulingService {

    private final ScheduledPostRepository postRepository;
    private final SocialMediaAccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduledPostResponse schedulePost(Long userId, SchedulePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SocialMediaAccount account = accountRepository.findByIdAndUserId(request.getAccountId(), userId)
                .orElseThrow(() -> new RuntimeException("Social media account not found"));

        ScheduledPost post = ScheduledPost.builder()
                .user(user)
                .account(account)
                .content(request.getContent())
                .mediaUrl(request.getMediaUrl())
                .scheduledTime(request.getScheduledTime())
                .status(PostStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        post = postRepository.save(post);

        return mapToResponse(post);
    }

    public List<ScheduledPostResponse> getScheduledPosts(Long userId) {
        return postRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteScheduledPost(Long userId, Long postId) {
        ScheduledPost post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new RuntimeException("Scheduled post not found"));

        if (post.getStatus() == PostStatus.PUBLISHED) {
            throw new RuntimeException("Cannot delete published post");
        }

        postRepository.delete(post);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void publishScheduledPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledPost> posts = postRepository.findByStatusAndScheduledTimeBefore(PostStatus.PENDING, now);

        for (ScheduledPost post : posts) {
            try {
                // Here you would integrate with actual social media APIs
                publishToSocialMedia(post);
                post.setStatus(PostStatus.PUBLISHED);
                post.setPublishedAt(LocalDateTime.now());
            } catch (Exception e) {
                post.setStatus(PostStatus.FAILED);
                post.setErrorMessage(e.getMessage());
            }
            postRepository.save(post);
        }
    }

    private void publishToSocialMedia(ScheduledPost post) {
        // Implementation would integrate with actual social media APIs
        // For example: Facebook Graph API, Twitter API, Instagram Graph API, LinkedIn API
        System.out.println("Publishing post to " + post.getAccount().getPlatform());
    }

    private ScheduledPostResponse mapToResponse(ScheduledPost post) {
        return ScheduledPostResponse.builder()
                .id(post.getId())
                .accountId(post.getAccount().getId())
                .platform(post.getAccount().getPlatform().name())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .scheduledTime(post.getScheduledTime())
                .status(post.getStatus().name())
                .createdAt(post.getCreatedAt())
                .publishedAt(post.getPublishedAt())
                .build();
    }
}