package com.zerobase.challengeproject.account.controller;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.form.RefundSearchForm;
import com.zerobase.challengeproject.account.domain.form.RefundUpdateForm;
import com.zerobase.challengeproject.account.service.AccountService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/account")
public class AccountAdminController {

  private final AccountService accountService;

  /**
   * 관리자가 환불 내역 확인
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/refund")
  public ResponseEntity<BaseResponseDto<PageDto<RefundDto>>> getAllRefund(
          @RequestParam @Min(1) int page,
          @RequestBody RefundSearchForm form) {
    return ResponseEntity.ok(accountService.getAllRefund(page, form));
  }


  /**
   * 관리자는 회원의 환불신청을 승인/비승인 할 수 있다.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PatchMapping("/refund")
  public ResponseEntity<BaseResponseDto<RefundDto>> refundApproval(
          @RequestParam boolean approval,
          @RequestBody RefundUpdateForm form) {
    return ResponseEntity.ok(accountService.refundDecision(approval, form));
  }
}
