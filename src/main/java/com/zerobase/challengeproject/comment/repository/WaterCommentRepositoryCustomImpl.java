package com.zerobase.challengeproject.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.WaterComment;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QWaterChallenge.waterChallenge;
import static com.zerobase.challengeproject.comment.entity.QWaterComment.waterComment;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class WaterCommentRepositoryCustomImpl implements WaterCommentRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public WaterComment searchWaterCommentById(Long commentId) {
    WaterComment findWaterComment = queryFactory.selectFrom(waterComment)
            .join(waterComment.waterChallenge, waterChallenge).fetchJoin()
            .join(waterComment.waterChallenge.challenge, challenge).fetchJoin()
            .join(waterComment.member, member).fetchJoin()
            .where(waterComment.id.eq(commentId))
            .fetchOne();
    if (findWaterComment == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_WATER_COMMENT);
    }
    return findWaterComment;
  }
}
