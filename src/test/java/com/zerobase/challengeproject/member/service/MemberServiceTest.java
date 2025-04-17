package com.zerobase.challengeproject.member.service;

import com.github.javafaker.Faker;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberProfileDto;
import com.zerobase.challengeproject.member.domain.form.ChangePasswordForm;
import com.zerobase.challengeproject.member.domain.form.MemberProfileFrom;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Stream;

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

    private Member mockMember;
    private Faker faker;

    @BeforeEach
    void setUp() {
        Faker faker = new Faker();
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
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    void getProfile() {
        //given
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        given(memberRepository.findByLoginId(userDetails.getUsername())).willReturn(Optional.of(mockMember));
        //when
        MemberProfileDto memberProfileDto = memberService.getProfile(userDetails.getUsername());
        //then
        assertNotNull(memberProfileDto);
        assertEquals(mockMember.getLoginId(), memberProfileDto.getLoginId());
        assertEquals(mockMember.getMemberName(), memberProfileDto.getMemberName());
    }

    @Test
    @DisplayName("프로필 업데이트 성공")
    void updateProfile() {
        // given
        MemberProfileFrom form = MemberProfileFrom.builder()
                .nickname("testNickname")
                .phoneNum("01011112222")
                .build();
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(mockMember.getLoginId());
        when(memberRepository.findByLoginId(userDetails.getUsername())).thenReturn(Optional.of(mockMember));
        // When
        MemberProfileDto result = memberService.updateProfile(userDetails.getUsername(), form);

        // Then
        assertNotNull(result);
        assertEquals(form.getPhoneNum(), result.getPhoneNum());
        assertEquals(form.getNickname(), result.getNickName());
        verify(memberRepository, times(1)).findByLoginId(mockMember.getLoginId());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword() {
        // given
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(mockMember.getLoginId());
        ChangePasswordForm form = ChangePasswordForm.builder()
                .password("testPassword")
                .newPassword("newPassword")
                .newPasswordVerify("newPassword")
                .build();
        when(memberRepository.findByLoginId(userDetails.getUsername())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches("testPassword", mockMember.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword", mockMember.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodePassword");

        // When
        String result = memberService.changePassword(userDetails.getUsername(), form);

        // Then
        assertEquals(mockMember.getLoginId(), result);
        verify(passwordEncoder, times(1)).matches("testPassword", "testPassword");
        verify(passwordEncoder, times(1)).matches("newPassword", "testPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
    }

    @ParameterizedTest(name = "비밀번호 변경 실패: 현재 비번={0}, 새 비번={1}, 확인용 비번={2}, 예상 오류={3}")
    @MethodSource("provideInvalidPasswordChangeForms")
    @DisplayName("비밀번호 변경 실패 - 다양한 예외 상황 테스트")
    void changePasswordFailures(String currentPw, String newPw, String newPwVerify, ErrorCode expectedErrorCode) {
        // Given
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn(mockMember.getLoginId());

        ChangePasswordForm form = ChangePasswordForm.builder()
                .password(currentPw)
                .newPassword(newPw)
                .newPasswordVerify(newPwVerify)
                .build();

        when(memberRepository.findByLoginId(userDetails.getUsername())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(eq(currentPw), eq(mockMember.getPassword())))
                .thenReturn(currentPw.equals("testPassword"));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.changePassword(userDetails.getUsername(), form);
        });
        assertEquals(expectedErrorCode, exception.getErrorCode());
    }

    private static Stream<Arguments> provideInvalidPasswordChangeForms() {
        return Stream.of(
                Arguments.of("wrongPassword", "newPassword", "newPassword", ErrorCode.INCORRECT_PASSWORD),
                Arguments.of("testPassword", "newPassword", "wrongVerify", ErrorCode.INCORRECT_PASSWORD),
                Arguments.of("testPassword", "testPassword", "testPassword", ErrorCode.MATCHES_PREVIOUS_PASSWORD)
        );
    }

}