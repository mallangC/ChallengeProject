package com.zerobase.challengeproject.member.service.oauthUserInfo;
/**
 OAuth2 인증 제공자로부터 받은 사용자 정보를 추상화한 인터페이스입니다.
 다양한 소셜 로그인(Google, Kakao, Naver 등) 공급자마다 사용자 정보 구조가 다르기 때문에,
 이 인터페이스를 통해 공통된 방식으로 사용자 정보를 다룰 수 있도록 합니다.
 각 소셜 제공자별로 이 인터페이스를 구현한 클래스를 통해 사용자 정보를 추출합니다.
 */
public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
