package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.HttpApiPageResponse;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.comment.domain.dto.*;
import com.zerobase.challengeproject.comment.service.CoteChallengeService;
import com.zerobase.challengeproject.comment.service.DietChallengeService;
import com.zerobase.challengeproject.comment.service.WaterChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/challenge")
public class AdminChallengeController {
  private final WaterChallengeService waterChallengeService;
  private final CoteChallengeService coteChallengeService;
  private final DietChallengeService dietChallengeService;

  /**
   * 코테 챌린지 인증 댓글 삭제 컨트롤러 메서드 (관리자)
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 인증 댓글 정보
   */
  @DeleteMapping("/cote/comment/{commentId}")
  public ResponseEntity<HttpApiPageResponse<CoteCommentDto>> adminDeleteComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(coteChallengeService.adminDeleteComment(commentId, userDetails));
  }

  /**
   * 다이어트 챌린지 전체를 조회 컨트롤러 메서드 (관리자)
   *
   * @param page        페이지 번호
   * @param challengeId 챌린지 아이디
   * @return 페이징이된 다이어트 챌린지 리스트
   */
  @GetMapping("/diet/{challengeId}")
  public ResponseEntity<HttpApiPageResponse<PageDto<DietChallengeDto>>> getAllDietChallenge(
          @RequestParam(defaultValue = "1") @Min(1) int page,
          @RequestParam(required = false, value = "pass") Boolean isPass,
          @PathVariable Long challengeId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.getAllDietChallenge(page, challengeId, isPass, userDetails));
  }

  /**
   * 다이어트 댓글 삭제 컨트롤러 메서드 (관리자)
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 다이어트 댓글 정보
   */
  @DeleteMapping("/diet/comment/{commentId}")
  public ResponseEntity<HttpApiPageResponse<DietCommentDto>> deleteComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.adminDeleteDietComment(commentId, userDetails));
  }


  /**
   * 물마시기 챌린지 전체 확인 컨트롤러 메서드 (관리자)
   *
   * @param page        페이지 숫자
   * @param challengeId 챌린지 아이디
   * @param isPass      챌린지 성공 여부
   * @param userDetails 회원 정보
   * @return 페이징된 물마시기 챌린지
   */
  @GetMapping("/water/{challengeId}")
  public ResponseEntity<HttpApiPageResponse<PageDto<WaterChallengeDto>>> getAllWaterChallenge(
          @PathVariable Long challengeId,
          @RequestParam(defaultValue = "1") @Min(1) int page,
          @RequestParam(required = false, value = "pass") Boolean isPass,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.getAllWaterChallenge(page, challengeId, isPass, userDetails));
  }


  /**
   * 물마시기 댓글 삭제 컨트롤러 메서드 (관리자)
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 물마시기 댓글 정보
   */
  @DeleteMapping("/water/comment/{commentId}")
  public ResponseEntity<HttpApiPageResponse<WaterCommentDto>> deleteWaterComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.adminDeleteWaterComment(commentId, userDetails));
  }


  //오늘 물마시기 챌린지 모두 추가(테스트용)
  @PostMapping("/water/addall")
  public ResponseEntity<String> addAllWaterChallenge() {
    waterChallengeService.addAllWaterChallenge();
    return ResponseEntity.ok("모두 추가 완료");
  }

}
