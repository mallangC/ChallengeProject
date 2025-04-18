package com.zerobase.challengeproject.member.components.jwt;

import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 잘못되었습니다."));

        return new UserDetailsImpl(member);
    }
}
