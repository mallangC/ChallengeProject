//package com.zerobase.challengeproject.member;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.javafaker.Faker;
//import com.zerobase.challengeproject.member.domain.form.MemberLoginForm;
//import com.zerobase.challengeproject.member.entity.Member;
//import com.zerobase.challengeproject.member.repository.MemberRepository;
//import com.zerobase.challengeproject.type.MemberType;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ActiveProfiles("test")
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//public class LoginIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    private Member mockMember;
//    private Member blacklistedMember;
//    private Faker faker;
//
//    @BeforeEach
//    void setup() {
//        faker = new Faker();
//
//        memberRepository.deleteAll();
//        // 정상 회원 저장
//        mockMember = Member.builder()
//                .loginId(faker.name().name())
//                .password(passwordEncoder.encode("testPassword1!"))
//                .email(faker.internet().emailAddress())
//                .isEmailVerified(true)
//                .phoneNumber("010"+faker.number().digits(8))
//                .memberType(MemberType.USER)
//                .nickname(faker.funnyName().name())
//                .memberName(faker.name().fullName())
//                .isBlackList(false)
//                .build();
//        memberRepository.save(mockMember);
//
//        // 블랙리스트 회원 저장
//        blacklistedMember = Member.builder()
//                .loginId(faker.name().name())
//                .isEmailVerified(true)
//                .password(passwordEncoder.encode("blackPassword1!"))
//                .email(faker.internet().emailAddress())
//                .phoneNumber("010"+faker.number().digits(8))
//                .memberType(MemberType.USER)
//                .nickname(faker.funnyName().name())
//                .memberName(faker.name().fullName())
//                .isBlackList(true)
//                .build();
//        memberRepository.save(blacklistedMember);
//    }
//
//    @Test
//    @DisplayName("로그인 성공 - JWT 반환")
//    void loginSuccess() throws Exception {
//        MemberLoginForm loginForm = new MemberLoginForm(mockMember.getLoginId(), "testPassword1!");
//
//        mockMvc.perform(post("/api/member/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginForm)))
//                .andExpect(status().isOk())
//                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
//                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다"));
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 아이디 없음")
//    void loginFailure() throws Exception {
//        MemberLoginForm loginForm = new MemberLoginForm(faker.name().name(), "testPassword1!");
//
//        mockMvc.perform(post("/api/member/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginForm)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못되었습니다."));
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 비밀번호 불일치")
//    void loginFailure2() throws Exception {
//        MemberLoginForm loginForm = new MemberLoginForm(mockMember.getLoginId(), faker.internet().password());
//
//        mockMvc.perform(post("/api/member/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginForm)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 잘못되었습니다."));
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 블랙리스트 회원")
//    void loginFailure3() throws Exception {
//        MemberLoginForm loginForm = new MemberLoginForm(blacklistedMember.getLoginId(),"blackPassword1!" );
//
//        mockMvc.perform(post("/api/member/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginForm)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.message").value("블랙리스트 등록된 회원입니다. 관리자에게 문의하세요"));
//    }
//
//    @AfterEach
//    void tearDown() {
//        SecurityContextHolder.clearContext();
//    }
//}
