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
public class MfaChallengeResponse {
    private String message;
    private LocalDateTime expiresAt;
}
