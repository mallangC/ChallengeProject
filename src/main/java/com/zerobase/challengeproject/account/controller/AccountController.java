package com.zerobase.challengeproject.account.controller;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.form.AccountAddForm;
import com.zerobase.challengeproject.account.domain.form.RefundAddForm;
import com.zerobase.challengeproject.account.domain.form.RefundSearchForm;
import com.zerobase.challengeproject.account.domain.form.RefundUpdateForm;
import com.zerobase.challengeproject.account.service.AccountService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

  private final AccountService accountService;

  /**
   * 회원 조회(계좌 확인을 위해 구현)
   */
  @GetMapping("/member")
  public ResponseEntity<MemberDto> getAccountDetail(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(accountService.getMember(userDetails));
  }

  /**
   * 회원 계좌에 금액 충전
   */
  @PostMapping
  public ResponseEntity<BaseResponseDto<AccountDetailDto>> addAmount(
          @Valid @RequestBody AccountAddForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(accountService.addAmount(form, userDetails));
  }

  /**
   * 전체 계좌 내역 조회 (페이징)
   */
  @GetMapping
  public ResponseEntity<BaseResponseDto<PageDto<AccountDetailDto>>> getAllAccountDetail(
          @RequestParam @Min(1) int page,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(accountService.getAllAccounts(page, userDetails));
  }


  /**
   * 회원이 충전했던 금액을 환불 신청
   */
  @PostMapping("/refund")
  public ResponseEntity<BaseResponseDto<RefundDto>> refundRequest(
          @RequestBody RefundAddForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(accountService.addRefund(form, userDetails));
  }


  /**
   * 회원의 환불 신청 취소
   */
  @DeleteMapping("/refund")
  public ResponseEntity<BaseResponseDto<RefundDto>> cancelRefundRequest(
          @RequestParam("id") Long refundId) {
    return ResponseEntity.ok(accountService.cancelRefund(refundId));
  }

  /**
   * 회원의 환불 신청 확인
   */
  @GetMapping("/refund")
  public ResponseEntity<BaseResponseDto<PageDto<RefundDto>>> getAllRefund(
          @RequestParam @Min(1) int page,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(accountService.getAllMyRefund(page, userDetails));
  }


  /**
   * 관리자가 환불 내역 확인
   */
//  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Secured("ADMIN")
  @GetMapping("/refund/admin")
  public ResponseEntity<BaseResponseDto<PageDto<RefundDto>>> getAllRefund(
          @RequestParam @Min(1) int page,
          @RequestBody RefundSearchForm form) {
    return ResponseEntity.ok(accountService.getAllRefund(page, form));
  }


  /**
   * 관리자는 회원의 환불신청을 승인/비승인 할 수 있다.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PatchMapping("/refund/admin")
  public ResponseEntity<BaseResponseDto<RefundDto>> refundApproval(
          @RequestParam boolean approval,
          @RequestBody RefundUpdateForm form) {
    return ResponseEntity.ok(accountService.refundDecision(approval, form));
  }



}
