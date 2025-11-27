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
public class SocialAccountResponse {
    private Long id;
    private String platform;
    private String accountName;
    private String accountId;
    private Boolean isActive;
    private LocalDateTime connectedAt;
}
