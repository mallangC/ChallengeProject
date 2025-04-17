package com.zerobase.challengeproject.challenge.repository;

import com.zerobase.challengeproject.challenge.entity.Challenge;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepositoryCustom {

  Optional<Challenge> searchChallengeWithCoteChallengeById(Long challengeId);

  Optional<Challenge> searchChallengeWithDietChallengeById(Long challengeId);

  Optional<Challenge> searchChallengeWithWaterChallengeById(Long challengeId);

  List<Challenge> searchAllChallenge();

}
