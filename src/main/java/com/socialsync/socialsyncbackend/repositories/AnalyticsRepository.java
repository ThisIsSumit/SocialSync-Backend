package com.socialsync.socialsyncbackend.repositories;

import com.socialsync.socialsyncbackend.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    List<Analytics> findByAccountId(Long accountId);

    List<Analytics> findByAccountIdAndRecordedAtBetween(Long accountId, LocalDateTime start, LocalDateTime end);
}
