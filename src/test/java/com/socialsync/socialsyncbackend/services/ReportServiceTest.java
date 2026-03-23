package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.entity.Analytics;
import com.socialsync.socialsyncbackend.entity.PlatformType;
import com.socialsync.socialsyncbackend.entity.PostStatus;
import com.socialsync.socialsyncbackend.entity.ScheduledPost;
import com.socialsync.socialsyncbackend.entity.SocialMediaAccount;
import com.socialsync.socialsyncbackend.entity.User;
import com.socialsync.socialsyncbackend.repositories.AnalyticsRepository;
import com.socialsync.socialsyncbackend.repositories.ScheduledPostRepository;
import com.socialsync.socialsyncbackend.repositories.SocialMediaAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private SocialMediaAccountRepository accountRepository;

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private ScheduledPostRepository postRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void generateCsvReport_shouldIncludeAnalyticsAndPostSections() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 31, 23, 59);

        SocialMediaAccount account = SocialMediaAccount.builder()
                .id(11L)
                .user(User.builder().id(userId).build())
                .platform(PlatformType.INSTAGRAM)
                .accountName("Insta Biz")
                .build();

        Analytics analytics = Analytics.builder()
                .account(account)
                .recordedAt(LocalDateTime.of(2026, 1, 10, 10, 0))
                .followers(100)
                .likes(50)
                .comments(10)
                .shares(5)
                .impressions(500)
                .engagementRate(6.5)
                .build();

        ScheduledPost post = ScheduledPost.builder()
                .id(100L)
                .user(User.builder().id(userId).build())
                .account(account)
                .content("January Campaign")
                .scheduledTime(LocalDateTime.of(2026, 1, 20, 14, 0))
                .status(PostStatus.PENDING)
                .build();

        when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));
        when(analyticsRepository.findByAccountIdAndRecordedAtBetween(11L, start, end)).thenReturn(List.of(analytics));
        when(postRepository.findByUserIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(userId, start, end))
                .thenReturn(List.of(post));

        byte[] bytes = reportService.generateCsvReport(userId, start, end);
        String csv = new String(bytes, StandardCharsets.UTF_8);

        assertTrue(csv.contains("Report Type,Platform,Account Name"));
        assertTrue(csv.contains("Analytics,\"INSTAGRAM\",\"Insta Biz\""));
        assertTrue(csv.contains("Scheduled Post,\"INSTAGRAM\""));
        assertTrue(csv.contains("\"January Campaign\""));
    }
}
