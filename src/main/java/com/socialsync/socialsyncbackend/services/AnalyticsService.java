package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.dto.AnalyticsResponse;
import com.socialsync.socialsyncbackend.entity.*;
import com.socialsync.socialsyncbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final SocialMediaAccountRepository accountRepository;

    public List<AnalyticsResponse> getAnalytics(Long userId) {
        List<SocialMediaAccount> accounts = accountRepository.findByUserId(userId);

        return accounts.stream()
                .map(this::getAccountAnalytics)
                .collect(Collectors.toList());
    }

    public AnalyticsResponse getAccountAnalytics(SocialMediaAccount account) {
        List<Analytics> analyticsList = analyticsRepository.findByAccountId(account.getId());

        if (analyticsList.isEmpty()) {
            return AnalyticsResponse.builder()
                    .accountId(account.getId())
                    .platform(account.getPlatform().name())
                    .accountName(account.getAccountName())
                    .followers(0)
                    .totalLikes(0)
                    .totalComments(0)
                    .totalShares(0)
                    .totalImpressions(0)
                    .engagementRate(0.0)
                    .build();
        }

        Analytics latest = analyticsList.get(analyticsList.size() - 1);

        int totalLikes = analyticsList.stream().mapToInt(Analytics::getLikes).sum();
        int totalComments = analyticsList.stream().mapToInt(Analytics::getComments).sum();
        int totalShares = analyticsList.stream().mapToInt(Analytics::getShares).sum();
        int totalImpressions = analyticsList.stream().mapToInt(Analytics::getImpressions).sum();

        return AnalyticsResponse.builder()
                .accountId(account.getId())
                .platform(account.getPlatform().name())
                .accountName(account.getAccountName())
                .followers(latest.getFollowers())
                .totalLikes(totalLikes)
                .totalComments(totalComments)
                .totalShares(totalShares)
                .totalImpressions(totalImpressions)
                .engagementRate(latest.getEngagementRate())
                .lastUpdated(latest.getRecordedAt())
                .build();
    }

    public byte[] generateReport(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for generating PDF/CSV reports
        // This would use libraries like Apache PDFBox or Apache POI
        return new byte[0];
    }
}
