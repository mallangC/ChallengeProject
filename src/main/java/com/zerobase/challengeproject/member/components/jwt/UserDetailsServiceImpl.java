package com.zerobase.challengeproject.member.components.jwt;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * UserDetails에서 유저의 정보를 가져옵니다.
     * @param loginId 로그인을 시도한 유저의 아이디
     * @return 유저가 입력한 유저의 아이디
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetailsImpl loadUserByUsername(String loginId) throws UsernameNotFoundException {

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

        if(member.isBlackList()){
            String message = "블랙리스트 등록된 회원입니다. 관리자에게 문의하세요";
            throw new AuthenticationServiceException(message);
        }

        return new UserDetailsImpl(member);
    }
}
