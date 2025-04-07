package com.zerobase.challengeproject.member.service.oauthUserInfo;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 Google OAuth2 로그인 후 사용자 정보를 처리하는 클래스입니다.
 {@link OAuth2UserInfo} 인터페이스를 구현하여, Google로부터 받은 사용자 정보 Map을 기반으로
 provider, providerId, 이메일, 이름을 추출합니다.

 */
@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo{
    /**
     * Google OAuth2 인증 서버로부터 전달받은 사용자 정보 Map입니다.
     */
    private final Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
