package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.DietComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietCommentRepository extends JpaRepository<DietComment, Long>, DietCommentRepositoryCustom {
    List<DietComment> findByDietChallengeIdAndMemberId(Long dietChallengeId, Long memberId);

    List<DietComment> findAllByDietChallengeIdIn(List<Long> challengeId);
}
