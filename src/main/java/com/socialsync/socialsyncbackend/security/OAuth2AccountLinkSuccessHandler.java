package com.socialsync.socialsyncbackend.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.socialsync.socialsyncbackend.controllers.OAuth2ConnectController;
import com.socialsync.socialsyncbackend.dto.ConnectAccountRequest;
import com.socialsync.socialsyncbackend.services.SocialMediaAccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AccountLinkSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final SocialMediaAccountService accountService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${app.oauth.mobile-callback-url:socialsync://oauth-callback}")
    private String mobileCallbackUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            String jwt = getLinkJwt(request);
            if (jwt == null) {
                redirectWithError(response, "Missing linking token");
                return;
            }

            Long userId = jwtUtil.extractUserId(jwt);

            if (!(authentication instanceof OAuth2AuthenticationToken oauth2Token)) {
                redirectWithError(response, "Invalid OAuth2 authentication");
                return;
            }

            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
            OAuth2AuthorizedClient client = authorizedClientService
                    .loadAuthorizedClient(registrationId, oauth2Token.getName());

            if (client == null || client.getAccessToken() == null) {
                redirectWithError(response, "Could not fetch access token");
                return;
            }

            OAuth2User oauth2User = oauth2Token.getPrincipal();
            if (oauth2User == null) {
                redirectWithError(response, "Could not fetch user profile");
                return;
            }
            Map<String, Object> attributes = oauth2User.getAttributes();

            String accountId = readFirst(attributes, "id", "sub");
            String accountName = readFirst(attributes, "name", "username", "preferred_username");
            OAuth2RefreshToken refreshToken = client.getRefreshToken();

            ConnectAccountRequest connectAccountRequest = new ConnectAccountRequest();
            connectAccountRequest.setPlatform(registrationId.toUpperCase());
            connectAccountRequest.setAccessToken(client.getAccessToken().getTokenValue());
            connectAccountRequest.setRefreshToken(refreshToken != null ? refreshToken.getTokenValue() : null);
            connectAccountRequest.setAccountId(accountId);
            connectAccountRequest.setAccountName(accountName);

            accountService.connectAccount(userId, connectAccountRequest);

            clearLinkCookie(response);
            String successRedirect = UriComponentsBuilder
                    .fromUriString(mobileCallbackUrl)
                    .queryParam("status", "success")
                    .queryParam("platform", registrationId)
                    .queryParam("accountId", accountId)
                    .build(true)
                    .toUriString();

            response.sendRedirect(successRedirect);
        } catch (RuntimeException ex) {
            redirectWithError(response, ex.getMessage());
        }
    }

    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        clearLinkCookie(response);
        String errorRedirect = UriComponentsBuilder
                .fromUriString(mobileCallbackUrl)
                .queryParam("status", "error")
                .queryParam("message", message != null ? message : "OAuth2 account linking failed")
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(errorRedirect);
    }

    private String getLinkJwt(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (OAuth2ConnectController.LINK_JWT_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void clearLinkCookie(HttpServletResponse response) {
        ResponseCookie expiredCookie = ResponseCookie.from(OAuth2ConnectController.LINK_JWT_COOKIE, "")
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
    }

    private String readFirst(Map<String, Object> attributes, String... keys) {
        for (String key : keys) {
            Object value = attributes.get(key);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }
}
