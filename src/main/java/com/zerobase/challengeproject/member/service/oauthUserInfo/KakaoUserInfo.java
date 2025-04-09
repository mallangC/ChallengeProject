package com.zerobase.challengeproject.member.service.oauthUserInfo;

import java.util.Map;
/**
 Kakao OAuth2 로그인 후 사용자 정보를 처리하는 클래스입니다.
 {@link OAuth2UserInfo} 인터페이스를 구현하여, 카카오에서 제공하는 사용자 정보 Map을 기반으로
 provider, providerId, 이메일, 닉네임, 실명 등을 추출합니다.
 </p>
 */
public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString(); // 카카오 사용자 ID
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }
    /**
     * 사용자의 실명을 반환합니다.
     * 실명은 "kakao_account.profile.name" 필드에 포함되어 있으며,
     * 해당 정보가 없는 경우 null을 반환합니다.
     *
     * @return 사용자 실명 또는 null
     */
    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        return profile != null ? (String) profile.get("name") : null;
    }

    public String getNickName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties != null ? (String) properties.get("nickname") : null;
    }
}
