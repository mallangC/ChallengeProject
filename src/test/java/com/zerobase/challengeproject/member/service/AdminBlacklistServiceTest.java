package com.zerobase.challengeproject.member.service;

import com.github.javafaker.Faker;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.domain.form.BlackListRegisterForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBlacklistServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AdminBlacklistService adminBlacklistService;

    private Member mockMember;
    private BlackListRegisterForm form;
    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
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
        form = BlackListRegisterForm.builder()
                .blacklistUserLoginId("blacklistUserLoginId")
                .build();
    }

    @Test
    @DisplayName("블랙리스트 등록 성공")
    void registerBlacklist() {
        //given
        when(memberRepository.findByLoginId(form.getBlacklistUserLoginId())).thenReturn(Optional.of(mockMember));
        //when
        String result = adminBlacklistService.registerBlacklist(form);
        //then
        assertEquals(mockMember.getLoginId(), result);
    }

    @Test
    @DisplayName("블랙리스트 등록 실패 - 존재하지 않는 회원")
    void registerBlacklistFailure() {
        //given
        when(memberRepository.findByLoginId(form.getBlacklistUserLoginId())).thenReturn(Optional.empty());
        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> adminBlacklistService.registerBlacklist(form));
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());

    }

    @Test
    @DisplayName("블랙리스트 해제 성공")
    void unRegisterBlacklist() {
        Member isBlacklistMember = Member.builder()
                .id(1L)
                .loginId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNumber("01011112222")
                .password("encodedPassword")
                .memberType(MemberType.USER)
                .isBlackList(true)
                .build();
        //given
        when(memberRepository.findByLoginId(form.getBlacklistUserLoginId())).thenReturn(Optional.of(isBlacklistMember));
        //when
        String result = adminBlacklistService.unRegisterBlacklist(form);
        //then
        assertEquals(isBlacklistMember.getLoginId(),result);
    }

    @Test
    @DisplayName("블랙리스트 해제 실패 - 블랙리스트가 아닌 맴버")
    void unRegisterBlacklistFailure() {
        //given
        when(memberRepository.findByLoginId(form.getBlacklistUserLoginId())).thenReturn(Optional.of(mockMember));
        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> adminBlacklistService.unRegisterBlacklist(form));
        assertEquals(ErrorCode.MEMBER_IS_UNBLACKLIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("블랙리스트 해제 실패 - 존재하지 않는 회원")
    void unRegisterBlacklistFailure2() {
        //given
        when(memberRepository.findByLoginId(form.getBlacklistUserLoginId())).thenReturn(Optional.empty());
        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> adminBlacklistService.unRegisterBlacklist(form));
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());
    }
}