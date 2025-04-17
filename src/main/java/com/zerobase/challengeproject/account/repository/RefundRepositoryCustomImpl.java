package com.zerobase.challengeproject.account.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.challengeproject.account.entity.Refund;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.zerobase.challengeproject.account.entity.QRefund.refund;


@RequiredArgsConstructor
public class RefundRepositoryCustomImpl implements RefundRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  /**
   * DB에서 환불 신청 리스트를 호출하는 메서드
   *
   * @param page       페이지 숫자
   * @param startAt    시작 날짜
   * @param isDone     환불 완료 여부
   * @param isRefunded 환불 여부
   * @return 페이징된 환불 신청 객체
   */
  @Override
  public Page<Refund> searchAllRefund(int page, LocalDateTime startAt, Boolean isDone, Boolean isRefunded) {
    Pageable pageable = PageRequest.of(page, 20);

    BooleanExpression whereClause = refund.createdAt.loe(LocalDateTime.now());
    if (startAt != null) {
      whereClause = refund.createdAt.between(startAt, LocalDateTime.now());
    }

    Long total = queryFactory.select(refund.count())
            .from(refund)
            .where(whereClause
                    .and(isRefunded == null ? null : refund.isRefunded.eq(isRefunded))
                    .and(isDone == null ? null : refund.isDone.eq(isDone)))
            .fetchOne();

    if (total == null) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    List<Refund> findRefunds = queryFactory.selectFrom(refund)
            .join(refund.member).fetchJoin()
            .join(refund.accountDetail).fetchJoin()
            .where(whereClause
                    .and(isRefunded == null ? null : refund.isRefunded.eq(isRefunded))
                    .and(isDone == null ? null : refund.isDone.eq(isDone)))
            .orderBy(refund.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    return new PageImpl<>(findRefunds, pageable, total);
  }

  /**
   * DB에서 환불 신청을 호출하는 메서드
   * 환불 신청에 연결된 회원, 계좌 내역을 fetchJoin()으로 즉시 로딩
   *
   * @param refundId 환불 아이디
   * @return 환불 신청 객체
   */
  @Override
  public Optional<Refund> searchRefundById(Long refundId) {
    Refund findRefund = queryFactory.selectFrom(refund)
            .join(refund.accountDetail).fetchJoin()
            .join(refund.member).fetchJoin()
            .where(refund.id.eq(refundId))
            .fetchOne();
    return Optional.ofNullable(findRefund);
  }

  /**
   * DB에서 환불 신청 리스트를 호출하는 메서드
   * 환불 신청에 연결된 회원, 계좌 내역을 fetchJoin()으로 즉시 로딩
   *
   * @param page    페이지 숫자
   * @param loginId 로그인 아이디
   * @return 페이징된 환불 신청 객체
   */
  @Override
  public Page<Refund> searchAllMyRefund(int page, String loginId) {
    Pageable pageable = PageRequest.of(page, 20);

    Long total = queryFactory.select(refund.count())
            .from(refund)
            .where(refund.member.loginId.eq(loginId))
            .fetchOne();

    if (total == null) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    List<Refund> findRefunds = queryFactory.selectFrom(refund)
            .join(refund.member).fetchJoin()
            .join(refund.accountDetail).fetchJoin()
            .where(refund.member.loginId.eq(loginId))
            .orderBy(refund.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    return new PageImpl<>(findRefunds, pageable, total);
  }

  /**
   * DB에서 환불 신청 객체를 호출하는 메서드
   *
   * @param refundId 환불 신청 아이디
   * @param loginId  로그인 아이디
   * @return 환불 신청 객체
   */
  @Override
  public Optional<Refund> searchRefundByIdAndLoginId(Long refundId, String loginId) {
    Refund findRefund = queryFactory.selectFrom(refund)
            .where(refund.id.eq(refundId)
                    .and(refund.member.loginId.eq(loginId)))
            .fetchOne();
    return Optional.ofNullable(findRefund);
  }

}
