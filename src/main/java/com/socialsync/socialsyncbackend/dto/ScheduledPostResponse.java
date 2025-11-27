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
public class ScheduledPostResponse {
    private Long id;
    private Long accountId;
    private String platform;
    private String content;
    private String mediaUrl;
    private LocalDateTime scheduledTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
