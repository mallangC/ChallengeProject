package com.zerobase.challengeproject.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.WaterComment;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QWaterChallenge.waterChallenge;
import static com.zerobase.challengeproject.comment.entity.QWaterComment.waterComment;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class WaterCommentRepositoryCustomImpl implements WaterCommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 물마시기 댓글 객체를 호출하는 메서드
   * 물마시기 댓글과 연결된 회원과 물마시기 챌린지,
   * 물마시기 챌린지와 연결된 챌린지를 fetchJoin()으로 즉시 로딩
   *
   * @param commentId 댓글 아이디
   * @return 물마시기 댓글 객체
   */
  @Override
  public Optional<WaterComment> searchWaterCommentById(Long commentId) {
    WaterComment findWaterComment = queryFactory.selectFrom(waterComment)
            .join(waterComment.waterChallenge, waterChallenge).fetchJoin()
            .join(waterComment.waterChallenge.challenge, challenge).fetchJoin()
            .join(waterComment.member, member).fetchJoin()
            .where(waterComment.id.eq(commentId))
            .fetchOne();
    return Optional.ofNullable(findWaterComment);
  }
}
