package com.socialsync.socialsyncbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectAccountRequest {
    @NotNull(message = "Platform is required")
    private String platform;

    @NotBlank(message = "Access token is required")
    private String accessToken;

    private String refreshToken;
    private String accountId;
    private String accountName;
}
