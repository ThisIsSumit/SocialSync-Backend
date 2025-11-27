package com.socialsync.socialsyncbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private SocialMediaAccount account;

    @Column(nullable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();

    private Integer followers;
    private Integer likes;
    private Integer comments;
    private Integer shares;
    private Integer impressions;
    private Integer reach;
    private Double engagementRate;
}
