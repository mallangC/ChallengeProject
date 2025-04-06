package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.DietComment;

public interface DietCommentRepositoryCustom {

  DietComment searchDietCommentById(Long commentId);
}
