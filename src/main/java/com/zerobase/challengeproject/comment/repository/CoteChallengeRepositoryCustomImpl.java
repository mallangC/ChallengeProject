package com.zerobase.challengeproject.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.comment.entity.QCoteChallenge.coteChallenge;
import static com.zerobase.challengeproject.comment.entity.QCoteComment.coteComment;
import static com.zerobase.challengeproject.member.entity.QMember.member;


@RequiredArgsConstructor
public class CoteChallengeRepositoryCustomImpl implements CoteChallengeRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public CoteChallenge searchCoteChallengeByStartAt(Long challengeId, String memberId, LocalDateTime startAt) {
    LocalDate date = startAt.toLocalDate();

    /*
    CoteChallenge findCoteChallenge = queryFactory.selectFrom(coteChallenge)
            .leftJoin(coteChallenge.comments, coteComment).fetchJoin()
            .join(coteChallenge.challenge, challenge).fetchJoin()
            .where(coteChallenge.challenge.id.eq(challengeId)
                    .and(coteChallenge.startAt.toLocalDate().eq(date)))
            .fetchOne();

     */
    CoteChallenge findCoteChallenge = queryFactory.selectFrom(coteChallenge)
            .leftJoin(coteChallenge.comments, coteComment).fetchJoin()
            .join(coteChallenge.challenge, challenge).fetchJoin()

            .where(coteChallenge.challenge.id.eq(challengeId)
                    .and(coteChallenge.startAt.between(
                            date.atStartOfDay(), date.plusDays(1).atStartOfDay().minusNanos(1)))) // 날짜만 비교
            .fetchFirst();

    if (findCoteChallenge == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_COTE_CHALLENGE);
    }

    boolean isAlreadyComment = findCoteChallenge.getComments().stream()
            .anyMatch(comment ->
                    comment.getMember().getMemberId().equals(memberId));

    if (isAlreadyComment) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_COMMENT_TODAY);
    }

    return findCoteChallenge;
  }

  @Override
  public CoteChallenge searchCoteChallengeById(Long coteChallengeId) {

    CoteChallenge findCoteChallenge = queryFactory.selectFrom(coteChallenge)
            .join(coteChallenge.challenge, challenge).fetchJoin()
            .join(challenge.member, member).fetchJoin()
            .leftJoin(coteChallenge.comments, coteComment).fetchJoin()
            .where(coteChallenge.id.eq(coteChallengeId))
            .fetchOne();

    if (findCoteChallenge == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_COTE_CHALLENGE);
    }

    return findCoteChallenge;
  }
}
