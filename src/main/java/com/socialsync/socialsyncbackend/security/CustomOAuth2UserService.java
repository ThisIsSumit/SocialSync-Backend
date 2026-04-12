package com.socialsync.socialsyncbackend.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (!"twitter".equals(registrationId)) {
            return oauth2User;
        }

        Map<String, Object> attrs = new HashMap<>(oauth2User.getAttributes());
        Object data = attrs.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
                if (entry.getKey() instanceof String key) {
                    attrs.put(key, entry.getValue());
                }
            }
        }

        Collection<? extends GrantedAuthority> authorities = oauth2User.getAuthorities();
        return new DefaultOAuth2User(authorities, attrs, "id");
    }
}
