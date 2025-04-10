package com.zerobase.challengeproject.member.service.oauthUserInfo;

import java.util.Map;
/**
 * OAuth2 인증 제공자에 따라 적절한 {@link OAuth2UserInfo} 구현체를 생성하는 팩토리 클래스입니다.
 * Google, Kakao, Naver 등의 다양한 소셜 로그인 제공자에 대해
 * 각각의 사용자 정보 파싱 클래스를 반환합니다.
 */
public class OAuth2UserInfoFactory {
    /**
     * 주어진 provider 이름에 따라 해당 OAuth2 제공자에 맞는 {@link OAuth2UserInfo} 구현체를 반환합니다.
     *
     * @param provider   OAuth2 인증 제공자 이름 (예: "google", "kakao", "naver")
     * @param attributes OAuth2 인증 과정에서 제공된 사용자 속성 정보 Map
     * @return 해당 제공자에 맞는 {@link OAuth2UserInfo} 구현체
     * @throws IllegalArgumentException 지원하지 않는 provider일 경우 예외 발생
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        return switch (provider.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자: " + provider);
        };
    }
}
