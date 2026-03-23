package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.dto.ScheduledPostResponse;
import com.socialsync.socialsyncbackend.entity.PlatformType;
import com.socialsync.socialsyncbackend.entity.PostStatus;
import com.socialsync.socialsyncbackend.entity.ScheduledPost;
import com.socialsync.socialsyncbackend.entity.SocialMediaAccount;
import com.socialsync.socialsyncbackend.repositories.ScheduledPostRepository;
import com.socialsync.socialsyncbackend.repositories.SocialMediaAccountRepository;
import com.socialsync.socialsyncbackend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostSchedulingServiceTest {

    @Mock
    private ScheduledPostRepository postRepository;

    @Mock
    private SocialMediaAccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostSchedulingService postSchedulingService;

    @Test
    void getScheduledPostsForRange_shouldMapRepositoryResults() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);

        SocialMediaAccount account = SocialMediaAccount.builder()
                .id(20L)
                .platform(PlatformType.FACEBOOK)
                .build();

        ScheduledPost post = ScheduledPost.builder()
                .id(30L)
                .account(account)
                .content("Calendar post")
                .scheduledTime(LocalDateTime.of(2026, 1, 10, 9, 0))
                .status(PostStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 1, 1, 9, 0))
                .build();

        when(postRepository.findByUserIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(userId, start, end))
                .thenReturn(List.of(post));

        List<ScheduledPostResponse> responses = postSchedulingService.getScheduledPostsForRange(userId, start, end);

        assertEquals(1, responses.size());
        assertEquals(30L, responses.get(0).getId());
        assertEquals(20L, responses.get(0).getAccountId());
        assertEquals("FACEBOOK", responses.get(0).getPlatform());
        assertEquals("Calendar post", responses.get(0).getContent());
        assertEquals("PENDING", responses.get(0).getStatus());
    }
}
