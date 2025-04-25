package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DietChallengeRepository extends JpaRepository<DietChallenge, Long>, DietChallengeRepositoryCustom {
    DietChallenge findByChallengeId(Long challengeId);
    List<DietChallenge> findAllByChallengeId(Long challengeId);
    Optional<DietChallenge> findByChallengeAndMember(Challenge challenge, Member member);
}
