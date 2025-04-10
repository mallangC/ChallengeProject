package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.domain.form.BlackListRegisterForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.MemberType;
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

    @Test
    @DisplayName("블랙리스트 등록 성공")
    void registerBlacklist () {
        //given
        Member mockMember = Member.builder()
                .id(1L)
                .loginId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .password("encodedPassword")
                .memberType(MemberType.USER)
                .isBlackList(false)
                .build();
        BlackListRegisterForm form = BlackListRegisterForm.builder()
                .blacklistUserLoginId("blacklistUserLoginId")
                .build();
        when(memberRepository.findByLoginId(form.getBlacklistUserLoginId())).thenReturn(Optional.of(mockMember));
        //when
        String result = adminBlacklistService.registerBlacklist(form);
        //then
        assertEquals(mockMember.getLoginId()+"님 블랙리스트 등록 완료", result);
    }
    @Test
    @DisplayName("블랙리스트 등록 실패 - 존재하지 않는 회원")
    void registerBlacklistFailure () {
        //given
        Member mockMember = Member.builder()
                .id(1L)
                .loginId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .password("encodedPassword")
                .memberType(MemberType.USER)
                .isBlackList(false)
                .build();
        BlackListRegisterForm form = BlackListRegisterForm.builder()
                .blacklistUserLoginId("blacklistUserLoginId")
                .build();
        when(memberRepository.findByLoginId(form.getBlacklistUserLoginId())).thenReturn(Optional.empty());
        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> adminBlacklistService.registerBlacklist(form));
        assertEquals(ErrorCode.NOT_FOUND_MEMBER, exception.getErrorCode());

    }
}