package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface WaterChallengeRepositoryCustom {
  Optional<WaterChallenge> searchWaterChallengeByChallengeIdAndLoginId(Long challengeId, String loginId);

  Page<WaterChallenge> searchAllWaterChallengeByChallengeId(int page, Long challengeId, Boolean isPass);
}

