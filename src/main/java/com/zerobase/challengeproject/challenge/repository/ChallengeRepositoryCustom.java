package com.zerobase.challengeproject.challenge.repository;

import com.zerobase.challengeproject.challenge.entity.Challenge;

import java.util.List;

public interface ChallengeRepositoryCustom {

  Challenge searchChallengeWithCoteChallengeById(Long challengeId);

  Challenge searchChallengeWithDietChallengeById(Long challengeId);

  Challenge searchChallengeWithWaterChallengeById(Long challengeId);

  List<Challenge> searchAllChallenge();

}
