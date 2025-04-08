package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.domain.form.WaterChallengeForm;
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
  public ResponseEntity<BaseResponseDto<WaterChallengeDto>> addWaterChallenge(
          @RequestBody @Valid WaterChallengeForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.addWaterChallenge(form, userDetails));
  }

  //오늘의 물마시기 챌린지 확인
  @GetMapping("/{challengeId}")
  public ResponseEntity<BaseResponseDto<WaterChallengeDto>> getWaterChallenge(
          @PathVariable Long challengeId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.getWaterChallenge(challengeId, userDetails));
  }

  //물마시기 챌린지 수정
  @PatchMapping
  public ResponseEntity<BaseResponseDto<WaterChallengeDto>> updateWaterChallenge(
          @RequestBody @Valid WaterChallengeForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(waterChallengeService.updateWaterChallenge(form, userDetails));
  }

}
