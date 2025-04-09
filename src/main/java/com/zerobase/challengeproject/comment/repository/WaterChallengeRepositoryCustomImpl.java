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

  /**
   * DB에서 물마시기 챌린지 객체를 호출하는 메서드
   * 물마시기 챌린지와 연결된 회원과 챌린지를 fetchJoin()으로 즉시 로딩
   *
   * @param challengeId 챌린지 아이디
   * @param loginId     로그인 아이디
   * @return 물마시기 챌린지 객체
   */
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

  /**
   * DB에서 페이징된 오늘의 물마시기 챌린지 정보를 호출하는 메서드
   * 물마시기 챌린지와 연결된 회원과 챌린지를 fetchJoin()으로 즉시 로딩
   *
   * @param page        페이지 숫자
   * @param challengeId 챌린지 아이디
   * @param isPass      챌린지 성공 여부
   * @return 페이징된 물마시기 챌린지 정보
   */
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
