package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterCommentDto;
import com.zerobase.challengeproject.comment.service.WaterChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class CommentAdminController {
  private final WaterChallengeService waterChallengeService;

  //물마시기 챌린지 조회(관리자)
  @GetMapping("/challenge/water/{challengeId}")
  public ResponseEntity<BaseResponseDto<PageDto<WaterChallengeDto>>> getAllWaterChallenge(
          @PathVariable Long challengeId,
          @RequestParam(defaultValue = "1") @Min(1) int page,
          @RequestParam(required = false) Boolean isPass,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.getAllWaterChallenge(page, challengeId, isPass, userDetails));
  }


  //물마시기 댓글 삭제(관리자)
  @DeleteMapping("/challenge/water/comment/{commentId}")
  public ResponseEntity<BaseResponseDto<WaterCommentDto>> deleteWaterComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.deleteWaterComment(commentId, userDetails));
  }


}
