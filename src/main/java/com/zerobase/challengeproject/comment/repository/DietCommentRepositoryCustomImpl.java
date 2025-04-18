package com.zerobase.challengeproject.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.DietComment;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QDietChallenge.dietChallenge;
import static com.zerobase.challengeproject.comment.entity.QDietComment.dietComment;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class DietCommentRepositoryCustomImpl implements DietCommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 다이어트 댓글 객체를 호출하는 메서드
   * 다이어트 댓글에 연결된 다이어트 챌린지와 멤버,
   * 다이어트 챌린지에 연결된 챌린지를 fetchJoin()으로 즉시 로딩
   *
   * @param commentId 댓글 아이디
   * @return 다이어트 댓글 객체
   */

  @Override
  public Optional<DietComment> searchDietCommentById(Long commentId) {
    DietComment findDietComment = queryFactory.selectFrom(dietComment)
            .join(dietComment.dietChallenge, dietChallenge).fetchJoin()
            .join(dietComment.dietChallenge.challenge, challenge).fetchJoin()
            .join(dietComment.member, member).fetchJoin()
            .where(dietComment.id.eq(commentId))
            .fetchOne();
    return Optional.ofNullable(findDietComment);
  }
}

