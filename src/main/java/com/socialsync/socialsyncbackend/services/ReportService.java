package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.entity.Analytics;
import com.socialsync.socialsyncbackend.entity.ScheduledPost;
import com.socialsync.socialsyncbackend.entity.SocialMediaAccount;
import com.socialsync.socialsyncbackend.repositories.AnalyticsRepository;
import com.socialsync.socialsyncbackend.repositories.ScheduledPostRepository;
import com.socialsync.socialsyncbackend.repositories.SocialMediaAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SocialMediaAccountRepository accountRepository;
    private final AnalyticsRepository analyticsRepository;
    private final ScheduledPostRepository postRepository;

    public byte[] generateCsvReport(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<SocialMediaAccount> accounts = accountRepository.findByUserId(userId);
        List<ScheduledPost> posts = postRepository.findByUserIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(
                userId, startDate, endDate
        );

        StringBuilder csv = new StringBuilder();
        csv.append("Report Type,Platform,Account Name,Recorded At,Followers,Likes,Comments,Shares,Impressions,Engagement Rate\n");

        for (SocialMediaAccount account : accounts) {
            List<Analytics> analytics = analyticsRepository.findByAccountIdAndRecordedAtBetween(
                    account.getId(), startDate, endDate
            );

            if (analytics.isEmpty()) {
                csv.append("Analytics,")
                        .append(escape(account.getPlatform().name())).append(",")
                        .append(escape(account.getAccountName())).append(",")
                        .append(",0,0,0,0,0,0.0\n");
                continue;
            }

            for (Analytics entry : analytics) {
                csv.append("Analytics,")
                        .append(escape(account.getPlatform().name())).append(",")
                        .append(escape(account.getAccountName())).append(",")
                        .append(entry.getRecordedAt()).append(",")
                        .append(defaultInt(entry.getFollowers())).append(",")
                        .append(defaultInt(entry.getLikes())).append(",")
                        .append(defaultInt(entry.getComments())).append(",")
                        .append(defaultInt(entry.getShares())).append(",")
                        .append(defaultInt(entry.getImpressions())).append(",")
                        .append(defaultDouble(entry.getEngagementRate())).append("\n");
            }
        }

        csv.append("\nReport Type,Platform,Scheduled Time,Status,Content\n");
        for (ScheduledPost post : posts) {
            csv.append("Scheduled Post,")
                    .append(escape(post.getAccount().getPlatform().name())).append(",")
                    .append(post.getScheduledTime()).append(",")
                    .append(post.getStatus().name()).append(",")
                    .append(escape(post.getContent())).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double defaultDouble(Double value) {
        return value == null ? 0.0 : value;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
