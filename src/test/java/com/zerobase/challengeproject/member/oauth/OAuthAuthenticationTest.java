package com.zerobase.challengeproject.member.oauth;

import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OAuthAuthenticationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll(); // 항상 초기화
        memberRepository.save(Member.builder()
                .loginId("loginId")
                .password("password")
                .nickname("testNickname")
                .email("test@gmail.com")
                .memberName("memberName")
                .phoneNumber("01012345678")
                .memberType(MemberType.USER)
                .build());
    }

    @Test
    @DisplayName("구글 로그인 리다이렉트 정상 동작")
    void googleLoginRedirect() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://accounts.google.com/**"));
    }

    @Test
    @DisplayName("카카오 로그인 리다이렉트 정상 동작")
    void kakaoLoginRedirect() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/kakao"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://kauth.kakao.com/**"));
    }

    @Test
    @DisplayName("네이버 로그인 리다이렉트 정상 동작")
    void naverLoginRedirect() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/naver"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("https://nid.naver.com/**"));
    }

    @Test
    @DisplayName("인증된 사용자가 프로필 정보를 성공적으로 조회")
    void authenticatedUserGetProfileSuccess() throws Exception {
        // given
        String email = "test@gmail.com";
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("테스트용 사용자 없음"));
        UserDetailsImpl principal = new UserDetailsImpl(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        //when & then
        mockMvc.perform(get("/api/member/profile")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("회원 정보 불러오기를 성공했습니다"))
                .andExpect(jsonPath("$.data.email").value(email));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
