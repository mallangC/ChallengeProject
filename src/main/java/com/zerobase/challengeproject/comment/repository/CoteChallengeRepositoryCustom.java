package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.domain.dto.CoteChallengeDto;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface CoteChallengeRepositoryCustom {
  CoteChallenge searchCoteChallengeByStartAt(Long challengeId, String memberId, LocalDateTime startAt);

  CoteChallenge searchCoteChallengeById(Long coteChallengeId);

  Page<CoteChallengeDto> searchAllCoteChallengeByChallengeId(int page, Long challengeId);
}
