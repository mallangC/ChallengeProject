package com.zerobase.challengeproject.member.service;

import com.github.javafaker.Faker;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.MailComponents;
import com.zerobase.challengeproject.member.components.jwt.JwtUtil;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberEmailAuthDto;
import com.zerobase.challengeproject.member.domain.dto.MemberSignupDto;
import com.zerobase.challengeproject.member.domain.form.MemberSignupForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.member.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberSignupServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private MailComponents mailComponents;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private MemberSignupService memberService;

  private Faker faker;

  @BeforeEach
  void setUp() {
    faker = new Faker();
  }

  @Test
  @DisplayName("회원 가입 성공")
  void signup() {
    //given
    MemberSignupForm memberSignupForm = MemberSignupForm.builder()
            .loginId(faker.name().username())
            .memberName(faker.name().username())
            .nickname(faker.funnyName().name())
            .email(faker.internet().emailAddress())
            .phoneNum("010" + faker.number().digits(8))
            .password("testtest1!")
            .confirmPassword("testtest1!")
            .build();

    when(memberRepository.existsByEmail(memberSignupForm.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(memberSignupForm.getPassword())).thenReturn("encodePassword!1");

    //when
    MemberSignupDto dto = memberService.signup(memberSignupForm);

    //then
    verify(memberRepository, times(1)).save(any(Member.class));
    verify(mailComponents, times(1)).send(eq(memberSignupForm.getEmail()), anyString(), anyString());
    verify(passwordEncoder, times(1)).encode(memberSignupForm.getPassword());
    assertNotNull(dto);
    assertEquals(dto.getLoginId(), memberSignupForm.getLoginId());
    assertEquals(dto.getMemberName(), memberSignupForm.getMemberName());
    assertEquals(dto.getEmail(), memberSignupForm.getEmail());
    assertEquals(dto.getPhoneNumber(), memberSignupForm.getPhoneNum());
  }


  @Test
  @DisplayName("이메일 인증 성공")
  void verifyEmail() {
    // given
    String emailAuthKey = "valid-key";
    Member member = Member.builder()
            .isEmailVerified(false)
            .emailAuthKey(emailAuthKey)
            .build();
    when(memberRepository.findByEmailAuthKey(emailAuthKey)).thenReturn(Optional.of(member));

    // when
    MemberEmailAuthDto authDto = memberService.verifyEmail(emailAuthKey);

    // then
    assertTrue(member.isEmailVerified());
    assertNotNull(authDto);
  }

  @Test
  @DisplayName("이메일 인증 실패 - 이미 인증된 메일 ")
  void verifyEmailFailure1() {
    // given
    String emailAuthKey = "verified-key";
    Member member = Member.builder()
            .isEmailVerified(true)
            .emailAuthKey(emailAuthKey)
            .build();
    when(memberRepository.findByEmailAuthKey(emailAuthKey)).thenReturn(Optional.of(member));

    // when & then
    CustomException exception = assertThrows(CustomException.class, () -> {
      memberService.verifyEmail(emailAuthKey);
    });
    assertEquals(ErrorCode.ALREADY_VERIFY_EMAIL, exception.getErrorCode());
  }

  @Test
  @DisplayName("이메일 인증 실패 - 유저를 찾을 수 없음")
  void verifyEmailFailure2() {
    // given
    String emailAuthKey = "nonexistent-key";
    when(memberRepository.findByEmailAuthKey(emailAuthKey)).thenReturn(Optional.empty());

    // when & then
    CustomException exception = assertThrows(CustomException.class, () -> {
      memberService.verifyEmail(emailAuthKey);
    });
    assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원 탈퇴 - 정상 동작")
  void unregister() {
    // given
    Member member = Member.builder()
            .loginId("testId")
            .memberName("testName")
            .nickname("testNickname")
            .email("testEmail@email.com")
            .phoneNumber("01011112222")
            .build();
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    when(userDetails.getUsername()).thenReturn(member.getLoginId());
    ResponseCookie mockCookie = ResponseCookie.from("refreshToken", "")
            .maxAge(0)
            .build();
    when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));
    when(jwtUtil.createRefreshTokenCookie("", 0)).thenReturn(mockCookie);

    // when
    ResponseCookie responseCookie = memberService.unregister(userDetails.getUsername());

    // Then
    assertNotNull(responseCookie);
    assertEquals("", responseCookie.getValue());
    assertEquals(0, responseCookie.getMaxAge().getSeconds());
    verify(refreshTokenRepository, times(1)).deleteByLoginId(member.getLoginId());
    verify(memberRepository, times(1)).delete(member);
  }

  @Test
  @DisplayName("회원 탈퇴 - 회원이 존재하지 않을 경우 예외 발생")
  void unregisterFailure() {
    //given
    String memberId = "nonExistentUser";
    UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
    when(userDetails.getUsername()).thenReturn(memberId);
    when(memberRepository.findByLoginId(memberId)).thenReturn(Optional.empty());

    //when & then
    CustomException exception = assertThrows(CustomException.class, () -> {
      memberService.unregister(userDetails.getUsername());
    });

    assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    verify(refreshTokenRepository, never()).deleteByLoginId(any());
    verify(memberRepository, never()).delete(any());
  }
}