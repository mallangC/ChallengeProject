package com.zerobase.challengeproject.member.service;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.domain.form.BlackListRegisterForm;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBlacklistService {

    private final MemberRepository memberRepository;

    @Transactional
    public String registerBlacklist(BlackListRegisterForm form) {
        Member member = memberRepository.findByLoginId(form.getBlacklistUserLoginId())
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
                );
        member.registerBlacklist();
        return member.getLoginId() + "님 블랙리스트 등록 완료";
    }
}
