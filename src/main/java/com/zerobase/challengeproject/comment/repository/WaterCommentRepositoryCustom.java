package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.WaterComment;

import java.util.Optional;

public interface WaterCommentRepositoryCustom {
  Optional<WaterComment> searchWaterCommentById(Long commentId);
}
