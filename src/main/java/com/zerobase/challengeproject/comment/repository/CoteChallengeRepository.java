package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoteChallengeRepository extends JpaRepository<CoteChallenge, Long> , CoteChallengeRepositoryCustom{
  boolean existsByStartAt(LocalDateTime startAt);
  List<CoteChallenge> findAllByChallengeId(Long challengeId);
}
