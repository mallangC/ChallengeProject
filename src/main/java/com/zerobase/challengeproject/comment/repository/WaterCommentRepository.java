package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.WaterComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaterCommentRepository extends JpaRepository<WaterComment, Long>, WaterCommentRepositoryCustom {

    List<WaterComment> findAllByWaterChallengeIdIn(List<Long> challengeId);
}
