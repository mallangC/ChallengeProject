package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.WaterChallenge;

public interface WaterChallengeRepositoryCustom {
  WaterChallenge searchWaterChallengeByChallengeIdAndLoginId(Long challengeId, String loginId);
}

