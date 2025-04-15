package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterCommentDto;
import com.zerobase.challengeproject.comment.domain.form.WaterChallengeForm;
import com.zerobase.challengeproject.comment.domain.form.WaterCommentAddForm;
import com.zerobase.challengeproject.comment.domain.form.WaterCommentUpdateForm;
import com.zerobase.challengeproject.comment.service.WaterChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/water")
public class WaterChallengeController {

  private final WaterChallengeService waterChallengeService;

  //물마시기 챌린지 추가
  @PostMapping
  public ResponseEntity<HttpApiResponse<WaterChallengeDto>> addWaterChallenge(
          @RequestBody @Valid WaterChallengeForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.addWaterChallenge(form, userDetails));
  }

  //오늘의 물마시기 챌린지 확인
  @GetMapping("/{challengeId}")
  public ResponseEntity<HttpApiResponse<WaterChallengeDto>> getWaterChallenge(
          @PathVariable Long challengeId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.getWaterChallenge(challengeId, userDetails));
  }

  //물마시기 챌린지 수정
  @PatchMapping
  public ResponseEntity<HttpApiResponse<WaterChallengeDto>> updateWaterChallenge(
          @RequestBody @Valid WaterChallengeForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.updateWaterChallenge(form, userDetails));
  }

  //물마시기 댓글 추가
  @PostMapping("/comment")
  public ResponseEntity<HttpApiResponse<WaterCommentDto>> addWaterComment(
          @RequestBody @Valid WaterCommentAddForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.addWaterComment(form, userDetails));
  }

  //물마시기 댓글 단건 확인
  @GetMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiResponse<WaterCommentDto>> getWaterComment(
          @PathVariable Long commentId) {
    return ResponseEntity.ok(waterChallengeService.getWaterComment(commentId));
  }

  //물마시기 댓글 추가
  @PatchMapping("/comment")
  public ResponseEntity<HttpApiResponse<WaterCommentDto>> updateWaterComment(
          @RequestBody @Valid WaterCommentUpdateForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.updateWaterComment(form, userDetails));
  }

}
