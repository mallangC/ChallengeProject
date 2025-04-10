package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterChallengeRepository extends JpaRepository<WaterChallenge, Long> , WaterChallengeRepositoryCustom{
    WaterChallenge findByChallengeId(Long challengeId);
}
