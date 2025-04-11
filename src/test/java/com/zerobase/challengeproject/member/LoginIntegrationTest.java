package com.zerobase.challengeproject.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.challengeproject.member.domain.form.MemberLoginForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // 정상 회원 저장
        Member normalMember = Member.builder()
                .loginId("testId")
                .password(passwordEncoder.encode("testPassword1!")) // 반드시 인코딩된 비밀번호
                .email("test@email.com")
                .phoneNum("01012345678")
                .memberType(MemberType.USER)
                .nickname("testNickname")
                .memberName("testName")
                .isBlackList(false)
                .build();
        memberRepository.save(normalMember);

        // 블랙리스트 회원 저장
        Member blacklistedMember = Member.builder()
                .loginId("blackUser")
                .password(passwordEncoder.encode("testPassword1!"))
                .email("blackUser@email.com")
                .phoneNum("01022345678")
                .memberType(MemberType.USER)
                .nickname("blackNickname")
                .memberName("blackMemberName")
                .isBlackList(true)
                .build();
        memberRepository.save(blacklistedMember);
    }

    @Test
    @DisplayName("로그인 성공 - JWT 반환")
    void loginSuccess() throws Exception {
        MemberLoginForm loginForm = new MemberLoginForm("testId", "testPassword1!");

        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다"));
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 없음")
    void loginFailure() throws Exception {
        MemberLoginForm loginForm = new MemberLoginForm("wrongId", "testPassword1!");

        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못되었습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFailure2() throws Exception {
        MemberLoginForm loginForm = new MemberLoginForm("testId", "wrongPassword!");

        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못되었습니다"));
    }

    @Test
    @DisplayName("로그인 실패 - 블랙리스트 회원")
    void loginFailure3() throws Exception {
        MemberLoginForm loginForm = new MemberLoginForm("blackUser", "testPassword1!");

        mockMvc.perform(post("/api/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("블랙리스트 등록된 회원입니다. 관리자에게 문의하세요"));
    }
}
