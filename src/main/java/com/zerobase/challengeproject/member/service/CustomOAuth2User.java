package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.MemberType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 인증을 통해 로그인한 사용자의 정보를 담는 커스텀 구현체입니다.
 * Spring Security의 {@link OAuth2User}를 구현하여, 사용자 속성과 권한, 이름 등을 제공합니다.
 * 내부적으로 {@link Member} 객체를 포함하고 있으며, 사용자 로그인 ID를 기준으로 이름을 반환합니다.
 */
public class CustomOAuth2User implements OAuth2User {

    @Getter
    private final Member member;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }
    /**
     * OAuth2 공급자로부터 받은 사용자 속성 정보를 반환합니다.
     *
     * @return 사용자 속성 정보 Map
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    /**
     * 사용자의 권한 정보를 반환합니다.
     * 현재는 기본적으로 MemberType.USER 권한을 부여합니다.
     *
     * @return 사용자 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(MemberType.USER.getAuthority()
        ));
    }

    @Override
    public String getName() {
        return member.getLoginId();
    }
}
