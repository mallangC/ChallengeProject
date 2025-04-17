package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.CoteComment;

import java.util.Optional;

public interface CoteCommentRepositoryCustom {

  Optional<CoteComment> searchCoteCommentById(Long coteCommentId);
}
