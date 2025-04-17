package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.HttpApiPageResponse;
import com.zerobase.challengeproject.comment.domain.dto.DietChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.DietCommentDto;
import com.zerobase.challengeproject.comment.domain.form.DietChallengeAddForm;
import com.zerobase.challengeproject.comment.domain.form.DietChallengeUpdateForm;
import com.zerobase.challengeproject.comment.domain.form.DietCommentAddForm;
import com.zerobase.challengeproject.comment.domain.form.DietCommentUpdateForm;
import com.zerobase.challengeproject.comment.service.DietChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/challenge/diet")
public class DietChallengeController {

  private final DietChallengeService dietChallengeService;

  /**
   * 다이어트 챌린지 추가 컨트롤러 메서드(참여할 때 작성)
   *
   * @param form        챌린지아이디, 이미지 주소, 목표 몸무게, 현재 몸무게
   * @param userDetails 회원 정보
   * @return 다이어트 챌린지 정보
   */
  @PostMapping
  public ResponseEntity<HttpApiPageResponse<DietChallengeDto>> addDietChallenge(
          @RequestBody @Valid DietChallengeAddForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.addDietChallenge(form, userDetails));
  }

  /**
   * 회원 본인이 작성한 다이어트 챌린지 조회 컨트롤러 메서드
   *
   * @param challengeId 챌린지 아이디
   * @param userDetails 유저 정보
   * @return 다이어트 챌린지 정보
   */
  @GetMapping("/{challengeId}")
  public ResponseEntity<HttpApiPageResponse<DietChallengeDto>> getDietChallenge(
          @PathVariable Long challengeId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.getDietChallenge(challengeId, userDetails));
  }


  /**
   * 다이어트 챌린지 수정 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 목표 몸무게, 현재 몸무게
   * @param userDetails 회원 정보
   * @return 수정된 다이어트 챌린지 정보
   */
  @PatchMapping
  public ResponseEntity<HttpApiPageResponse<DietChallengeDto>> updateDietChallenge(
          @RequestBody @Valid DietChallengeUpdateForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.updateDietChallenge(form, userDetails));
  }


  /**
   * 다이어트 댓글 추가 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param userDetails 회원 정보
   * @return 추가된 다이어트 댓글 정보
   */
  @PostMapping("/comment")
  public ResponseEntity<HttpApiPageResponse<DietCommentDto>> addComment(
          @RequestBody @Valid DietCommentAddForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.addDietComment(form, userDetails));
  }

  /**
   * 다이어트 댓글 단건 조회 컨트롤러 메서드
   *
   * @param commentId 댓글 아이디
   * @return 조회한 다이어트 댓글 정보
   */
  @GetMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiPageResponse<DietCommentDto>> getComment(
          @PathVariable Long commentId) {
    return ResponseEntity.ok(dietChallengeService.getDietComment(commentId));
  }

  /**
   * 다이어트 댓글 수정 컨트롤러 메서드
   *
   * @param form        댓글 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param userDetails 회원 정보
   * @return 수정한 다이어트 댓글 정보
   */
  @PatchMapping("/comment")
  public ResponseEntity<HttpApiPageResponse<DietCommentDto>> updateComment(
          @RequestBody @Valid DietCommentUpdateForm form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.updateDietComment(form, userDetails));
  }


  /**
   * 다이어트 댓글 삭제 컨트롤러 메서드
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 다이어트 댓글 정보
   */
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiPageResponse<DietCommentDto>> deleteComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(dietChallengeService.deleteDietComment(commentId, userDetails));
  }

}
