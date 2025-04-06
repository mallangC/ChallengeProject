package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.DietChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietChallengeRepository extends JpaRepository<DietChallenge, Long>, DietChallengeRepositoryCustom {
}
