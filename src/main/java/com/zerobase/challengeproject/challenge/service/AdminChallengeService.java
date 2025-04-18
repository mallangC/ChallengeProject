package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.MemberType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminChallengeService {

    private final ChallengeRepository challengeRepository;
    public void deleteChallengeByAdmin(Long challengeId, Member loginMember){
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        if(!loginMember.getMemberType().equals(MemberType.ADMIN)){
            throw new CustomException(ErrorCode.NOT_MEMBER_TYPE_ADMIN);
        }

        challengeRepository.delete(challenge);
    }
}
