package com.zerobase.challengeproject.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.AccountType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.zerobase.challengeproject.account.entity.QAccountDetail.accountDetail;
import static com.zerobase.challengeproject.challenge.entity.QChallenge.challenge;
import static com.zerobase.challengeproject.challenge.entity.QMemberChallenge.memberChallenge;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 회원 객체를 호출하는 메서드
   * searchByDate에서 검색하는 시간(LocalDateTime.now())까지 내역을 검색
   * 계좌 내역을 fetchJoin()으로 즉시 로딩
   *
   * @param loginId      로그인 아이디
   * @param searchByDate 검색 시작 날짜
   * @return 회원 객체
   */
  @Override
  public Member searchByLoginIdAndAccountDetailsToDate(String loginId, LocalDateTime searchByDate) {
    Member findMember = queryFactory.selectFrom(member)
            .leftJoin(member.accountDetails, accountDetail).fetchJoin()
            .where(member.memberId.eq(loginId)
                    .and(accountDetail.accountType.eq(AccountType.CHARGE))
                    .and(accountDetail.isRefunded.eq(false))
                    .and(accountDetail.createdAt.between(searchByDate, LocalDateTime.now())))
            .fetchOne();

    if (findMember == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
    }

    List<AccountDetail> accountDetails = findMember.getAccountDetails();
    if (accountDetails.isEmpty()) {
      throw new CustomException(ErrorCode.ALREADY_REFUNDED);
    }

    long sum = findMember.getAccountDetails().stream()
            .mapToLong(AccountDetail::getAmount)
            .sum();

    if (sum > findMember.getAccount()) {
      throw new CustomException(ErrorCode.ALREADY_SPENT_MONEY);
    }

    return findMember;
  }

  /**
   * DB에서 회원 객체를 호출하는 메서드
   * 회원에 연결된 계좌 내역을 fetchJoin()으로 즉시 로딩
   *
   * @param longinId  로그인 아이디
   * @param accountId 거래 내역 아이디
   * @return 회원 객체
   */
  @Override
  public Member searchByLoginIdAndAccountDetailId(String longinId, Long accountId) {
    Member findMember = queryFactory.selectFrom(member)
            .join(member.accountDetails, accountDetail).fetchJoin()
            .where(member.memberId.eq(longinId)
                    .and(accountDetail.id.eq(accountId)))
            .fetchOne();
    if (findMember == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_ACCOUNT_DETAIL);
    }
    if (findMember.getAccountDetails().get(0).getAccountType() != AccountType.CHARGE) {
      throw new CustomException(ErrorCode.NOT_CHARGE_DETAIL);
    }
    return findMember;
  }

  /**
   * DB에서 회원 객체를 호출하는 메서드
   * 회원에 연결된 계좌 내역, 챌린지를 fetchJoin()으로 즉시 로딩
   *
   * @param loginId 로그인 아이디
   * @return 회원 객체
   */
  @Override
  public Member searchByLoginId(String loginId) {
    Member findMember = queryFactory.selectFrom(member)
            .leftJoin(member.memberChallenges, memberChallenge).fetchJoin()
            .leftJoin(memberChallenge.challenge, challenge).fetchJoin()
            .where(member.memberId.eq(loginId))
            .fetchOne();

    if (findMember == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
    }

    return findMember;
  }


}
