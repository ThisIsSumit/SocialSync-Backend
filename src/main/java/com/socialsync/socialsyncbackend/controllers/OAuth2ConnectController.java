package com.socialsync.socialsyncbackend.controllers;

import com.socialsync.socialsyncbackend.entity.PlatformType;
import com.socialsync.socialsyncbackend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Locale;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "OAuth2 Social Connect", description = "Start provider OAuth2 flow to link social media accounts")
public class OAuth2ConnectController {

    public static final String LINK_JWT_COOKIE = "SOCIALSYNC_LINK_JWT";

    private final JwtUtil jwtUtil;

    @GetMapping("/authorization/{provider}")
    @Operation(summary = "Start OAuth2 account linking flow for a provider")
    public ResponseEntity<Void> startAuthorization(
            @RequestHeader("Authorization") String token,
            @PathVariable String provider,
            HttpServletResponse response) {

        String jwt = token.substring(7);
        jwtUtil.extractUserId(jwt);

        PlatformType.valueOf(provider.toUpperCase(Locale.ROOT));

        ResponseCookie linkCookie = ResponseCookie.from(LINK_JWT_COOKIE, jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(300)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, linkCookie.toString());

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/" + provider.toLowerCase(Locale.ROOT)))
                .build();
    }
}
