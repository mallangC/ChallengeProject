package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.WaterComment;

public interface WaterCommentRepositoryCustom {
  WaterComment searchWaterCommentById(Long commentId);
}
