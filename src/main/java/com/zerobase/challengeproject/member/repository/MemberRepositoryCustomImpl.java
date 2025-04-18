package com.zerobase.challengeproject.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.AccountType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.challengeproject.account.entity.QAccountDetail.accountDetail;
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
  public Optional<Member> searchByLoginIdAndAccountDetailsToDate(String loginId, LocalDateTime searchByDate) {
    Member findMember = queryFactory.selectFrom(member)
            .leftJoin(member.accountDetails, accountDetail).fetchJoin()
            .where(member.loginId.eq(loginId)
                    .and(accountDetail.accountType.eq(AccountType.CHARGE))
                    .and(accountDetail.isRefunded.eq(false))
                    .and(accountDetail.createdAt.between(searchByDate, LocalDateTime.now())))
            .fetchOne();
    return Optional.ofNullable(findMember);
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
  public Optional<Member> searchByLoginIdAndAccountDetailId(String longinId, Long accountId) {
    Member findMember = queryFactory.selectFrom(member)
            .join(member.accountDetails, accountDetail).fetchJoin()
            .where(member.loginId.eq(longinId)
                    .and(accountDetail.id.eq(accountId)))
            .fetchOne();
    return Optional.ofNullable(findMember);
  }
}
