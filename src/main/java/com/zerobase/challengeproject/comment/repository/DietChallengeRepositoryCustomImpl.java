package com.zerobase.challengeproject.comment.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QDietChallenge.dietChallenge;
import static com.zerobase.challengeproject.comment.entity.QDietComment.dietComment;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class DietChallengeRepositoryCustomImpl implements DietChallengeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 다이어트 챌린지 객체를 호출하는 메서드
   * 다이어트 챌린지에 연결된 챌린지, 회원, 다이어트 댓글을 fetchJoin()으로 즉시 로딩
   *
   * @param challengeId 챌린지 아이디
   * @param loginId     로그인 아이디
   * @return 다이어트 챌린지 정보
   */
  @Override
  public Optional<DietChallenge> searchDietChallengeByChallengeIdAndLoginId(Long challengeId, String loginId) {
    DietChallenge findDietChallenge = queryFactory.selectFrom(dietChallenge)
            .join(dietChallenge.challenge, challenge).fetchJoin()
            .join(dietChallenge.member, member).fetchJoin()
            .leftJoin(dietChallenge.comments, dietComment).fetchJoin()
            .where(dietChallenge.challenge.id.eq(challengeId)
                    .and(dietChallenge.member.loginId.eq(loginId)))
            .fetchOne();
    return Optional.ofNullable(findDietChallenge);
  }

  /**
   * DB에서 다이어트 챌린지 객체를 전부 호출하는 메서드
   * 다이어트 챌린지에 연결된 챌린지, 회원을 fetchJoin()으로 즉시 로딩
   *
   * @param page        페이지 숫자
   * @param challengeId 챌린지 아이디
   * @param isPass      챌린지 성공 여부
   * @return 페이징된 다이어트 챌린지 정보 (댓글 제외)
   */
  @Override
  public Page<DietChallenge> searchAllDietChallengeByChallengeId(
          int page, Long challengeId, Boolean isPass) {
    Pageable pageable = PageRequest.of(page, 20);

    BooleanExpression expression = null;
    if (isPass != null) {
      if (isPass) {
        expression = dietChallenge.currentWeight.loe(dietChallenge.goalWeight);
      } else {
        expression = dietChallenge.currentWeight.gt(dietChallenge.goalWeight);
      }
    }

    Long total = queryFactory.select(dietChallenge.count())
            .from(dietChallenge)
            .where(dietChallenge.challenge.id.eq(challengeId)
                    .and(expression))
            .fetchOne();

    if (total == null) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    List<DietChallenge> findDietChallenges = queryFactory.selectFrom(dietChallenge)
            .join(dietChallenge.challenge, challenge).fetchJoin()
            .join(dietChallenge.member, member).fetchJoin()
            .where(dietChallenge.challenge.id.eq(challengeId)
                    .and(expression))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    return new PageImpl<>(findDietChallenges, pageable, total);
  }
}
