package com.zerobase.challengeproject.account.service;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.request.AccountAddRequest;
import com.zerobase.challengeproject.account.domain.request.RefundAddRequest;
import com.zerobase.challengeproject.account.domain.request.RefundSearchRequest;
import com.zerobase.challengeproject.account.domain.request.RefundUpdateRequest;
import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.account.entity.Refund;
import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.account.repository.RefundRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberDto;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final MemberRepository memberRepository;
  private final AccountDetailRepository accountDetailRepository;
  private final RefundRepository refundRepository;

  public MemberDto getMember(UserDetailsImpl userDetails) {
    return MemberDto.fromWithoutAccountDetails(
            memberRepository.findByLoginId(userDetails.getUsername())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)));
  }

  /**
   * 회원이 계좌 내역을 조회하기 위한 서비스 메서드
   * page를 사용하기 때문에 총 계좌내역 갯수를 알기 위한 쿼리가 따로 실행되어 쿼리가 두번 실행
   * 내역을 찾을 수 없는 경우 빈 페이지로 반환
   *
   * @param page 찾은 계좌 내역 페이지
   * @return 계좌 내역과 총 갯수, 총페이지, 현재 페이지, 한페이지에 표시되는 계좌내역의 갯수 정보
   */
  public HttpApiResponse<PageDto<AccountDetailDto>> getAllAccounts(int page, UserDetailsImpl userDetails) {
    Page<AccountDetailDto> paging = accountDetailRepository.searchAllAccountDetail(page - 1, userDetails.getUsername());
    return new HttpApiResponse<>(PageDto.from(paging)
            , "계좌 내역 조회에 성공했습니다.(" + page + "페이지)"
            , HttpStatus.OK);
  }

  /**
   * 회원이 금액을 충전하기 위한 서비스 메서드
   * 충전 내역이 DB에 저장되고 회원 계좌에 금액이 충전
   *
   * @param request 회원이 충전할 금액
   * @param member  회원 객체
   * @return id, updateAt을 제외한 모든 충전내역
   */
  @Transactional
  public AccountDetailDto addAmount(AccountAddRequest request, Member member) {
    Long amount = request.getChargeAmount();
    AccountDetail detail = AccountDetail.charge(member, amount);
    accountDetailRepository.save(detail);
    member.chargeAccount(amount);
    return AccountDetailDto.from(detail);
  }

  /**
   * 회원이 계좌 내역을 조회하기 위한 서비스 메서드
   * page를 사용하기 때문에 총 계좌내역 갯수를 알기 위한 쿼리가 따로 실행되어 쿼리가 두번 실행
   * 내역을 찾을 수 없는 경우 빈 페이지로 반환
   *
   * @param page 찾은 계좌 내역 페이지
   * @return 계좌 내역과 총 갯수, 총페이지, 현재 페이지, 한페이지에 표시되는 계좌내역의 갯수 정보
   */
  public Page<AccountDetailDto> getAllAccounts(int page, String loginId) {
    Page<AccountDetail> accountDetails = accountDetailRepository.searchAllAccountDetail(page - 1, loginId);

    List<AccountDetailDto> accountDetailDtos =
            accountDetails.getContent().stream()
                    .map(AccountDetailDto::from)
                    .toList();

    return new PageImpl<>(accountDetailDtos,
            accountDetails.getPageable(),
            accountDetails.getTotalElements());
  }

  /**
   * 회원의 이전 충전한 금액에 대한 환불신청 서비스 메서드
   * 이미 신청한 환불 내역이 있거나, 충전 내역을 찾을 수 없을 때 예외 발생
   *
   * @param form 환불 신청할 내역id, 환불 사유
   * @return 환불 신청에 대한 정보 (id 제외)
   */
  public RefundDto addRefund(RefundAddRequest form, String loginId) {
    boolean isExist = refundRepository.existsByAccountDetail_Id(form.getAccountId());
    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_REFUND_REQUEST);
    }
    Member member = memberRepository.searchByLoginIdAndAccountDetailId(loginId, form.getAccountId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACCOUNT_DETAIL));

    if (member.getAccountDetails().get(0).getAccountType() != AccountType.CHARGE) {
      throw new CustomException(ErrorCode.NOT_CHARGE_DETAIL);
    }

    Refund refund = Refund.from(form.getContent(), member);
    refundRepository.save(refund);
    return RefundDto.from(refund);
  }

  /**
   * 회원이 신청한 환불 신청내역을 모두 조회하는 서비스 메서드
   * 회원을 찾을 수 없을 경우 예외 발생
   *
   * @param page    페이지 숫자
   * @param loginId 회원 로그인 아이디
   * @return 페이징된 환불 신청 내역
   */
  public Page<RefundDto> getAllRefund(int page, String loginId) {
    Page<Refund> refunds = refundRepository.searchAllMyRefund(page - 1, loginId);
    List<RefundDto> refundDtos = refunds.stream()
            .map(RefundDto::from)
            .toList();
    return new PageImpl<>(refundDtos, refunds.getPageable(), refunds.getTotalElements());
  }

  /**
   * 회원이 이전에 신청한 환불신청을 취소하는 서비스 메서드
   * 파라미터로 받은 id의 환불신청이 없으면 예외 발생
   *
   * @param refundId 취소할 환불신청 아이디
   * @return 취소하기 전 환불 신청 정보
   */
  @Transactional
  public RefundDto cancelRefund(Long refundId, String loginId) {
    Refund refund = refundRepository.searchRefundByIdAndLoginId(refundId, loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REFUND));
    if (refund.getMember().getLoginId().equals(loginId)) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_REFUND);
    }
    if (refund.isDone()) {
      throw new CustomException(ErrorCode.ALREADY_DONE);
    }
    refundRepository.delete(refund);
    return RefundDto.from(refund);
  }

  /**
   * 관리자가 회원이 신청한 환불을 확인하기 위한 서비스 메서드
   * 환불 신청 내역을 찾을 수 없는 경우 빈 페이지로 반환
   *
   * @param page 페이지 넘버
   * @param form 검색 기준이 되는 날짜(문자열 예- 2025-03-31 00), 두개의 boolean
   * @return paging된 검색 기준에 맞는 Refund 정보
   */
  public Page<RefundDto> getAllRefundForAdmin(int page, RefundSearchRequest form) {
    Page<Refund> refunds = refundRepository.searchAllRefund(
            page - 1, form.getStartAtStr(), form.getDone(), form.getRefunded());

    List<RefundDto> refundDtos = refunds.stream()
            .map(RefundDto::from)
            .toList();
    return new PageImpl<>(refundDtos, refunds.getPageable(), refunds.getTotalElements());
  }


  /**
   * 관리자가 회원이 한 환불 신청을 승인/ 비승인 하기위한 서비스 메서드
   * 환불할 충전 내역이 없거나, 이미 환불 받았거나, 충전 내역이 아니거나,
   * 환불할 충전 내역과 내역 이후에 충전한 전체 금액이(이미 환불된건 제외)이
   * 지금 계좌에 있는금액보다 크면(이미 사용했다고 판단) 예외 발생
   * 승인시
   * Refund에 isDone, isRefunded & AccountDetail에 isRefunded 모두 true
   * Refund amdinContent 를 "환불 완료" 변경
   * 비승인시
   * Refund에 isDone = true, adminContent에 form에 있는 content로 변경
   *
   * @param approval 승인/ 비승인 확인
   * @param form     환불 신청한 아이디,
   * @return updateAt을 제외한 모든 환불 내역
   */
  @Transactional
  public RefundDto refundDecision(boolean approval, RefundUpdateRequest form) {
    Refund refund = refundRepository.searchRefundById(form.getRefundId())
            .orElseThrow(() -> new CustomException(ErrorCode.ALREADY_REFUND_REQUEST));
    verifyRefundDetail(refund);
    AccountDetail accountDetail = refund.getAccountDetail();
    if (accountDetail.getAccountType() != AccountType.CHARGE) {
      throw new CustomException(ErrorCode.NOT_CHARGE_DETAIL);
    }
    if (approval) {
      Member member = memberRepository.searchByLoginIdAndAccountDetailsToDate(
                      refund.getMember().getLoginId(),
                      accountDetail.getCreatedAt())
              .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
      verifyAccountDetails(member);

      AccountDetail refundDetail = AccountDetail.refund(member, accountDetail.getAmount());
      accountDetailRepository.save(refundDetail);
      member.refundAccount(accountDetail, refund);
    } else {
      refund.rejectRefund(form);
    }
    return RefundDto.from(refund);
  }

  private void verifyRefundDetail(Refund refund) {
    if (refund.isRefunded()) {
      throw new CustomException(ErrorCode.ALREADY_REFUNDED);
    }
    if (refund.isDone()) {
      throw new CustomException(ErrorCode.ALREADY_DONE);
    }
  }

  private void verifyAccountDetails(Member member) {
    List<AccountDetail> accountDetails = member.getAccountDetails();
    if (accountDetails.isEmpty()) {
      throw new CustomException(ErrorCode.ALREADY_REFUNDED);
    }

    long sum = accountDetails.stream()
            .mapToLong(AccountDetail::getAmount)
            .sum();
    if (member.getAccount() < sum) {
      throw new CustomException(ErrorCode.ALREADY_SPENT_MONEY);
    }
  }

}
