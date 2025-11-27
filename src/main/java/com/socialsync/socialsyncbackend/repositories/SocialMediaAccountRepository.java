package com.socialsync.socialsyncbackend.repositories;

import com.socialsync.socialsyncbackend.entity.SocialMediaAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialMediaAccountRepository extends JpaRepository<SocialMediaAccount, Long> {
    List<SocialMediaAccount> findByUserId(Long userId);

    Optional<SocialMediaAccount> findByIdAndUserId(Long id, Long userId);
}
