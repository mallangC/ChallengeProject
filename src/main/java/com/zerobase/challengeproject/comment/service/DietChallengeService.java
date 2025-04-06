package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.DietChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.DietCommentDto;
import com.zerobase.challengeproject.comment.domain.form.DietChallengeAddForm;
import com.zerobase.challengeproject.comment.domain.form.DietChallengeUpdateForm;
import com.zerobase.challengeproject.comment.domain.form.DietCommentAddForm;
import com.zerobase.challengeproject.comment.domain.form.DietCommentUpdateForm;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.comment.entity.DietComment;
import com.zerobase.challengeproject.comment.repository.DietChallengeRepository;
import com.zerobase.challengeproject.comment.repository.DietCommentRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DietChallengeService {
  private final ChallengeRepository challengeRepository;
  private final DietChallengeRepository dietChallengeRepository;
  private final DietCommentRepository dietCommentRepository;

  //DB호출 횟수에서 제일 처음 회원호출은 언제나 호출되기 때문에 제외

  /**
   * 다이어트 챌린지 추가 서비스 메서드
   * 챌린지 CategoryType이 다이어트가 아닐 때, 이미 다이어트 챌린지를 작성 했을 때,
   * 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 3회) 호출 1, 저장 2
   *
   * @param form        챌린지아이디, 이미지 주소, 목표 몸무게, 현재 몸무게
   * @param userDetails 회원 정보
   * @return 다이어트 챌린지 정보
   */
  public BaseResponseDto<DietChallengeDto> addDietChallenge(DietChallengeAddForm form,
                                                            UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    Challenge challenge = challengeRepository.searchChallengeWithDietChallengeById(form.getChallengeId());
    if (challenge.getCategoryType() != CategoryType.DIET) {
      throw new CustomException(ErrorCode.NOT_DIET_CHALLENGE);
    }

    boolean isExist = challenge.getDietChallenges().stream()
            .anyMatch(c -> c.getMember().getMemberId().equals(member.getMemberId()));
    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_DIET_CHALLENGE);
    }

    DietChallenge dietChallenge = DietChallenge.from(form, member, challenge);
    DietComment dietComment = DietComment.builder()
            .dietChallenge(dietChallenge)
            .member(member)
            .image(form.getImage())
            .content("참여 인증")
            .build();
    dietChallengeRepository.save(dietChallenge);
    dietCommentRepository.save(dietComment);

    return new BaseResponseDto<>(DietChallengeDto.from(dietChallenge),
            "다이어트 챌린지 추가를 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 회원 본인이 작성한 다이어트 챌린지 조회 서비스 메서드
   * 다이어트 챌린지를 찾을 수 없을 때 예외 발생(참여하지 않았을 경우)
   * (DB호출 1회) 호출 1
   *
   * @param challengeId 챌린지 아이디
   * @param userDetails 유저 정보
   * @return 다이어트 챌린지 정보
   */
  public BaseResponseDto<DietChallengeDto> getDietChallenge(Long challengeId,
                                                            UserDetailsImpl userDetails) {
    DietChallenge dietChallenge =
            dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(
                    challengeId, userDetails.getUsername());
    return new BaseResponseDto<>(DietChallengeDto.from(dietChallenge),
            "다이어트 챌린지 단건 조회를 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 다이어트 챌린지 전체를 조회 서비스 메서드
   * 다이어트 챌린지가 없는 경우 비어있는 리스트 반환
   * (DB호출 1회) 호출 1
   *
   * @param page        페이지 번호
   * @param challengeId 챌린지 아이디
   * @return 페이징이된 다이어트 챌린지 리스트
   */
  public BaseResponseDto<PageDto<DietChallengeDto>> getAllDietChallenge(int page,
                                                                        Long challengeId) {
    Page<DietChallengeDto> dietChallengeDtos =
            dietChallengeRepository.searchAllDietChallengeByChallengeId(page - 1, challengeId);
    return new BaseResponseDto<>(PageDto.from(dietChallengeDtos),
            "다이어트 챌린지 전체 조회를 성공했습니다.(" + page + "페이지)",
            HttpStatus.OK);
  }

  /**
   * 다이어트 챌린지 수정 서비스 메서드
   * (DB호출 2회) 호출 1, 업데이트 1
   *
   * @param form        챌린지 아이디, 목표 몸무게, 현재 몸무게
   * @param userDetails 회원 정보
   * @return 수정된 다이어트 챌린지 정보
   */
  @Transactional
  public BaseResponseDto<DietChallengeDto> updateDietChallenge(DietChallengeUpdateForm form,
                                                               UserDetailsImpl userDetails) {
    DietChallenge dietChallenge =
            dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(
                    form.getChallengeId(), userDetails.getUsername());
    if (dietChallenge.getChallenge().getStartDate().isBefore(LocalDateTime.now())) {
      throw new CustomException(ErrorCode.CANNOT_UPDATE_AFTER_START_CHALLENGE);
    }
    dietChallenge.update(form);
    return new BaseResponseDto<>(DietChallengeDto.from(dietChallenge),
            "다이어트 챌린지 수정을 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 다이어트 댓글 추가 서비스 메서드
   * 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 3회) 호출 1, 저장 1, 업데이트
   *
   * @param form        챌린지 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param userDetails 회원 정보
   * @return 추가된 다이어트 댓글 정보
   */
  @Transactional
  public BaseResponseDto<DietCommentDto> addDietComment(DietCommentAddForm form, UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    DietChallenge dietChallenge = dietChallengeRepository.
            searchDietChallengeByChallengeIdAndLoginId(form.getChallengeId(), member.getMemberId());
    DietComment dietComment = DietComment.from(form, dietChallenge, member);
    dietCommentRepository.save(dietComment);
    dietChallenge.updateWeight(form.getCurrentWeight());
    return new BaseResponseDto<>(DietCommentDto.from(dietComment),
            "다이어트 댓글 추가를 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 다이어트 댓글 단건 조회 서비스 메서드
   * 다이어트 댓글을 찾을 수 없을 때 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param commentId 댓글 아이디
   * @return 조회한 다이어트 댓글 정보
   */
  public BaseResponseDto<DietCommentDto> getDietComment(Long commentId) {
    DietComment dietComment = dietCommentRepository.searchDietCommentById(commentId);
    return new BaseResponseDto<>(DietCommentDto.from(dietComment),
            "다이어트 댓글 조회를 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 다이어트 댓글 수정 서비스 메서드
   * 다이어트 댓글을 찾을 수 없을 때 예외 발생
   * (DB호출 2회) 호출 1, 업데이트 1
   *
   * @param form        댓글 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param userDetails 회원 정보
   * @return 수정한 다이어트 댓글 정보
   */
  @Transactional
  public BaseResponseDto<DietCommentDto> updateDietComment(DietCommentUpdateForm form,
                                                           UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    DietComment dietComment = dietCommentRepository.searchDietCommentById(form.getCommentId());
    checkMemberOwnerOfComment(member, dietComment);
    dietComment.update(form);
    dietComment.getDietChallenge().updateWeight(form.getCurrentWeight());
    return new BaseResponseDto<>(DietCommentDto.from(dietComment),
            "다이어트 댓글 수정을 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 다이어트 댓글 삭제 서비스 메서드
   * (DB호출 2회) 호출 1, 삭제 1
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 다이어트 댓글 정보
   */
  @Transactional
  public BaseResponseDto<DietCommentDto> deleteDietComment(Long commentId,
                                                           UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    DietComment dietComment = dietCommentRepository.searchDietCommentById(commentId);
    checkMemberOwnerOfComment(member, dietComment);
    dietCommentRepository.delete(dietComment);
    return new BaseResponseDto<>(DietCommentDto.from(dietComment),
            "다이어트 댓글 삭제를 성공했습니다.",
            HttpStatus.OK);
  }

  private void checkMemberOwnerOfComment(Member member, DietComment comment) {
    if (!member.getMemberId().equals(comment.getMember().getMemberId())) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_COMMENT);
    }
  }

}