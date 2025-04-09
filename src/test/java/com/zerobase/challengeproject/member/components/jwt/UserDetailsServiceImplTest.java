package com.zerobase.challengeproject.member.components.jwt;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.member.domain.form.MemberSignupForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Test
    @DisplayName("UserDetails 에서 유저 정보 가져오기")
    void loadUserByUsername() {
        //given
        String memberId = "testId";
        Member mockMember = Member.builder()
                .id(1L)
                .memberId("testId")
                .memberName("testName")
                .nickname("testNickname")
                .email("testEmail@email.com")
                .phoneNum("01011112222")
                .password("testtest1!")
                .build();
        when(memberRepository.findByMemberId(mockMember.getMemberId()))
                .thenReturn(Optional.of(mockMember));
        //when
        UserDetails result = userDetailsServiceImpl.loadUserByUsername(memberId);
        //then
        assertNotNull(result);
        assertEquals(mockMember.getMemberId(), result.getUsername());
    }

    @Test
    @DisplayName("UserDetails 에서 유저 정보 가져오기 실패")
    void loadUserByUsernameFailure() {
        //given
        String memberId = "nonExistTestId";
        when(memberRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
        //when & then
        assertThrows(CustomException.class, () -> userDetailsServiceImpl.loadUserByUsername(memberId));
    }

}