package com.zerobase.challengeproject.challenge.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QCoteChallenge.coteChallenge;
import static com.zerobase.challengeproject.comment.entity.QDietChallenge.dietChallenge;
import static com.zerobase.challengeproject.comment.entity.QWaterChallenge.waterChallenge;
import static com.zerobase.challengeproject.member.entity.QMember.member;


@RequiredArgsConstructor
public class ChallengeRepositoryCustomImpl implements ChallengeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 챌린지 객체를 호출하는 메서드
   * 챌린지에 연결된 멤버, 코테 챌린지를 fetchJoin()으로 즉시 로딩
   *
   * @param challengeId 챌린지 아이디
   * @return 챌린지 객체
   */
  @Override
  public Challenge searchChallengeWithCoteChallengeById(Long challengeId) {
    Challenge findChallenge = queryFactory.selectFrom(challenge)
            .join(challenge.member, member).fetchJoin()
            .leftJoin(challenge.coteChallenges, coteChallenge).fetchJoin()
            .where(challenge.id.eq(challengeId))
            .fetchOne();

    if (findChallenge == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGE);
    }
    return findChallenge;
  }

  /**
   * DB에서 챌린지 객체를 호출하는 메서드
   * 챌린지에 연결된 멤버, 다이어트 챌린지를 fetchJoin()으로 즉시 로딩
   *
   * @param challengeId 챌린지 아이디
   * @return 챌린지 객체
   */
  @Override
  public Challenge searchChallengeWithDietChallengeById(Long challengeId) {
    Challenge findChallenge = queryFactory.selectFrom(challenge)
            .join(challenge.member, member).fetchJoin()
            .leftJoin(challenge.dietChallenges, dietChallenge).fetchJoin()
            .where(challenge.id.eq(challengeId))
            .fetchOne();

    if (findChallenge == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGE);
    }
    return findChallenge;
  }

  @Override
  public Challenge searchChallengeWithWaterChallengeById(Long challengeId) {
    Challenge findChallenge = queryFactory.selectFrom(challenge)
            .join(challenge.member, member).fetchJoin()
            .leftJoin(challenge.waterChallenges, waterChallenge).fetchJoin()
            .where(challenge.id.eq(challengeId))
            .fetchOne();

    if (findChallenge == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGE);
    }
    return findChallenge;
  }
}
