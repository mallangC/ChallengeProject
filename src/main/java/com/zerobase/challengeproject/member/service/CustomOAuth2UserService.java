package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.member.service.oauthUserInfo.OAuth2UserInfo;
import com.zerobase.challengeproject.member.service.oauthUserInfo.OAuth2UserInfoFactory;
import com.zerobase.challengeproject.type.MemberType;
import com.zerobase.challengeproject.type.SocialProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
/**
 * OAuth2 로그인 요청을 처리하는 커스텀 서비스입니다.
 * Spring Security의 {@link OAuth2UserService}를 구현하여,
 * Google, Kakao, Naver 등 다양한 OAuth2 제공자로부터 사용자 정보를 받아오고,
 * DB에 사용자가 존재하지 않을 경우 자동으로 회원가입을 처리합니다.
 * 최종적으로 {@link CustomOAuth2User} 객체를 반환하여 인증 정보를 구성합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * 기본 OAuth2 사용자 정보 조회를 담당하는 Spring 기본 구현체입니다.
     */
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    /**
     * OAuth2 로그인 시 사용자 정보를 로드하여 처리합니다.
     * 공급자(provider)별 사용자 정보를 파악하고, 회원이 존재하지 않으면 자동 가입 처리를 진행한 후
     * {@link CustomOAuth2User} 객체를 생성해 반환합니다.
     *
     * @param request OAuth2 로그인 요청 정보
     * @return 인증된 사용자 정보 {@link OAuth2User}
     * @throws OAuth2AuthenticationException 인증 처리 중 오류 발생 시 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        // OAuth2 제공자 이름 (google, kakao, naver)
        String provider = request.getClientRegistration().getRegistrationId();
        // 기본 OAuth2 사용자 정보 조회
        OAuth2User oAuth2User = delegate.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        /**
         *  🫢 OpenID Connect 사용자일 경우 속성 다시 추출 🫢
         */
        if(oAuth2User instanceof OidcUser oidcUser){
            attributes = oidcUser.getAttributes();
        } else {
            attributes = oAuth2User.getAttributes();
        }
        // 제공자별 사용자 정보 파싱
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        String loginId = provider + "_" + userInfo.getProviderId();
        String encodedPassword = passwordEncoder.encode(provider + UUID.randomUUID());

        Member member = saveOrGetMember(loginId,encodedPassword,userInfo,provider);
        if(member.isBlackList()){
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("blacklist_user", "블랙리스트 등록된 회원입니다. 관리자에게 문의하세요.", null)
            );
        }

        return new CustomOAuth2User(member, attributes);
    }
    /**
     * 사용자의 이메일을 기준으로 회원을 조회하고, 존재하지 않으면 신규 회원으로 저장합니다.
     *
     * @param loginId         생성된 로그인 ID
     * @param encodedPassword 임시 비밀번호 (암호화된 값)
     * @param userInfo        OAuth2 사용자 정보
     * @param provider        OAuth2 제공자명 (google, kakao, 등)
     * @return 기존 또는 새롭게 저장된 {@link Member} 객체
     */
    private Member saveOrGetMember(String loginId, String encodedPassword, OAuth2UserInfo userInfo, String provider) {
        return memberRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .loginId(loginId)
                        .password(encodedPassword)
                        .memberName(userInfo.getName())
                        .nickname("멋쟁이 " + userInfo.getName())
                        .phoneNumber("010-0000-0000")
                        .email(userInfo.getEmail())
                        .memberType(MemberType.USER)
                        .registerDate(LocalDateTime.now())
                        .socialProvider(SocialProvider.valueOf(provider.toUpperCase()))
                        .socialId(userInfo.getProviderId())
                        .isEmailVerified(true)
                        .emailAuthDate(LocalDateTime.now())
                        .isBlackList(false)
                        .account(0L)
                        .build()));
    }
}
