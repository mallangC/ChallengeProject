package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoteChallengeRepository extends JpaRepository<CoteChallenge, Long> , CoteChallengeRepositoryCustom{
  boolean existsByStartAt(LocalDateTime startAt);
  List<CoteChallenge> findAllByChallengeId(Long challengeId);


}
