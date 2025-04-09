package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberProfileDto;
import com.zerobase.challengeproject.member.domain.form.ChangePasswordForm;
import com.zerobase.challengeproject.member.domain.form.MemberProfileFrom;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 정보 조회 성공")
    void getProfile() {
        //given
        Member member = Member.builder()
                .memberId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        given(memberRepository.findByMemberId(userDetails.getUsername())).willReturn(Optional.of(member));
        //when
        MemberProfileDto memberProfileDto = memberService.getProfile(userDetails);
        //then
        assertNotNull(memberProfileDto);
        assertEquals(member.getMemberId(), memberProfileDto.getMemberId());
        assertEquals(member.getMemberName(), memberProfileDto.getMemberName());
    }

    @Test
    @DisplayName("회원 정보 조회 실패 - 존재하지 않는 유저")
    void getProfileFailure() {
        //given
        Member NotFoundMember = Member.builder()
                .memberId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(NotFoundMember);
        when(memberRepository.findByMemberId(NotFoundMember.getMemberId()))
                .thenReturn(Optional.empty());
        //when&then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.getProfile(userDetails));
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile() {
        // given
        Member member = Member.builder()
                .memberId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .build();
        MemberProfileFrom form = MemberProfileFrom.builder()
                .nickname("testNickname")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(member.getMemberId());
        when(memberRepository.findByMemberId(userDetails.getUsername())).thenReturn(Optional.of(member));
        // When
        MemberProfileDto result = memberService.updateProfile(userDetails, form);

        // Then
        assertNotNull(result);
        assertEquals(form.getPhoneNum(), result.getPhoneNum());
        assertEquals(form.getNickname(), result.getNickName());
        verify(memberRepository, times(1)).findByMemberId(member.getMemberId());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword() {
        // given
        Member member = Member.builder()
                .memberId("testId")
                .password("testPassword")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(member.getMemberId());
        ChangePasswordForm form = ChangePasswordForm.builder()
                .password("testPassword")
                .newPassword("newPassword")
                .newPasswordVerify("newPassword")
                .build();
        when(memberRepository.findByMemberId(userDetails.getUsername())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("testPassword", member.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword", member.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodePassword");

        // When
        String result = memberService.changePassword(userDetails, form);

        // Then
        assertEquals(member.getMemberId(), result);
        verify(passwordEncoder, times(1)).matches("testPassword", "testPassword");
        verify(passwordEncoder, times(1)).matches("newPassword", "testPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호가 일치하지 않는 경우 예외 발생")
    void changePasswordFailure() {
        // Given
        Member member = Member.builder()
                .memberId("testId")
                .password("testPassword")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(member.getMemberId());
        ChangePasswordForm form = ChangePasswordForm.builder()
                .password("wrongPassword")
                .newPassword("newPassword")
                .newPasswordVerify("newPassword")
                .build();
        when(memberRepository.findByMemberId(userDetails.getUsername())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(anyString(), eq(member.getPassword())))
                .thenAnswer(invocation -> {
                    String rawPassword = invocation.getArgument(0);
                    return rawPassword.equals("testPassword");
                });
        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.changePassword(userDetails, form);
        });
        assertEquals(ErrorCode.INCORRECT_PASSWORD, exception.getErrorCode());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 새 비밀번호와 확인이 일치하지 않는 경우 예외 발생")
    void changePasswordFailure2() {
        // Given
        Member member = Member.builder()
                .memberId("testId")
                .password("testPassword")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(member.getMemberId());
        ChangePasswordForm form = ChangePasswordForm.builder()
                .password("testPassword")
                .newPassword("newPassword")
                .newPasswordVerify("wrongNewPassword")
                .build();
        when(memberRepository.findByMemberId(userDetails.getUsername())).thenReturn(Optional.of(member));
        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.changePassword(userDetails, form);
        });
        assertEquals(ErrorCode.INCORRECT_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 변경 - 새 비밀번호가 기존 비밀번호와 같은 경우 예외 발생")
    void changePassword_MatchesPreviousPassword() {
        // Given
        Member member = Member.builder()
                .memberId("testId")
                .password("testPassword")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(member.getMemberId());
        ChangePasswordForm form = ChangePasswordForm.builder()
                .password("testPassword")
                .newPassword("testPassword")
                .newPasswordVerify("testPassword")
                .build();
        when(memberRepository.findByMemberId(userDetails.getUsername())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(form.getPassword(), member.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(form.getPassword(), member.getPassword())).thenReturn(true);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.changePassword(userDetails, form);
        });
        assertEquals(ErrorCode.MATCHES_PREVIOUS_PASSWORD, exception.getErrorCode());
        verify(passwordEncoder, never()).encode(any());
    }
}