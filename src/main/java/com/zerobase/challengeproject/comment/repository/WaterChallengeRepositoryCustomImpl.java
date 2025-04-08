package com.zerobase.challengeproject.comment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

  @Override
  public Page<WaterChallengeDto> searchAllWaterChallengeByChallengeId(int page, Long challengeId, Boolean isPass) {
    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
    Pageable pageable = PageRequest.of(page, 20);

    BooleanExpression expression = null;
    if (isPass != null) {
      if (isPass) {
        expression = waterChallenge.currentMl.goe(waterChallenge.goalMl);
      } else {
        expression = waterChallenge.currentMl.lt(waterChallenge.goalMl);
      }
    }

    Long total = queryFactory.select(waterChallenge.count())
            .from(waterChallenge)
            .where(waterChallenge.challenge.id.eq(challengeId)
                    .and(expression)
                    .and(waterChallenge.createdAt.between(startOfDay, endOfDay)))
            .fetchOne();

    if (total == null) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    List<WaterChallenge> findWaterChallenges = queryFactory.selectFrom(waterChallenge)
            .join(waterChallenge.challenge, challenge).fetchJoin()
            .join(waterChallenge.member, member).fetchJoin()
            .where(waterChallenge.challenge.id.eq(challengeId)
                    .and(expression)
                    .and(waterChallenge.createdAt.between(startOfDay, endOfDay)))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    List<WaterChallengeDto> waterChallengeDtos = findWaterChallenges.stream()
            .map(WaterChallengeDto::fromWithoutComment)
            .toList();

    return new PageImpl<>(waterChallengeDtos, pageable, total);
  }
}
