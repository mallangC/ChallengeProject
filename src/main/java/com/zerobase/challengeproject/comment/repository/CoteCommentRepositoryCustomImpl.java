package com.zerobase.challengeproject.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.zerobase.challengeproject.comment.entity.QCoteComment.coteComment;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class CoteCommentRepositoryCustomImpl implements CoteCommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 코테 댓글 객체를 호출하는 메서드
   * 코테 댓글에 연결된 회원을 fetchJoin()으로 즉시 로딩
   *
   * @param commentId 댓글 아이디
   * @return 코테 댓글 객체
   */
  @Override
  public Optional<CoteComment> searchCoteCommentById(Long commentId) {
    CoteComment findCoteComment = queryFactory.selectFrom(coteComment)
            .join(coteComment.member, member).fetchJoin()
            .where(coteComment.id.eq(commentId))
            .fetchOne();
    return Optional.ofNullable(findCoteComment);
  }
}
