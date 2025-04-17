package com.zerobase.challengeproject.member.service;

import com.github.javafaker.Faker;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;
import com.zerobase.challengeproject.member.domain.dto.MemberLogoutDto;
import com.zerobase.challengeproject.member.domain.dto.RefreshTokenDto;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.entity.RefreshToken;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.member.repository.RefreshTokenRepository;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MemberLoginServiceTest {
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberLoginService memberLoginService;

    private Member mockMember;
    private Faker faker;
    private RefreshToken existingRefreshToken;
    private final String mockAccessToken = "mockAccessToken";
    private final String mockRefreshToken = "mockRefreshToken";

    @BeforeEach
    void setUp() {
        faker = new Faker();
        // Mock Member 생성
        mockMember = Member.builder()
                .id(1L)
                .loginId(faker.funnyName().name())
                .memberName(faker.name().fullName())
                .nickname(faker.funnyName().name())
                .email(faker.internet().emailAddress())
                .phoneNumber("010"+faker.number().digits(8))
                .password("testPassword")
                .memberType(MemberType.USER)
                .isBlackList(false)
                .build();

        // 기존 리프레시 토큰 (삭제될 토큰)
        existingRefreshToken = RefreshToken.builder()
                .id(1L)
                .token(mockRefreshToken)
                .expireDate(Instant.now().plusSeconds(60 * 60 * 24 * 7))
                .loginId(mockMember.getLoginId())
                .build();
    }

    @Test
    @DisplayName("로그아웃 성공 - 리프레시 토큰이 들어있는 쿠키 삭제")
    public void logout() {
        //given
        when(jwtUtil.extractLoginId(mockAccessToken)).thenReturn(mockMember.getLoginId());
        //when
        MemberLogoutDto result = memberLoginService.logout(mockAccessToken);
        //then
        assertNotNull(result);
        assertEquals(mockMember.getLoginId(), result.getLoginId());
        verify(refreshTokenRepository, times(1)).deleteByLoginId(mockMember.getLoginId());
    }

    @Test
    @DisplayName("엑세스 토큰 발급 성공 - 리프레시 토큰으로 엑세스 토큰 재발급")
    void refreshAccessToken() {
        //given
        String newAccessToken = "newAccessToken";
        when(jwtUtil.extractLoginId(mockRefreshToken)).thenReturn(mockMember.getLoginId());
        when(memberRepository.findByLoginId(mockMember.getLoginId())).thenReturn(Optional.of(mockMember));
        when(refreshTokenRepository.findByToken(mockRefreshToken)).thenReturn(Optional.of(existingRefreshToken));
        when(jwtUtil.generateAccessToken(mockMember.getLoginId(), mockMember.getMemberType())).thenReturn(newAccessToken);
        //when
        RefreshTokenDto result = memberLoginService.refreshAccessToken(mockRefreshToken);
        //then
        assertNotNull(result);
        assertEquals(newAccessToken, result.getAccessToken());
    }
}