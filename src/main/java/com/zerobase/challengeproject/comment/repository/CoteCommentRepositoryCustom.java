package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.CoteComment;

public interface CoteCommentRepositoryCustom {

  CoteComment searchCoteCommentById(Long coteCommentId);
}
