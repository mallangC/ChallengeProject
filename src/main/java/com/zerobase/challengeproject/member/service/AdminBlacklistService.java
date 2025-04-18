package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.domain.form.BlackListRegisterForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBlacklistService {

    private final MemberRepository memberRepository;

    /**
     * 관리자가 회원을 블랙리스트로 등록하는 서비스 메서드
     * @param form 블랙리스트로 등록하려는 회원 로그인 아이디
     * @return 블랙리스트 맴버 아이디
     */
    @Transactional
    public String registerBlacklist(BlackListRegisterForm form) {
        Member member = memberRepository.findByLoginId(form.getBlacklistUserLoginId())
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
                );
        member.registerBlacklist();
        return member.getLoginId();
    }

    /**
     * 관리자가 회원을 블랙리스트로 해제하는 서비스 메서드
     * @param form 블랙리스트로 해제하려는 회원 로그인 아이디
     * @return 블랙리스트가 헤제된 맴버 아이디
     */
    public String unRegisterBlacklist(BlackListRegisterForm form) {
        Member member = memberRepository.findByLoginId(form.getBlacklistUserLoginId())
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
                );
        if(!member.isBlackList()){
            throw new CustomException(ErrorCode.MEMBER_IS_UNBLACKLIST);
        }
        member.unRegisterBlacklist();
        return member.getLoginId();
    }
}
