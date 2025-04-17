package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.comment.domain.dto.DietChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.DietCommentDto;
import com.zerobase.challengeproject.comment.domain.request.DietChallengeAddRequest;
import com.zerobase.challengeproject.comment.domain.request.DietChallengeUpdateRequest;
import com.zerobase.challengeproject.comment.domain.request.DietCommentAddRequest;
import com.zerobase.challengeproject.comment.domain.request.DietCommentUpdateRequest;
import com.zerobase.challengeproject.comment.service.DietChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<HttpApiResponse<DietChallengeDto>> addDietChallenge(
          @RequestBody @Valid DietChallengeAddRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            dietChallengeService.addDietChallenge(form, userDetails.getMember()),
            "다이어트 챌린지 추가를 성공했습니다.",
            HttpStatus.OK));
  }

  /**
   * 회원 본인이 작성한 다이어트 챌린지 조회 컨트롤러 메서드
   *
   * @param challengeId 챌린지 아이디
   * @param userDetails 회원 정보
   * @return 다이어트 챌린지 정보
   */
  @GetMapping("/{challengeId}")
  public ResponseEntity<HttpApiResponse<DietChallengeDto>> getDietChallenge(
          @PathVariable Long challengeId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            dietChallengeService.getDietChallenge(challengeId, userDetails.getUsername()),
            "다이어트 챌린지 단건 조회를 성공했습니다.",
            HttpStatus.OK));
  }


  /**
   * 다이어트 챌린지 수정 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 목표 몸무게, 현재 몸무게
   * @param userDetails 회원 정보
   * @return 수정된 다이어트 챌린지 정보
   */
  @PatchMapping
  public ResponseEntity<HttpApiResponse<DietChallengeDto>> updateDietChallenge(
          @RequestBody @Valid DietChallengeUpdateRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            dietChallengeService.updateDietChallenge(form, userDetails.getUsername()),
            "다이어트 챌린지 수정을 성공했습니다.",
            HttpStatus.OK));
  }


  /**
   * 다이어트 댓글 추가 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param userDetails 회원 정보
   * @return 추가된 다이어트 댓글 정보
   */
  @PostMapping("/comment")
  public ResponseEntity<HttpApiResponse<DietCommentDto>> addComment(
          @RequestBody @Valid DietCommentAddRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            dietChallengeService.addDietComment(form, userDetails.getMember()),
            "다이어트 댓글 추가를 성공했습니다.",
            HttpStatus.OK));
  }

  /**
   * 다이어트 댓글 단건 조회 컨트롤러 메서드
   *
   * @param commentId 댓글 아이디
   * @return 조회한 다이어트 댓글 정보
   */
  @GetMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiResponse<DietCommentDto>> getComment(
          @PathVariable Long commentId) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            dietChallengeService.getDietComment(commentId),
            "다이어트 댓글 조회를 성공했습니다.",
            HttpStatus.OK));
  }

  /**
   * 다이어트 댓글 수정 컨트롤러 메서드
   *
   * @param form        댓글 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param userDetails 회원 정보
   * @return 수정한 다이어트 댓글 정보
   */
  @PatchMapping("/comment")
  public ResponseEntity<HttpApiResponse<DietCommentDto>> updateComment(
          @RequestBody @Valid DietCommentUpdateRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            dietChallengeService.updateDietComment(form, userDetails.getMember()),
            "다이어트 댓글 수정을 성공했습니다.",
            HttpStatus.OK));
  }


  /**
   * 다이어트 댓글 삭제 컨트롤러 메서드
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 다이어트 댓글 정보
   */
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiResponse<DietCommentDto>> deleteComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            dietChallengeService.deleteDietComment(commentId, userDetails.getMember()),
            "다이어트 댓글 삭제를 성공했습니다.",
            HttpStatus.OK));
  }

}
