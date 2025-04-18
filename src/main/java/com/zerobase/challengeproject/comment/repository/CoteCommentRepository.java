package com.zerobase.challengeproject.comment.repository;

import com.zerobase.challengeproject.comment.entity.CoteComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoteCommentRepository extends JpaRepository<CoteComment, Long>, CoteCommentRepositoryCustom {


    List<CoteComment> findAllByCoteChallengeIdInAndMemberId(List<Long> coteChallengeIds, Long memberId);

}
