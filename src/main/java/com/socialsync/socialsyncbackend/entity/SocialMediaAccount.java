package com.socialsync.socialsyncbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_media_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialMediaAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformType platform;

    @Column(nullable = false)
    private String accountId;

    private String accountName;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime tokenExpiresAt;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime connectedAt = LocalDateTime.now();
}
