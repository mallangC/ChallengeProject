package com.zerobase.challengeproject.account.controller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.PaginatedResponse;
import com.zerobase.challengeproject.account.domain.dto.AccountDetailDto;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.request.AccountAddRequest;
import com.zerobase.challengeproject.account.domain.request.RefundAddRequest;
import com.zerobase.challengeproject.account.domain.request.RefundSearchRequest;
import com.zerobase.challengeproject.account.domain.request.RefundUpdateRequest;
import com.zerobase.challengeproject.account.service.AccountService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

  private final AccountService accountService;

  /**
   * 회원 계좌에 금액 충전
   */
  @PostMapping
  public ResponseEntity<HttpApiResponse<AccountDetailDto>> addAmount(
          @Valid @RequestBody AccountAddRequest request,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    return ResponseEntity.ok(new HttpApiResponse<>(
            accountService.addAmount(request, member),
            request.getChargeAmount() + "원 충전 성공",
            HttpStatus.OK));
  }

  /**
   * 전체 계좌 내역 조회 (페이징)
   */
  @GetMapping
  public ResponseEntity<PaginatedResponse<AccountDetailDto>> getAllAccountDetail(
          @RequestParam @Min(1) int page,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    Page<AccountDetailDto> accountDetailList =
            accountService.getAllAccounts(page, userDetails.getUsername());
    return ResponseEntity.ok(PaginatedResponse.from(
            accountDetailList,
            "계좌 내역 조회 성공",
            HttpStatus.OK));
  }


  /**
   * 회원이 충전했던 금액을 환불 신청
   */
  @PostMapping("/refund")
  public ResponseEntity<HttpApiResponse<RefundDto>> refundRequest(
          @RequestBody RefundAddRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            accountService.addRefund(form, userDetails.getUsername()),
            "환불 신청 성공",
            HttpStatus.OK));
  }


  /**
   * 회원의 환불 신청 확인
   */
  @GetMapping("/refund")
  public ResponseEntity<PaginatedResponse<RefundDto>> getAllRefundForAdmin(
          @RequestParam @Min(1) int page,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(PaginatedResponse.from(
            accountService.getAllRefund(page, userDetails.getUsername()),
            "회원 환불 신청 조회 성공",
            HttpStatus.OK));
  }


  /**
   * 회원의 환불 신청 취소
   */
  @DeleteMapping("/refund")
  public ResponseEntity<HttpApiResponse<RefundDto>> cancelRefundRequest(
          @RequestParam("id") Long refundId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            accountService.cancelRefund(refundId, userDetails.getUsername()),
            "환불 신청 취소",
            HttpStatus.OK));
  }


  /**
   * 관리자가 환불 내역 확인
   */
  @Secured("ADMIN")
  @GetMapping("/refund/admin")
  public ResponseEntity<PaginatedResponse<RefundDto>> getAllRefundForAdmin(
          @RequestParam @Min(1) int page,
          @RequestBody RefundSearchRequest form) {
    return ResponseEntity.ok(PaginatedResponse.from(
            accountService.getAllRefundForAdmin(page, form),
            "관리자 환불 신청 조회 성공",
            HttpStatus.OK));
  }


  /**
   * 관리자는 회원의 환불신청을 승인/비승인 할 수 있다.
   */
  @Secured("ADMIN")
  @PatchMapping("/refund/admin")
  public ResponseEntity<HttpApiResponse<RefundDto>> refundApprovalForAdmin(
          @RequestBody RefundUpdateRequest form) {
    String message = form.getApproval() ? "관리자 환불 승인 성공" : "관리자 환불 비승인 성공";
    return ResponseEntity.ok(new HttpApiResponse<>(
            accountService.refundDecision(form),
            message,
            HttpStatus.OK));
  }


}
