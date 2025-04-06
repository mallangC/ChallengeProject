package com.zerobase.challengeproject.challenge.repository;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.entity.MemberChallenge;
import com.zerobase.challengeproject.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberChallengeRepository extends JpaRepository<MemberChallenge, Long> {
    Page<MemberChallenge> findByMemberId(Long memberId, Pageable pageable);
    boolean existsByChallengeAndMember(Challenge challenge, Member member);
    Optional<MemberChallenge> findByChallengeAndMember(Challenge challenge, Member member);
    long countByChallengeAndMemberNot(Challenge challenge, Member member);
}
