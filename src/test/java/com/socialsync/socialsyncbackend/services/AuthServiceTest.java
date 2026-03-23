package com.socialsync.socialsyncbackend.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.socialsync.socialsyncbackend.dto.AuthResponse;
import com.socialsync.socialsyncbackend.dto.LoginRequest;
import com.socialsync.socialsyncbackend.dto.SignUpRequest;
import com.socialsync.socialsyncbackend.entity.User;
import com.socialsync.socialsyncbackend.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private com.socialsync.socialsyncbackend.repositories.UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void signUp_shouldCreateUserAndReturnAuthResponse() {
        SignUpRequest request = new SignUpRequest("test@example.com", "Password123", "John", "Doe", "9999999999");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-pass");
        when(jwtUtil.generateToken(anyString(), any(Long.class))).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        AuthResponse response = authService.signUp(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encoded-pass", savedUser.getPassword());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("9999999999", savedUser.getPhoneNumber());
        assertEquals(Boolean.FALSE, savedUser.getEmailVerified());
        assertEquals(Boolean.FALSE, savedUser.getMfaEnabled());

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void signUp_shouldThrowWhenEmailAlreadyExists() {
        SignUpRequest request = new SignUpRequest("exists@example.com", "Password123", "Jane", "Doe", "12345");
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.signUp(request));

        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void login_shouldAuthenticateAndReturnAuthResponse() {
        LoginRequest request = new LoginRequest("john@example.com", "Password123");
        User user = User.builder()
                .id(42L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Smith")
                .password("encoded")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(org.mockito.Mockito.mock(Authentication.class));
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("john@example.com", 42L)).thenReturn("token-42");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token-42", response.getToken());
        assertEquals(42L, response.getUserId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals("Smith", response.getLastName());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest("missing@example.com", "Password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(org.mockito.Mockito.mock(Authentication.class));
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));

        assertEquals("User not found", ex.getMessage());
    }
}
