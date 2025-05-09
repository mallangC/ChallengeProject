package com.zerobase.challengeproject.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QCoteChallenge.coteChallenge;
import static com.zerobase.challengeproject.comment.entity.QCoteComment.coteComment;
import static com.zerobase.challengeproject.member.entity.QMember.member;


@RequiredArgsConstructor
public class CoteChallengeRepositoryCustomImpl implements CoteChallengeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 코테 챌린지 객체를 호출하는 메서드
   * 코테 챌린지에 연결된 챌린지, 댓글을 fetchJoin()으로 즉시 로딩
   *
   * @param challengeId 챌린지 아이디
   * @param loginId     로그인 아이디
   * @param findAt      날짜
   * @return 코테 챌린지 객체
   */

  @Override
  public Optional<CoteChallenge> searchCoteChallengeByStartAt(Long challengeId, String loginId, LocalDateTime findAt) {
    LocalDate date = findAt.toLocalDate();
    CoteChallenge findCoteChallenge = queryFactory.selectFrom(coteChallenge)
            .leftJoin(coteChallenge.comments, coteComment).fetchJoin()
            .join(coteChallenge.challenge, challenge).fetchJoin()
            .where(coteChallenge.challenge.id.eq(challengeId)
                    .and(coteChallenge.startAt.between(
                            date.atStartOfDay(), date.plusDays(1).atStartOfDay().minusNanos(1)))) // 날짜만 비교
            .fetchFirst();
    return Optional.ofNullable(findCoteChallenge);
  }

  /**
   * DB에서 코테 챌린지 객체를 호출하는 메서드
   * 코테 챌린지에 연결된 챌린지, 회원, 댓글을 fetchJoin()으로 즉시 로딩
   *
   * @param coteChallengeId 코테 챌린지 아이디
   * @return 코테 챌린지 객체
   */
  @Override
  public Optional<CoteChallenge> searchCoteChallengeById(Long coteChallengeId) {
    CoteChallenge findCoteChallenge = queryFactory.selectFrom(coteChallenge)
            .join(coteChallenge.challenge, challenge).fetchJoin()
            .join(challenge.member, member).fetchJoin()
            .leftJoin(coteChallenge.comments, coteComment).fetchJoin()
            .where(coteChallenge.id.eq(coteChallengeId))
            .fetchOne();
    return Optional.ofNullable(findCoteChallenge);
  }

  /**
   * DB에서 코테 챌린지 객체를 모두 호출하는 메서드
   * 코테 챌린지에 연결된 챌린지, 회원을 fetchJoin()으로 즉시 로딩
   *
   * @param page        페이지 숫자
   * @param challengeId 챌린지 아이디
   * @return 페이징된 코테 챌린지 정보
   */
  @Override
  public Page<CoteChallenge> searchAllCoteChallengeByChallengeId(int page, Long challengeId) {
    Pageable pageable = PageRequest.of(page, 20);

    Long total = queryFactory.select(coteChallenge.count())
            .from(coteChallenge)
            .where(coteChallenge.challenge.id.eq(challengeId))
            .fetchOne();

    if (total == null) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    List<CoteChallenge> findCoteChallenges = queryFactory.selectFrom(coteChallenge)
            .join(coteChallenge.challenge, challenge).fetchJoin()
            .join(challenge.member, member).fetchJoin()
            .where(coteChallenge.challenge.id.eq(challengeId))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    return new PageImpl<>(findCoteChallenges, pageable, total);
  }
}
