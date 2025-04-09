package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterCommentDto;
import com.zerobase.challengeproject.comment.domain.form.WaterChallengeForm;
import com.zerobase.challengeproject.comment.domain.form.WaterCommentAddForm;
import com.zerobase.challengeproject.comment.domain.form.WaterCommentUpdateForm;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.comment.entity.WaterComment;
import com.zerobase.challengeproject.comment.repository.WaterChallengeRepository;
import com.zerobase.challengeproject.comment.repository.WaterCommentRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.CategoryType;
import com.zerobase.challengeproject.type.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WaterChallengeService {

  private final ChallengeRepository challengeRepository;
  private final WaterChallengeRepository waterChallengeRepository;
  private final WaterCommentRepository waterCommentRepository;

  //DB호출 횟수에서 제일 처음 회원호출은 언제나 호출되기 때문에 제외


  /**
   * 물마시기 챌린지 추가 서비스 메서드
   * 챌린지 참여할 때 작성한 목표 섭취량이 매일 목표 섭취량의 기준이됨
   * 물마시기 챌린지가 아닐 때, 이미 물마시기 챌린지를 추가한 경우 예외 발생
   * (DB호출 2회) 호출 1, 저장 1
   *
   * @param form        챌린지 아이디, 하루 목표 섭취량
   * @param userDetails 회원 정보
   * @return 추가한 물마시기 챌린지 정보
   */
  public BaseResponseDto<WaterChallengeDto> addWaterChallenge(WaterChallengeForm form,
                                                              UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    Challenge challenge = challengeRepository.searchChallengeWithWaterChallengeById(form.getChallengeId());
    if (challenge.getCategoryType() != CategoryType.WATER) {
      throw new CustomException(ErrorCode.NOT_WATER_CHALLENGE);
    }

    boolean isEntered = challenge.getWaterChallenges().stream()
            .anyMatch(c -> c.getMember().getMemberId().equals(member.getMemberId()));
    if (isEntered) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_WATER_CHALLENGE);
    }

    WaterChallenge waterChallenge = WaterChallenge.from(form, challenge, member);
    waterChallengeRepository.save(waterChallenge);

    return new BaseResponseDto<>(WaterChallengeDto.fromWithoutComment(waterChallenge)
            , "물마시기 챌린지 추가를 성공했습니다."
            , HttpStatus.OK);
  }

  //TODO batch를 사용해서 매일 00시에 그날 물마시기 챌린지가 추가 기능 구현

  /**
   * 오늘의 물마시기 챌린지 조회 서비스 메서드
   * 물마시기 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param challengeId 챌린지 아이디
   * @param userDetails 회원 정보
   * @return 물마시기 챌린지 정보
   */
  public BaseResponseDto<WaterChallengeDto> getWaterChallenge(Long challengeId,
                                                              UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    WaterChallenge waterChallenge = waterChallengeRepository
            .searchWaterChallengeByChallengeIdAndLoginId(challengeId, member.getMemberId());
    return new BaseResponseDto<>(WaterChallengeDto.fromWithoutComment(waterChallenge),
            "오늘의 물마시기 챌린지 조회를 성공했습니다."
            , HttpStatus.OK);
  }

  /**
   * 관리자 물마시기 챌린지 전체 확인 서비스 메서드
   * (DB호출 2회) 호출 2
   *
   * @param page        페이지 숫자
   * @param challengeId 챌린지 아이디
   * @param isPass      챌린지 성공 여부
   * @param userDetails 회원 정보
   * @return 페이징된 물마시기 챌린지
   */
  //물마시기 챌린지 전체 확인(관리자)(challengeId, userDetails) (DB호출 2회) 호출 2
  public BaseResponseDto<PageDto<WaterChallengeDto>> getAllWaterChallenge(int page,
                                                                          Long challengeId,
                                                                          Boolean isPass,
                                                                          UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    checkAdminByMemberType(member);
    Page<WaterChallengeDto> waterChallenges =
            waterChallengeRepository.searchAllWaterChallengeByChallengeId(
                    page - 1, challengeId, isPass);
    return new BaseResponseDto<>(PageDto.from(waterChallenges),
            "물마시기 챌린지 전체 조회를 성공했습니다."
            , HttpStatus.OK);
  }

  /**
   * 물마시기 챌린지 수정 서비스 메서드
   * 챌린지가 시작한 경우 예외 발생
   * (DB호출 2회) 호출 1, 수정 1
   *
   * @param form        챌린지 아이디, 목표 섭취량
   * @param userDetails 회원 정보
   * @return 수정된 물마시기 챌린지 정보
   */
  @Transactional
  public BaseResponseDto<WaterChallengeDto> updateWaterChallenge(WaterChallengeForm form,
                                                                 UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    WaterChallenge waterChallenge = waterChallengeRepository.
            searchWaterChallengeByChallengeIdAndLoginId(form.getChallengeId(), member.getMemberId());

    if (LocalDateTime.now().isAfter(waterChallenge.getChallenge().getStartDate())) {
      throw new CustomException(ErrorCode.CANNOT_UPDATE_AFTER_START_CHALLENGE);
    }
    waterChallenge.updateGoalMl(form.getGoalMl());
    return new BaseResponseDto<>(WaterChallengeDto.fromWithoutComment(waterChallenge),
            "물마시기 챌린지 수정을 성공했습니다."
            , HttpStatus.OK);
  }

  /**
   * 물마시기 댓글 추가 서비스 메서드
   * 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 2회) 호출 1, 추가 1
   *
   * @param form        챌린지 아이디, 현재 섭취량, 이미지
   * @param userDetails 회원 정보
   * @return 추가된 물마시기 댓글 정보
   */
  public BaseResponseDto<WaterCommentDto> addWaterComment(WaterCommentAddForm form,
                                                          UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    WaterChallenge waterChallenge = waterChallengeRepository.
            searchWaterChallengeByChallengeIdAndLoginId(form.getChallengeId(), member.getMemberId());
    WaterComment waterComment = WaterComment.from(form, waterChallenge, member);
    waterChallenge.updateCurrentMl(form.getDrinkingMl());
    waterCommentRepository.save(waterComment);
    return new BaseResponseDto<>(WaterCommentDto.from(waterComment),
            "물마시기 댓글 추가를 성공했습니다."
            , HttpStatus.OK);
  }

  /**
   * 물마시기 댓글 단건 조회 서비스 메서드
   * 물마시기 댓글을 찾을 수 없을 때 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param commentId 댓글 아이디
   * @return 물마시기 댓글 정보
   */
  public BaseResponseDto<WaterCommentDto> getWaterComment(Long commentId) {
    WaterComment waterComment = waterCommentRepository.searchWaterCommentById(commentId);
    return new BaseResponseDto<>(WaterCommentDto.from(waterComment),
            "물마시기 댓글 단건 조회를 성공했습니다."
            , HttpStatus.OK);
  }

  /**
   * 물마시기 댓글 수정 서비스 메서드
   * 물마시기 댓글을 찾을 수 없을 때, 내가 작성한 댓글이 아닐 때 예외 발생
   * (DB호출 3회) 호출 1, 수정 2
   *
   * @param form        댓글 아이디, 현재 섭취량, 이미지
   * @param userDetails 회원 정보
   * @return 수정된 물마시기 댓글 정보
   */
  @Transactional
  public BaseResponseDto<WaterCommentDto> updateWaterComment(WaterCommentUpdateForm form,
                                                             UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    WaterComment waterComment = waterCommentRepository.searchWaterCommentById(form.getCommentId());
    if (!waterComment.getMember().getMemberId().equals(member.getMemberId())) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_COMMENT);
    }
    waterComment.update(form);
    return new BaseResponseDto<>(WaterCommentDto.from(waterComment),
            "물마시기 댓글 수정을 성공했습니다."
            , HttpStatus.OK);
  }

  /**
   * 관리자 물마시기 댓글 삭제 서비스 메서드
   * 물마시기 댓글을 찾을 수 없을 때, 관리자가 아닐 때 예외 발생
   * (DB호출 3회) 호출 1, 수정 1, 삭제 1
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 물마시기 댓글 정보
   */
  @Transactional
  public BaseResponseDto<WaterCommentDto> adminDeleteWaterComment(Long commentId,
                                                                  UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    checkAdminByMemberType(member);
    WaterComment waterComment = waterCommentRepository.searchWaterCommentById(commentId);
    waterComment.getWaterChallenge().updateCurrentMl(-waterComment.getDrinkingMl());
    waterCommentRepository.delete(waterComment);
    return new BaseResponseDto<>(WaterCommentDto.from(waterComment),
            "물마시기 댓글 삭제를 성공했습니다."
            , HttpStatus.OK);
  }

  private void checkAdminByMemberType(Member member) {
    if (member.getMemberType() != MemberType.ADMIN) {
      throw new CustomException(ErrorCode.NOT_MEMBER_TYPE_ADMIN);
    }
  }


}
