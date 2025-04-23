package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterChallengeRepository extends JpaRepository<WaterChallenge, Long> , WaterChallengeRepositoryCustom{
    WaterChallenge findByChallengeId(Long challengeId);
    List<WaterChallenge> findAllByChallengeId(Long challengeId);
    List<WaterChallenge> findAllByChallengeIdAndMember(Long challengeId, Member member);

}
