package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import org.springframework.data.domain.Page;

public interface WaterChallengeRepositoryCustom {
  WaterChallenge searchWaterChallengeByChallengeIdAndLoginId(Long challengeId, String loginId);

  Page<WaterChallengeDto> searchAllWaterChallengeByChallengeId(int page, Long challengeId, Boolean isPass);
}

