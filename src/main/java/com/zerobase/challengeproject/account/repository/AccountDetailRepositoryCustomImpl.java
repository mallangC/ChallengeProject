package com.zerobase.challengeproject.account.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.account.entity.AccountDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.zerobase.challengeproject.account.entity.QAccountDetail.accountDetail;


@Repository
@RequiredArgsConstructor
public class AccountDetailRepositoryCustomImpl implements AccountDetailRepositoryCustom {
  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 계좌 내역 리스트를 호출하는 메서드
   *
   * @param page    페이지 숫자
   * @param loginId 로그인 아이디
   * @return 페이징된 계좌 내역 정보
   */
  @Override
  public Page<AccountDetail> searchAllAccountDetail(int page, String loginId) {
    Pageable pageable = PageRequest.of(page, 20);
    Long total = queryFactory.select(accountDetail.count())
            .from(accountDetail)
            .where(accountDetail.member.loginId.eq(loginId))
            .fetchOne();

    if (total == null) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    List<AccountDetail> findAccountDetails = queryFactory.selectFrom(accountDetail)
            .where(accountDetail.member.loginId.eq(loginId))
            .orderBy(accountDetail.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    return new PageImpl<>(findAccountDetails, pageable, total);
  }
}
