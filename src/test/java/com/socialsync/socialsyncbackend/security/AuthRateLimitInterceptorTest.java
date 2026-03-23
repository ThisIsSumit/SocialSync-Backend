package com.socialsync.socialsyncbackend.security;

import com.socialsync.socialsyncbackend.exception.RateLimitExceededException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthRateLimitInterceptorTest {

    @Test
    void preHandle_shouldAllowWithinLimit() {
        AuthRateLimitInterceptor interceptor = new AuthRateLimitInterceptor(2, 2);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandle_shouldBlockAfterLimitExceeded() {
        AuthRateLimitInterceptor interceptor = new AuthRateLimitInterceptor(1, 1);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
        assertThrows(RateLimitExceededException.class,
                () -> interceptor.preHandle(request, response, new Object()));
    }
}
