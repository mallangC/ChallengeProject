package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.DietChallenge;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface DietChallengeRepositoryCustom {

  Optional<DietChallenge> searchDietChallengeByChallengeIdAndLoginId(Long challengeId, String loginId);

  Page<DietChallenge> searchAllDietChallengeByChallengeId(int page, Long challengeId, Boolean isPass);
}
