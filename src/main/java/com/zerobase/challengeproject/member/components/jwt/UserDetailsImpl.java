package com.zerobase.challengeproject.member.components.jwt;

import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
/**
 * 사용자 인증 정보를 나타내는 클래스.
 * Spring Security의 UserDetails를 구현하여 회원 정보를 보유.
 */
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    /** 회원 객체 (인증 대상) */
    private final Member member;
    /**
     * 회원 정보를 반환하는 메서드
     * @return 회원 객체
     */
    public Member getMember() {
        return member;
    }
    /**
     * 사용자의 권한을 반환하는 메서드
     * @return 사용자의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        MemberType type = member.getMemberType();
        String authority = type.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }
    /**
     * 사용자의 비밀번호를 반환하는 메서드
     * @return 비밀번호
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }
    /**
     * 사용자의 아이디(고유 식별자)를 반환하는 메서드
     * @return 사용자 아이디
     */
    @Override
    public String getUsername() {
        return member.getMemberId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
