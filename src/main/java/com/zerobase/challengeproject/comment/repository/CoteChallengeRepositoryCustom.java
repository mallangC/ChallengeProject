package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CoteChallengeRepositoryCustom {
  Optional<CoteChallenge> searchCoteChallengeByStartAt(Long challengeId, String memberId, LocalDateTime startAt);

  Optional<CoteChallenge> searchCoteChallengeById(Long coteChallengeId);

  Page<CoteChallenge> searchAllCoteChallengeByChallengeId(int page, Long challengeId);
}
