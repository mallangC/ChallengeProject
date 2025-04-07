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
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        //Auth 서비스 제공자
        String provider = request.getClientRegistration().getRegistrationId();
        //기본 사용자 정보 가져오기
        OAuth2User oAuth2User = delegate.loadUser(request);


        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);

        if(oAuth2User instanceof OidcUser oidcUser){
            attributes = oidcUser.getAttributes();
            userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        }

        String loginId = provider + "_" + userInfo.getProviderId();
        String encodedPassword = passwordEncoder.encode(provider + UUID.randomUUID());

        Member member = saveOrGetMember(loginId,encodedPassword,userInfo,provider);

        return new CustomOAuth2User(member, attributes);
    }

    private Member saveOrGetMember(String loginId, String encodedPassword, OAuth2UserInfo userInfo, String provider) {
        return memberRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .loginId(loginId)
                        .password(encodedPassword)
                        .memberName(userInfo.getName())
                        .nickname("멋쟁이 " + userInfo.getName())
                        .phoneNum("010-0000-0000")
                        .email(userInfo.getEmail())
                        .memberType(MemberType.USER)
                        .registerDate(LocalDateTime.now())
                        .socialProvider(SocialProvider.valueOf(provider.toUpperCase()))
                        .socialId(userInfo.getProviderId())
                        .emailAuthYn(true)
                        .emailAuthDate(LocalDateTime.now())
                        .account(0L)
                        .build()));
    }
}
