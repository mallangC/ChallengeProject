package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterCommentDto;
import com.zerobase.challengeproject.comment.domain.request.WaterChallengeRequest;
import com.zerobase.challengeproject.comment.domain.request.WaterCommentAddRequest;
import com.zerobase.challengeproject.comment.domain.request.WaterCommentUpdateRequest;
import com.zerobase.challengeproject.comment.service.WaterChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/water")
public class WaterChallengeController {

  private final WaterChallengeService waterChallengeService;

  /**
   * 물마시기 챌린지 추가 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 하루 목표 섭취량
   * @param userDetails 회원 정보
   * @return 추가한 물마시기 챌린지 정보
   */
  @PostMapping
  public ResponseEntity<HttpApiResponse<WaterChallengeDto>> addWaterChallenge(
          @RequestBody @Valid WaterChallengeRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            waterChallengeService.addWaterChallenge(form, userDetails.getMember()),
            "물마시기 챌린지 추가 성공",
            HttpStatus.OK));
  }

  /**
   * 오늘의 물마시기 챌린지 조회 컨트롤러 메서드
   *
   * @param challengeId 챌린지 아이디
   * @param userDetails 회원 정보
   * @return 물마시기 챌린지 정보
   */
  @GetMapping("/{challengeId}")
  public ResponseEntity<HttpApiResponse<WaterChallengeDto>> getWaterChallenge(
          @PathVariable Long challengeId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            waterChallengeService.getWaterChallenge(challengeId, userDetails.getMember()),
            "오늘의 물마시기 챌린지 조회 성공",
            HttpStatus.OK));
  }

  /**
   * 물마시기 챌린지 수정 컨트롤러 메서드
   * (DB호출 2회) 호출 1, 수정 1
   *
   * @param form        챌린지 아이디, 목표 섭취량
   * @param userDetails 회원 정보
   * @return 수정된 물마시기 챌린지 정보
   */
  @PatchMapping
  public ResponseEntity<HttpApiResponse<WaterChallengeDto>> updateWaterChallenge(
          @RequestBody @Valid WaterChallengeRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            waterChallengeService.updateWaterChallenge(form, userDetails.getUsername()),
            "물마시기 챌린지 수정 성공",
            HttpStatus.OK));
  }

  /**
   * 물마시기 댓글 추가 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 현재 섭취량, 이미지
   * @param userDetails 회원 정보
   * @return 추가된 물마시기 댓글 정보
   */
  @PostMapping("/comment")
  public ResponseEntity<HttpApiResponse<WaterCommentDto>> addWaterComment(
          @RequestBody @Valid WaterCommentAddRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            waterChallengeService.addWaterComment(form, userDetails.getMember()),
            "물마시기 댓글 추가 성공",
            HttpStatus.OK));
  }

  /**
   * 물마시기 댓글 단건 조회 컨트롤러 메서드
   *
   * @param commentId 댓글 아이디
   * @return 물마시기 댓글 정보
   */
  @GetMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiResponse<WaterCommentDto>> getWaterComment(
          @PathVariable Long commentId) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            waterChallengeService.getWaterComment(commentId),
            "물마시기 댓글 단건 조회 성공"
            , HttpStatus.OK));
  }

  /**
   * 물마시기 댓글 수정 컨트롤러 메서드
   *
   * @param form        댓글 아이디, 현재 섭취량, 이미지
   * @param userDetails 로그인 아이디
   * @return 수정된 물마시기 댓글 정보
   */
  @PatchMapping("/comment")
  public ResponseEntity<HttpApiResponse<WaterCommentDto>> updateWaterComment(
          @RequestBody @Valid WaterCommentUpdateRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            waterChallengeService.updateWaterComment(form, userDetails.getUsername()),
            "물마시기 댓글 수정 성공",
            HttpStatus.OK));
  }

  /**
   * 물마시기 댓글 삭제 컨트롤러 메서드
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 물마시기 댓글 정보
   */
  @DeleteMapping("/{commentId}")
  public ResponseEntity<HttpApiResponse<WaterCommentDto>> deleteWaterComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            waterChallengeService.deleteWaterComment(commentId, userDetails.getUsername()),
            "물마시기 댓글 삭제 성공",
            HttpStatus.OK));
  }

}
