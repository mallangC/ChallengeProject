package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.domain.dto.DietChallengeDto;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import org.springframework.data.domain.Page;

public interface DietChallengeRepositoryCustom {

  DietChallenge searchDietChallengeByChallengeIdAndLoginId(Long challengeId, String loginId);

  Page<DietChallengeDto> searchAllDietChallengeByChallengeId(int page, Long challengeId, Boolean isPass);
}
