package com.zerobase.challengeproject.comment.controller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.PaginatedResponse;
import com.zerobase.challengeproject.comment.domain.dto.CoteChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.CoteCommentDto;
import com.zerobase.challengeproject.comment.domain.request.CoteChallengeRequest;
import com.zerobase.challengeproject.comment.domain.request.CoteChallengeUpdateRequest;
import com.zerobase.challengeproject.comment.domain.request.CoteCommentRequest;
import com.zerobase.challengeproject.comment.domain.request.CoteCommentUpdateRequest;
import com.zerobase.challengeproject.comment.service.CoteChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/challenge/cote")
public class CoteChallengeController {
  private final CoteChallengeService coteChallengeService;


  /**
   * 날짜를 기준으로 코테 문제를 추가하는 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 제목, 문제 링크, 날짜
   * @param userDetails 회원 정보
   * @return 추가한 코테 챌린지 정보
   */
  @PostMapping
  public ResponseEntity<HttpApiResponse<CoteChallengeDto>> addCoteChallenge(
          @RequestBody @Valid CoteChallengeRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.addCoteChallenge(form, userDetails.getUsername()),
            "코테 챌린지 생성 성공",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지를 단건 조회하는 컨트롤러 메서드
   *
   * @param coteChallengeId 코테 챌린지 아이디
   * @return 댓글을 제외한 코테 챌린지의 정보
   */
  @GetMapping("/{coteChallengeId}")
  public ResponseEntity<HttpApiResponse<CoteChallengeDto>> getCoteChallenge(
          @PathVariable Long coteChallengeId) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.getCoteChallenge(coteChallengeId),
            "코테 챌린지 단건 조회 성공",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지를 전체 조회하는 컨트롤러 메서드
   *
   * @param page        페이지
   * @param challengeId 챌린지 아이디
   * @return 댓글을 제외한 모든 코테 챌린지의 정보
   */
  @GetMapping
  public ResponseEntity<PaginatedResponse<CoteChallengeDto>> getCoteChallenge(
          @RequestParam @Min(1) int page,
          @RequestParam("id") Long challengeId) {
    return ResponseEntity.ok(PaginatedResponse.from(
            coteChallengeService.getAllCoteChallenge(page, challengeId),
            "코테 챌린지 전체 조회 성공(" + page + "페이지)",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지를 수정하기 위한 컨트롤러 메서드
   *
   * @param form        수정할 코테 챌린지 아이디, 수정할 코테 문제, 수정할 코테 링크
   * @param userDetails 자신이 만든 챌린지 인지 확인을 위한 회원 정보
   * @return 댓글을 제외한 수정된 코테 챌린지의 정보
   */
  @PatchMapping
  public ResponseEntity<HttpApiResponse<CoteChallengeDto>> updateCoteChallenge(
          @RequestBody @Valid CoteChallengeUpdateRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.updateCoteChallenge(form, userDetails.getUsername()),
            "코테 챌린지 수정 성공",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지(문제) 삭제를 위한 컨트롤러 메서드
   *
   * @param coteChallengeId 코테 챌린지 아이디
   * @param userDetails     자신이 만든 챌린지 인지 확인을 위한 회원 정보
   * @return 삭제된 코테 챌린지의 정보
   */
  @DeleteMapping("/{coteChallengeId}")
  public ResponseEntity<HttpApiResponse<CoteChallengeDto>> deleteCoteChallenge(
          @PathVariable Long coteChallengeId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.deleteCoteChallenge(coteChallengeId, userDetails.getUsername()),
            "코테 챌린지 삭제 성공",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지 인증 댓글 작성을 위한 컨트롤러 메서드
   *
   * @param form        챌린지 아이디, 인증하기 위한 이미지주소, 설명
   * @param userDetails username 사용
   * @return 인증 댓글 정보
   */
  @PostMapping("/comment")
  public ResponseEntity<HttpApiResponse<CoteCommentDto>> addComment(
          @RequestBody @Valid CoteCommentRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.addComment(form, userDetails.getMember()),
            "코테 댓글 추가 성공",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지 인증 댓글을 조회하기 위한 컨트롤러 메서드
   *
   * @param commentId 댓글 아이디
   * @return 인증 댓글 정보
   */
  @GetMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiResponse<CoteCommentDto>> getComment(
          @PathVariable Long commentId) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.getComment(commentId),
            "코테 댓글 조회 성공",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지 인증 댓글을 수정하기 위한 컨트롤러 메서드
   *
   * @param form        댓글 아이디, 수정할 이미지 주소, 수정할 문제풀이
   * @param userDetails 회원 정보
   * @return 수정된 인증 댓글 정보
   */
  @PatchMapping("/comment")
  public ResponseEntity<HttpApiResponse<CoteCommentDto>> updateComment(
          @RequestBody @Valid CoteCommentUpdateRequest form,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.updateComment(form, userDetails.getUsername()),
            "코테 댓글 수정 성공",
            HttpStatus.OK));
  }

  /**
   * 코테 챌린지 인증 댓글을 삭제하기 위한 컨트롤러 메서드
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 인증 댓글 정보
   */
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<HttpApiResponse<CoteCommentDto>> deleteComment(
          @PathVariable Long commentId,
          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return ResponseEntity.ok(new HttpApiResponse<>(
            coteChallengeService.deleteComment(commentId, userDetails.getUsername()),
            "코테 댓글 삭제 성공",
            HttpStatus.OK));
  }

}
