package com.socialsync.socialsyncbackend.security;

import com.socialsync.socialsyncbackend.controllers.OAuth2ConnectController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AccountLinkFailureHandler implements AuthenticationFailureHandler {

    @Value("${app.oauth.mobile-callback-url:socialsync://oauth-callback}")
    private String mobileCallbackUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        ResponseCookie expiredCookie = ResponseCookie.from(OAuth2ConnectController.LINK_JWT_COOKIE, "")
                .path("/")
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        String errorRedirect = UriComponentsBuilder
                .fromUriString(mobileCallbackUrl)
                .queryParam("status", "error")
                .queryParam("message", exception.getMessage() != null ? exception.getMessage() : "OAuth2 authentication failed")
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(errorRedirect);
    }
}
