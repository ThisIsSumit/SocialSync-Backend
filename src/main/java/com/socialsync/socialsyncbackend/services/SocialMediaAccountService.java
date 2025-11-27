package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.dto.*;
import com.socialsync.socialsyncbackend.entity.*;
import com.socialsync.socialsyncbackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialMediaAccountService {

    private final SocialMediaAccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public SocialAccountResponse connectAccount(Long userId, ConnectAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SocialMediaAccount account = SocialMediaAccount.builder()
                .user(user)
                .platform(PlatformType.valueOf(request.getPlatform().toUpperCase()))
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .accessToken(request.getAccessToken())
                .refreshToken(request.getRefreshToken())
                .isActive(true)
                .connectedAt(LocalDateTime.now())
                .build();

        account = accountRepository.save(account);

        return mapToResponse(account);
    }

    public List<SocialAccountResponse> getConnectedAccounts(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void disconnectAccount(Long userId, Long accountId) {
        SocialMediaAccount account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.delete(account);
    }

    private SocialAccountResponse mapToResponse(SocialMediaAccount account) {
        return SocialAccountResponse.builder()
                .id(account.getId())
                .platform(account.getPlatform().name())
                .accountName(account.getAccountName())
                .accountId(account.getAccountId())
                .isActive(account.getIsActive())
                .connectedAt(account.getConnectedAt())
                .build();
    }
}