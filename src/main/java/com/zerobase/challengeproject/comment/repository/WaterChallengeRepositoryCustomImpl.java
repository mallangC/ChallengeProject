package com.zerobase.challengeproject.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QWaterChallenge.waterChallenge;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class WaterChallengeRepositoryCustomImpl implements WaterChallengeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public WaterChallenge searchWaterChallengeByChallengeIdAndLoginId(Long challengeId, String loginId) {
    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
    WaterChallenge findWaterChallenge = queryFactory.selectFrom(waterChallenge)
            .join(waterChallenge.member, member).fetchJoin()
            .join(waterChallenge.challenge, challenge).fetchJoin()
            .where(waterChallenge.challenge.id.eq(challengeId)
                    .and(waterChallenge.member.memberId.eq(loginId))
                    .and(waterChallenge.createdAt.between(startOfDay, endOfDay)))
            .fetchOne();
    if (findWaterChallenge == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_WATER_CHALLENGE);
    }
    return findWaterChallenge;
  }
}
