package com.zerobase.challengeproject.account.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.form.AccountAddForm;
import com.zerobase.challengeproject.account.domain.form.RefundAddForm;
import com.zerobase.challengeproject.account.domain.form.RefundSearchForm;
import com.zerobase.challengeproject.account.domain.form.RefundUpdateForm;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final MemberRepository memberRepository;
  private final AccountDetailRepository accountDetailRepository;
  private final RefundRepository refundRepository;

  public MemberDto getMember(UserDetailsImpl userDetails) {
    return MemberDto.fromWithoutAccountDetails(searchMember(userDetails.getUsername()));
  }

  /**
   * 회원이 계좌 내역을 조회하기 위한 서비스 메서드
   * page를 사용하기 때문에 총 계좌내역 갯수를 알기 위한 쿼리가 따로 실행되어 쿼리가 두번 실행
   * 내역을 찾을 수 없는 경우 빈 페이지로 반환
   *
   * @param page 찾은 계좌 내역 페이지
   * @return 계좌 내역과 총 갯수, 총페이지, 현재 페이지, 한페이지에 표시되는 계좌내역의 갯수 정보
   */
  public BaseResponseDto<PageDto<AccountDetailDto>> getAllAccounts(int page, UserDetailsImpl userDetails) {
    Page<AccountDetailDto> paging = accountDetailRepository.searchAllAccountDetail(page - 1, userDetails.getUsername());
    return new BaseResponseDto<>(PageDto.from(paging)
            , "계좌 내역 조회에 성공했습니다.(" + page + "페이지)"
            , HttpStatus.OK);
  }


  /**
   * 회원이 금액을 충전하기 위한 서비스 메서드
   * 충전 내역이 DB에 저장되고 회원 계좌에 금액이 충전
   *
   * @param form 회원이 충전할 금액
   * @return id, updateAt을 제외한 모든 충전내역
   */
  @Transactional
  public BaseResponseDto<AccountDetailDto> addAmount(AccountAddForm form, UserDetailsImpl userDetails) {
    String userId = userDetails.getUsername();
    Member member = searchMember(userId);

    Long amount = form.getChargeAmount();
    AccountDetail detail = AccountDetail.charge(member, amount);
    accountDetailRepository.save(detail);

    member.chargeAccount(amount);
    return new BaseResponseDto<>(
            AccountDetailDto.from(detail),
            amount + "원 충전을 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 회원의 이전 충전한 금액에 대한 환불신청 서비스 메서드
   * 이미 신청한 환불 내역이 있거나, 충전 내역을 찾을 수 없을 때 예외 발생
   *
   * @param form 환불 신청할 내역id, 환불 사유
   * @return 환불 신청에 대한 정보 (id 제외)
   */
  public BaseResponseDto<RefundDto> addRefund(RefundAddForm form, UserDetailsImpl userDetails) {
    String userId = userDetails.getUsername();
    boolean isExist = refundRepository.existsByAccountDetail_Id(form.getAccountId());
    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_REFUND_REQUEST);
    }
    Member member = memberRepository.searchByLoginIdAndAccountDetailId(userId, form.getAccountId());

    Refund refund = Refund.from(form.getContent(), member);
    refundRepository.save(refund);

    return new BaseResponseDto<>(RefundDto.from(refund),
            "환불 신청을 성공했습니다.",
            HttpStatus.OK);
  }


  public BaseResponseDto<PageDto<RefundDto>> getAllMyRefund(int page, UserDetailsImpl userDetails) {
    String userId = userDetails.getUsername();
    Page<RefundDto> paging = refundRepository.searchAllMyRefund(page - 1, userId);
    return new BaseResponseDto<>(PageDto.from(paging)
            , "회원의 환불신청 조회에 성공했습니다.(" + page + "페이지)"
            , HttpStatus.OK);
  }

  /**
   * 회원이 이전에 신청한 환불신청을 취소하는 서비스 메서드
   * 파라미터로 받은 id의 환불신청이 없으면 예외 발생
   *
   * @param refundId 취소할 환불신청 아이디
   * @return 취소하기 전 환불 신청 정보
   */
  @Transactional
  public BaseResponseDto<RefundDto> cancelRefund(Long refundId) {
    Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REFUND));

    if (refund.isDone()) {
      throw new CustomException(ErrorCode.ALREADY_DONE);
    }

    refundRepository.delete(refund);
    return new BaseResponseDto<>(RefundDto.from(refund),
            "환불 신청을 취소했습니다.",
            HttpStatus.OK);
  }

  /**
   * 관리자가 회원이 신청한 환불을 확인하기 위한 서비스 메서드
   * 환불 신청 내역을 찾을 수 없는 경우 빈 페이지로 반환
   *
   * @param page 페이지 넘버
   * @param form 검색 기준이 되는 날짜(문자열 예- 2025-03-31 00), 두개의 boolean
   * @return paging된 검색 기준에 맞는 Refund 정보
   */
  public BaseResponseDto<PageDto<RefundDto>> getAllRefund(int page, RefundSearchForm form) {
    Page<RefundDto> paging = refundRepository.searchAllRefund(page - 1, form.getStartAtStr(), form.getDone(), form.getRefunded());
    return new BaseResponseDto<>(PageDto.from(paging)
            , "환불 신청 조회에 성공했습니다.(" + page + "페이지)"
            , HttpStatus.OK);
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
  public BaseResponseDto<RefundDto> refundDecision(boolean approval, RefundUpdateForm form) {
    Refund refund = refundRepository.searchRefundById(form.getRefundId());
    AccountDetail accountDetail = refund.getAccountDetail();
    if (accountDetail.getAccountType() != AccountType.CHARGE) {
      throw new CustomException(ErrorCode.NOT_CHARGE_DETAIL);
    }
    if (approval) {
      Member member = memberRepository.searchByLoginIdAndAccountDetailsToDate(
              refund.getMember().getMemberId(),
              accountDetail.getCreatedAt());
      AccountDetail refundDetail = AccountDetail.refund(member, accountDetail.getAmount());
      accountDetailRepository.save(refundDetail);
      member.refundAccount(accountDetail, refund);
      return new BaseResponseDto<>(RefundDto.from(refund),
              "환불 승인을 성공했습니다.",
              HttpStatus.OK);
    }
    refund.refundFalse(form);
    return new BaseResponseDto<>(RefundDto.from(refund),
            "환불 비승인을 성공했습니다.",
            HttpStatus.OK);
  }

  private Member searchMember(String userId) {
    return memberRepository.findByMemberId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
  }
}
