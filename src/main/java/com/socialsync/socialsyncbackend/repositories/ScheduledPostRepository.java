package com.socialsync.socialsyncbackend.repositories;

import com.socialsync.socialsyncbackend.entity.PostStatus;
import com.socialsync.socialsyncbackend.entity.ScheduledPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledPostRepository extends JpaRepository<ScheduledPost, Long> {
    List<ScheduledPost> findByUserId(Long userId);

    List<ScheduledPost> findByStatusAndScheduledTimeBefore(PostStatus status, LocalDateTime time);

    Optional<ScheduledPost> findByIdAndUserId(Long id, Long userId);
}
