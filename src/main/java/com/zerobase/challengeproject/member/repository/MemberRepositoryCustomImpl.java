package com.zerobase.challengeproject.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.zerobase.challengeproject.account.entity.QAccountDetail.accountDetail;
import static com.zerobase.challengeproject.member.entity.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Member searchByEmailAndAccountDetailsToDate(String email, LocalDateTime searchByDate) {
    Member findMember = queryFactory.selectFrom(member)
            .leftJoin(member.accountDetails, accountDetail).fetchJoin()
            .where(member.loginId.eq(email)
                    .and(accountDetail.isCharge.eq(true))
                    .and(accountDetail.isRefunded.eq(false))
                    .and(accountDetail.createdAt.between(searchByDate, LocalDateTime.now())))
            .fetchOne();

    if (findMember == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_MEMBER);
    }

    List<AccountDetail> accountDetails = findMember.getAccountDetails();
    if (accountDetails.isEmpty()){
      throw new CustomException(ErrorCode.ALREADY_REFUNDED);
    }

    long sum = findMember.getAccountDetails().stream()
            .mapToLong(AccountDetail::getAmount)
            .sum();

    if (sum > findMember.getAccount()){
      throw new CustomException(ErrorCode.ALREADY_SPENT_MONEY);
    }

    return findMember;
  }

  @Override
  public Member searchByEmailAndAccountDetailId(String email, Long accountId) {
    Member findMember = queryFactory.selectFrom(member)
            .join(member.accountDetails, accountDetail).fetchJoin()
            .where(member.loginId.eq(email)
                    .and(accountDetail.id.eq(accountId)))
            .fetchOne();
    if (findMember == null) {
      throw new CustomException(ErrorCode.NOT_FOUND_ACCOUNT_DETAIL);
    }
    if (!findMember.getAccountDetails().get(0).isCharge()){
      throw new CustomException(ErrorCode.NOT_CHARGE_DETAIL);
    }
    return findMember;
  }
}
