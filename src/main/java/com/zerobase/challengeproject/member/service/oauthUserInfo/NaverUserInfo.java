package com.zerobase.challengeproject.member.service.oauthUserInfo;

import java.util.Map;

/**
 Naver OAuth2 로그인 후 사용자 정보를 처리하는 클래스입니다.
 {@link OAuth2UserInfo} 인터페이스를 구현하여, 네이버에서 제공하는 사용자 정보 Map을 기반으로
 provider, providerId, 이메일, 닉네임, 실명 등을 추출합니다.
 </p>
 */
public class NaverUserInfo implements OAuth2UserInfo{
    private final Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
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
