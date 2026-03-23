package com.socialsync.socialsyncbackend.security;

import com.socialsync.socialsyncbackend.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthRateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int loginLimit;
    private final int signupLimit;

    public AuthRateLimitInterceptor(
            @Value("${app.rate-limit.auth.login-per-minute:10}") int loginLimit,
            @Value("${app.rate-limit.auth.signup-per-hour:20}") int signupLimit) {
        this.loginLimit = loginLimit;
        this.signupLimit = signupLimit;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (!path.startsWith("/api/auth")) {
            return true;
        }

        String clientKey = resolveClientKey(request);
        Bucket bucket = getBucket(clientKey, path);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }

        return true;
    }

    private Bucket getBucket(String clientKey, String path) {
        String key = clientKey + ":" + path;
        return buckets.computeIfAbsent(key, k -> {
            if (path.contains("/signup")) {
                return Bucket.builder()
                        .addLimit(Bandwidth.classic(signupLimit, Refill.greedy(signupLimit, Duration.ofHours(1))))
                        .build();
            }
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(loginLimit, Refill.greedy(loginLimit, Duration.ofMinutes(1))))
                    .build();
        });
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
