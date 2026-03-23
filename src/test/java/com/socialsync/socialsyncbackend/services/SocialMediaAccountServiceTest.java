package com.socialsync.socialsyncbackend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.socialsync.socialsyncbackend.dto.ConnectAccountRequest;
import com.socialsync.socialsyncbackend.dto.SocialAccountResponse;
import com.socialsync.socialsyncbackend.entity.PlatformType;
import com.socialsync.socialsyncbackend.entity.SocialMediaAccount;
import com.socialsync.socialsyncbackend.entity.User;

@ExtendWith(MockitoExtension.class)
class SocialMediaAccountServiceTest {

    @Mock
    private com.socialsync.socialsyncbackend.repositories.SocialMediaAccountRepository accountRepository;

    @Mock
    private com.socialsync.socialsyncbackend.repositories.UserRepository userRepository;

    @InjectMocks
    private SocialMediaAccountService socialMediaAccountService;

    @Test
    void connectAccount_shouldPersistAndReturnResponse() {
        Long userId = 3L;
        User user = User.builder().id(userId).email("user@example.com").build();
        ConnectAccountRequest request = new ConnectAccountRequest("instagram", "access", "refresh", "acc-1", "My Account");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(SocialMediaAccount.class))).thenAnswer(invocation -> {
            SocialMediaAccount account = invocation.getArgument(0);
            account.setId(50L);
            account.setConnectedAt(LocalDateTime.of(2026, 3, 23, 12, 0));
            return account;
        });

        SocialAccountResponse response = socialMediaAccountService.connectAccount(userId, request);

        ArgumentCaptor<SocialMediaAccount> captor = ArgumentCaptor.forClass(SocialMediaAccount.class);
        verify(accountRepository).save(captor.capture());
        SocialMediaAccount saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(PlatformType.INSTAGRAM, saved.getPlatform());
        assertEquals("acc-1", saved.getAccountId());
        assertEquals("My Account", saved.getAccountName());
        assertEquals("access", saved.getAccessToken());
        assertEquals("refresh", saved.getRefreshToken());
        assertEquals(Boolean.TRUE, saved.getIsActive());

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals("INSTAGRAM", response.getPlatform());
        assertEquals("acc-1", response.getAccountId());
        assertEquals("My Account", response.getAccountName());
        assertEquals(Boolean.TRUE, response.getIsActive());
    }

    @Test
    void connectAccount_shouldThrowWhenUserMissing() {
        Long userId = 404L;
        ConnectAccountRequest request = new ConnectAccountRequest("instagram", "access", "refresh", "acc-1", "My Account");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> socialMediaAccountService.connectAccount(userId, request));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getConnectedAccounts_shouldMapEntityList() {
        Long userId = 3L;
        SocialMediaAccount a1 = SocialMediaAccount.builder()
                .id(1L)
                .platform(PlatformType.FACEBOOK)
                .accountId("fb-1")
                .accountName("FB Account")
                .isActive(true)
                .connectedAt(LocalDateTime.of(2026, 1, 1, 9, 0))
                .build();

        when(accountRepository.findByUserId(userId)).thenReturn(List.of(a1));

        List<SocialAccountResponse> responses = socialMediaAccountService.getConnectedAccounts(userId);

        assertEquals(1, responses.size());
        assertEquals("FACEBOOK", responses.get(0).getPlatform());
        assertEquals("fb-1", responses.get(0).getAccountId());
        assertEquals("FB Account", responses.get(0).getAccountName());
    }

    @Test
    void disconnectAccount_shouldDeleteWhenFound() {
        Long userId = 3L;
        Long accountId = 10L;
        SocialMediaAccount account = SocialMediaAccount.builder().id(accountId).build();

        when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(account));

        socialMediaAccountService.disconnectAccount(userId, accountId);

        verify(accountRepository).delete(account);
    }

    @Test
    void disconnectAccount_shouldThrowWhenMissing() {
        Long userId = 3L;
        Long accountId = 10L;

        when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> socialMediaAccountService.disconnectAccount(userId, accountId));

        assertEquals("Account not found", ex.getMessage());
    }
}
