package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;
import com.zerobase.challengeproject.member.domain.dto.MemberLoginResponse;
import com.zerobase.challengeproject.member.domain.dto.MemberLogoutDto;
import com.zerobase.challengeproject.member.domain.dto.RefreshTokenDto;
import com.zerobase.challengeproject.member.domain.form.MemberLoginForm;
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

import static org.junit.jupiter.api.Assertions.*;
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
    private RefreshToken existingRefreshToken;
    private final String mockAccessToken = "mockAccessToken";
    private final String mockRefreshToken = "mockRefreshToken";

    @BeforeEach
    void setUp() {
        // Mock Member 생성
        mockMember = Member.builder()
                .id(1L)
                .memberId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .password("encodedPassword")
                .memberType(MemberType.USER)
                .build();

        // 기존 리프레시 토큰 (삭제될 토큰)
        existingRefreshToken = RefreshToken.builder()
                .id(1L)
                .token(mockRefreshToken)
                .expireDate(Instant.now().plusSeconds(60 * 60 * 24 * 7))
                .memberId(mockMember.getMemberId())
                .build();
    }

    @Test
    @DisplayName("로그인 성공 - 기존 리프레시 토큰 삭제 후 새로운 토큰 발급")
    void login() {
        // given
        MemberLoginForm loginForm = MemberLoginForm.builder()
                .memberId("testId")
                .password("testPassword1!")
                .build();
        when(memberRepository.findByMemberId(loginForm.getMemberId())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(loginForm.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtUtil.generateAccessToken(loginForm.getMemberId(), mockMember.getMemberType())).thenReturn(mockAccessToken);
        when(jwtUtil.generateRefreshToken(loginForm.getMemberId(), mockMember.getMemberType())).thenReturn(mockRefreshToken);
        when(refreshTokenRepository.findByMemberId(loginForm.getMemberId())).thenReturn(Optional.of(existingRefreshToken));

        // when
        MemberLoginResponse result = memberLoginService.login(loginForm);

        // then
        assertNotNull(result);
        assertEquals(mockAccessToken, result.getAccessToken());
        assertEquals(mockMember.getMemberId(), result.getMemberId());
        verify(refreshTokenRepository, times(1)).deleteByMemberId(existingRefreshToken.getMemberId());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 회원")
    void loginFailure1() {
        // given
        MemberLoginForm loginForm = MemberLoginForm.builder()
                .memberId("testId")
                .password("testPassword1!")
                .build();
        when(memberRepository.findByMemberId(loginForm.getMemberId())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> memberLoginService.login(loginForm));
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFailure2() {
        MemberLoginForm loginForm = MemberLoginForm.builder()
                .memberId("testId")
                .password("testPassword1!")
                .build();
        // given
        when(memberRepository.findByMemberId(loginForm.getMemberId())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(loginForm.getPassword(), mockMember.getPassword())).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> memberLoginService.login(loginForm));
        assertEquals(ErrorCode.INCORRECT_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 성공 - 리프레시 토큰이 들어있는 쿠키 삭제")
    public void logout() {
        //given
        String accessToken = "Bearer " + mockAccessToken;

        when(jwtUtil.extractMemberId(mockAccessToken)).thenReturn(mockMember.getMemberId());
        when(jwtUtil.isTokenValid(mockRefreshToken)).thenReturn(true);
        //when
        MemberLogoutDto result = memberLoginService.logout(accessToken, mockRefreshToken);
        //then
        assertNotNull(result);
        assertEquals(mockMember.getMemberId(), result.getMemberId());
        verify(refreshTokenRepository, times(1)).deleteByMemberId(mockMember.getMemberId());
    }

    @Test
    @DisplayName("로그아웃 실패 - 토큰이 제공 되지 않음")
    void logoutFailure() {
        // Arrange
        String invalidToken = null;

        // Act & Assert
        assertThrows(CustomException.class, () -> memberLoginService.logout(mockAccessToken, invalidToken));
    }

    @Test
    @DisplayName("엑세스 토큰 발급 성공 - 리프레시 토큰으로 엑세스 토큰 재발급")
    void refreshAccessToken() {
        //given
        String newAccessToken = "newAccessToken";
        when(jwtUtil.isTokenValid(mockRefreshToken)).thenReturn(true);
        when(jwtUtil.extractMemberId(mockRefreshToken)).thenReturn(mockMember.getMemberId());
        when(memberRepository.findByMemberId(mockMember.getMemberId())).thenReturn(Optional.of(mockMember));
        when(refreshTokenRepository.findByToken(mockRefreshToken)).thenReturn(Optional.of(existingRefreshToken));
        when(jwtUtil.generateAccessToken(mockMember.getMemberId(), mockMember.getMemberType())).thenReturn(newAccessToken);
        //when
        RefreshTokenDto result = memberLoginService.refreshAccessToken(mockRefreshToken);
        //then
        assertNotNull(result);
        assertEquals(newAccessToken, result.getAccessToken());
    }
    @Test
    @DisplayName("토큰 재발급 - 토큰이 유효하지 않음")
    void refreshAccessTokenFailure() {
        // given
        when(jwtUtil.isTokenValid(existingRefreshToken.getToken())).thenReturn(false);

        // when & then
        assertThrows(CustomException.class, () -> memberLoginService.refreshAccessToken(existingRefreshToken.getToken()));
    }
}