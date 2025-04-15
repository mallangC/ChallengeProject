package com.zerobase.challengeproject.account.controller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.PaginatedResponse;
import com.zerobase.challengeproject.account.domain.dto.RefundDto;
import com.zerobase.challengeproject.account.domain.request.RefundSearchRequest;
import com.zerobase.challengeproject.account.domain.request.RefundUpdateRequest;
import com.zerobase.challengeproject.account.service.AccountService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<PaginatedResponse<RefundDto>> getAllRefund(
          @RequestParam @Min(1) int page,
          @RequestBody RefundSearchRequest form) {
    return ResponseEntity.ok(PaginatedResponse.from(
            accountService.getAllRefundForAdmin(page, form),
            "환불 내역 확인 성공",
            HttpStatus.OK));
  }


  /**
   * 관리자는 회원의 환불신청을 승인/비승인 할 수 있다.
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PatchMapping("/refund")
  public ResponseEntity<HttpApiResponse<RefundDto>> refundApproval(
          @RequestParam boolean approval,
          @RequestBody RefundUpdateRequest form) {
    String decision = approval ? "승인" : "비승인";
    return ResponseEntity.ok(new HttpApiResponse<>(
            accountService.refundDecision(approval, form),
            "환불 " + decision + " 성공",
            HttpStatus.OK));
  }
}
