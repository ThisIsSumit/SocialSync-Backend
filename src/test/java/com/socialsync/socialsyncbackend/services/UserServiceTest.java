package com.socialsync.socialsyncbackend.services;

import com.socialsync.socialsyncbackend.dto.ProfileUpdateRequest;
import com.socialsync.socialsyncbackend.entity.User;
import com.socialsync.socialsyncbackend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void updateProfile_shouldUpdateNotificationPreferences() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .firstName("Old")
                .emailNotifications(true)
                .pushNotifications(true)
                .build();

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                "New",
                "Name",
                "1111111111",
                false,
                true
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateProfile(userId, request);

        assertEquals("New", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        assertEquals("1111111111", updated.getPhoneNumber());
        assertEquals(false, updated.getEmailNotifications());
        assertEquals(true, updated.getPushNotifications());
    }
}
