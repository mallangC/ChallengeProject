package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.domain.form.WaterChallengeForm;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.comment.repository.WaterChallengeRepository;
import com.zerobase.challengeproject.comment.repository.WaterCommentRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.RequiredArgsConstructor;
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

  //물마시기 챌린지 추가(form, userDetails) (DB호출 2회) 호출 1, 저장 1
  //챌린지 참여할 때 작성한 목표 섭취량이 매일 목표 섭취량의 기준이됨
  //TODO batch를 사용해서 매일 00시에 그날 물마시기 챌린지가 추가 기능 구현
  public BaseResponseDto<WaterChallengeDto> addWaterChallenge(WaterChallengeForm form, UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    Challenge challenge = challengeRepository.searchChallengeWithWaterChallengeById(form.getChallengeId());
    if (challenge.getCategoryType() != CategoryType.WATER) {
      throw new CustomException(ErrorCode.NOT_WATER_CHALLENGE);
    }

    boolean isEntered = challenge.getWaterChallenges().stream()
            .anyMatch(c -> c.getMember().getMemberId().equals(member.getMemberId()));
    if (isEntered) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_DIET_CHALLENGE);
    }

    WaterChallenge waterChallenge = WaterChallenge.from(form, challenge, member);
    waterChallengeRepository.save(waterChallenge);

    return new BaseResponseDto<>(WaterChallengeDto.fromWithoutComment(waterChallenge)
            , "물마시기 챌린지 추가를 성공했습니다."
            , HttpStatus.OK);
  }

  //오늘의 물마시기 챌린지 조회(challengeId, userDetails)(DB호출 1회) 호출 1
  public BaseResponseDto<WaterChallengeDto> getWaterChallenge(Long challengeId, UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    WaterChallenge waterChallenge = waterChallengeRepository
            .searchWaterChallengeByChallengeIdAndLoginId(challengeId, member.getMemberId());
    return new BaseResponseDto<>(WaterChallengeDto.fromWithoutComment(waterChallenge),
            "물마시기 챌린지 추가를 성공했습니다."
            , HttpStatus.OK);
  }

  //물마시기 챌린지 전체 확인(challengeId, userDetails) -> 필요한가?

  //물마시기 챌린지 수정(form, userDetails) -> 챌린지가 시작한경우 불가능 (DB호출 2회) 호출 1, 수정 1
  @Transactional
  public BaseResponseDto<WaterChallengeDto> updateWaterChallenge(WaterChallengeForm form, UserDetailsImpl userDetails) {
    Member member = userDetails.getMember();
    WaterChallenge waterChallenge = waterChallengeRepository.
            searchWaterChallengeByChallengeIdAndLoginId(form.getChallengeId(), member.getMemberId());

    if (LocalDateTime.now().isAfter(waterChallenge.getChallenge().getStartDate())) {
      throw new CustomException(ErrorCode.CANNOT_UPDATE_AFTER_START_CHALLENGE);
    }
    waterChallenge.updateGoalMl(form.getGoalMl());
    return new BaseResponseDto<>(WaterChallengeDto.fromWithoutComment(waterChallenge),
            "물마시기 챌린지 추가를 성공했습니다."
            , HttpStatus.OK);
  }

  //물마시기 코멘트 추가(form, userDetails)
  //물마시기 코멘트 확인(commentId)
  //물마시기 코멘트 수정(form, userDetails)
  //물마시기 코멘트 삭제(commentId, userDetails) 섭취량이 변하진 않음

}
