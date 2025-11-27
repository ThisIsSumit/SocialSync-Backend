package com.socialsync.socialsyncbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Long accountId;
    private String platform;
    private String accountName;
    private Integer followers;
    private Integer totalLikes;
    private Integer totalComments;
    private Integer totalShares;
    private Integer totalImpressions;
    private Double engagementRate;
    private LocalDateTime lastUpdated;
}
