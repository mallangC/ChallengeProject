package com.zerobase.challengeproject.member.components.jwt;

import com.github.javafaker.Faker;
import com.zerobase.challengeproject.exception.BlacklistedMemberException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomAuthenticationProvider authenticationProvider;

    private Member mockMember;
    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        mockMember = Member.builder()
                .loginId(faker.name().username())
                .password("testPassword")
                .memberName(faker.name().fullName())
                .nickname(faker.funnyName().name())
                .email(faker.internet().emailAddress())
                .memberType(MemberType.USER)
                .phoneNumber("010" + faker.number().digits(8))
                .build();
    }

    @Test
    @DisplayName("정상 인증 성공")
    void authenticate() {
        // given
        UserDetailsImpl userDetails = new UserDetailsImpl(mockMember);
        when(userDetailsService.loadUserByUsername(mockMember.getLoginId())).thenReturn(userDetails);
        when(passwordEncoder.matches(mockMember.getPassword(), userDetails.getPassword())).thenReturn(true);

        Authentication auth = new UsernamePasswordAuthenticationToken(mockMember.getLoginId(), mockMember.getPassword());

        // when
        Authentication result = authenticationProvider.authenticate(auth);

        // then
        assertThat(result.getPrincipal()).isEqualTo(userDetails);
        assertThat(result.getAuthorities()).isEqualTo(userDetails.getAuthorities());
    }

    @Test
    @DisplayName("인증 실패 - 비밀번호 불일치")
    void authenticateFailure() {
        // given
        String password = "wrongPassword";
        UserDetailsImpl userDetails = new UserDetailsImpl(mockMember);
        when(userDetailsService.loadUserByUsername(mockMember.getLoginId())).thenReturn(userDetails);
        when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(false);

        Authentication auth = new UsernamePasswordAuthenticationToken(mockMember.getLoginId(), password);

        // when & then
        assertThrows(BadCredentialsException.class, () -> authenticationProvider.authenticate(auth));
    }

    @Test
    @DisplayName("인증 실패 - 블랙리스트 회원 ")
    void authenticateFailure2() {
        Member blackListedMember = Member.builder()
                .loginId(faker.name().username())
                .password("testPassword")
                .memberName(faker.name().fullName())
                .nickname(faker.funnyName().name())
                .email(faker.internet().emailAddress())
                .memberType(MemberType.USER)
                .isBlackList(true)
                .phoneNumber("010" + faker.number().digits(8))
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(blackListedMember);

        when(userDetailsService.loadUserByUsername(blackListedMember.getLoginId())).thenReturn(userDetails);
        when(passwordEncoder.matches(blackListedMember.getPassword(), userDetails.getPassword())).thenReturn(true);

        Authentication auth = new UsernamePasswordAuthenticationToken(blackListedMember.getLoginId(), blackListedMember.getPassword());

        // when & then
        BlacklistedMemberException exception = assertThrows(BlacklistedMemberException.class,
                () -> authenticationProvider.authenticate(auth));
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.MEMBER_IS_BLACKLIST.getMessage());
    }

    @Test
    @DisplayName("UsernamePasswordAuthenticationToken 지원 여부 확인")
    void supports() {
        boolean supports = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);
        assertThat(supports).isTrue();
    }
}
