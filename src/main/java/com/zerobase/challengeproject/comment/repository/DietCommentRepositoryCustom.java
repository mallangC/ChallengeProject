package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.DietComment;

import java.util.Optional;

public interface DietCommentRepositoryCustom {

  Optional<DietComment> searchDietCommentById(Long commentId);
}
