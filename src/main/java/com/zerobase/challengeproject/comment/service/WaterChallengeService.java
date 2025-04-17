package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.WaterChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterCommentDto;
import com.zerobase.challengeproject.comment.domain.request.WaterChallengeRequest;
import com.zerobase.challengeproject.comment.domain.request.WaterCommentAddRequest;
import com.zerobase.challengeproject.comment.domain.request.WaterCommentUpdateRequest;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.comment.entity.WaterComment;
import com.zerobase.challengeproject.comment.repository.WaterChallengeRepository;
import com.zerobase.challengeproject.comment.repository.WaterCommentRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.CategoryType;
import com.zerobase.challengeproject.type.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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
   * @param form   챌린지 아이디, 하루 목표 섭취량
   * @param member 회원 객체
   * @return 추가한 물마시기 챌린지 정보
   */
  public WaterChallengeDto addWaterChallenge(WaterChallengeRequest form,
                                             Member member) {
    Challenge challenge = challengeRepository.searchChallengeWithWaterChallengeById(form.getChallengeId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

    verifyChallenge(challenge, member.getLoginId());

    WaterChallenge waterChallenge = WaterChallenge.fromForm(form, challenge, member);
    waterChallengeRepository.save(waterChallenge);

    return WaterChallengeDto.fromWithoutComment(waterChallenge);
  }

  /**
   * 스케쥴러를 사용해서 매일 00시에 그날 물마시기 챌린지가 추가 기능 구현
   */
  public void addAllWaterChallenge() {
    LocalDate now = LocalDate.now();
    LocalDateTime today = now.atStartOfDay();
    LocalDateTime yesterdayStart = now.atStartOfDay().minusDays(1);

    List<Challenge> challenges = challengeRepository.searchAllChallenge();
    if (challenges.isEmpty()) {
      throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGE);
    }
    for (Challenge challenge : challenges) {
      //오늘이 시작날이면 waterChallenges에 있는 WaterChallenge(참여할 때 작성됨)를 바탕으로 오늘 WaterChallenge 만들기
      if (challenge.getStartDate().equals(today)) {
        for (WaterChallenge waterChallenge : challenge.getWaterChallenges()) {
          //참여할 때 작성된 WaterChallenge를 기준으로 새로운 WaterChallenge를 생성 후 저장
          waterChallengeRepository.save(WaterChallenge.fromWaterChallenge(waterChallenge));
        }
      } else {
        //이미 진행 중인 챌린지는 waterChallenges에 있는 어제 날짜로 만들어진 WaterChallenge를 바탕으로 오늘 WaterChallenge 만들기
        List<WaterChallenge> waterChallenges = challenge.getWaterChallenges();
        //최근에 생성된 순으로 정렬
        waterChallenges.sort(Comparator.comparing(WaterChallenge::getCreatedAt).reversed());
        System.out.println(waterChallenges);
        //최근에 생성된 WaterChallenge가 오늘 생성되었다면 이미 이 함수가 실행됐다고 판단하고 예외 발생
        if (waterChallenges.get(0).getCreatedAt().toLocalDate().equals(today.toLocalDate())) {
          throw new CustomException(ErrorCode.ALREADY_ADDED_WATER_CHALLENGE);
        }
        for (WaterChallenge waterChallenge : challenge.getWaterChallenges()) {
          //어제 날짜의 데이터를 기준으로 새로운 WaterChallenge를 생성 후 저장
          if (waterChallenge.getCreatedAt().toLocalDate().equals(yesterdayStart.toLocalDate())) {
            waterChallengeRepository.save(WaterChallenge.fromWaterChallenge(waterChallenge));
          } else {
            //정렬 했기 때문에 어제 이후 WaterChallenge는 무시
            return;
          }
        }
      }
    }
  }

  /**
   * 오늘의 물마시기 챌린지 조회 서비스 메서드
   * 물마시기 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param challengeId 챌린지 아이디
   * @param member      회원 객체
   * @return 물마시기 챌린지 정보
   */
  public WaterChallengeDto getWaterChallenge(Long challengeId, Member member) {
    WaterChallenge waterChallenge = waterChallengeRepository
            .searchWaterChallengeByChallengeIdAndLoginId(challengeId, member.getLoginId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WATER_CHALLENGE));

    return WaterChallengeDto.fromWithoutComment(waterChallenge);
  }

  /**
   * 물마시기 챌린지 수정 서비스 메서드
   * 챌린지가 시작한 경우 예외 발생
   * (DB호출 2회) 호출 1, 수정 1
   *
   * @param form    챌린지 아이디, 목표 섭취량
   * @param loginId 로그인 아이디
   * @return 수정된 물마시기 챌린지 정보
   */
  @Transactional
  public WaterChallengeDto updateWaterChallenge(WaterChallengeRequest form, String loginId) {
    WaterChallenge waterChallenge =
            searchWaterChallenge(form.getChallengeId(), loginId);

    if (LocalDateTime.now().isAfter(waterChallenge.getChallenge().getStartDate())) {
      throw new CustomException(ErrorCode.CANNOT_UPDATE_AFTER_START_CHALLENGE);
    }
    waterChallenge.updateGoalIntake(form.getGoalIntake());
    return WaterChallengeDto.fromWithoutComment(waterChallenge);
  }

  /**
   * 물마시기 댓글 추가 서비스 메서드
   * 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 2회) 호출 1, 추가 1
   *
   * @param form   챌린지 아이디, 현재 섭취량, 이미지
   * @param member 회원 객체
   * @return 추가된 물마시기 댓글 정보
   */
  public WaterCommentDto addWaterComment(WaterCommentAddRequest form, Member member) {
    WaterChallenge waterChallenge = searchWaterChallenge(form.getChallengeId(), member.getLoginId());
    WaterComment waterComment = WaterComment.from(form, waterChallenge, member);
    waterChallenge.updateCurrentIntake(form.getDrinkingIntake());
    waterCommentRepository.save(waterComment);
    return WaterCommentDto.from(waterComment);
  }

  /**
   * 물마시기 댓글 단건 조회 서비스 메서드
   * 물마시기 댓글을 찾을 수 없을 때 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param commentId 댓글 아이디
   * @return 물마시기 댓글 정보
   */
  public WaterCommentDto getWaterComment(Long commentId) {
    WaterComment waterComment = searchWaterComment(commentId);
    return WaterCommentDto.from(waterComment);
  }

  /**
   * 물마시기 댓글 수정 서비스 메서드
   * 물마시기 댓글을 찾을 수 없을 때, 내가 작성한 댓글이 아닐 때 예외 발생
   * (DB호출 3회) 호출 1, 수정 2
   *
   * @param form    댓글 아이디, 현재 섭취량, 이미지
   * @param loginId 로그인 아이디
   * @return 수정된 물마시기 댓글 정보
   */
  @Transactional
  public WaterCommentDto updateWaterComment(WaterCommentUpdateRequest form,
                                            String loginId) {
    WaterComment waterComment = searchWaterComment(form.getCommentId(), loginId);
    waterComment.update(form);
    return WaterCommentDto.from(waterComment);
  }

  /**
   * 물마시기 댓글 삭제 서비스 메서드
   * 물마시기 댓글을 찾을 수 없을 때, 내가 작성한 댓글이 아닐 때 예외 발생
   *
   * @param commentId 댓글 아이디
   * @param loginId   로그인 아이디
   * @return 삭제된 물마시기 댓글 정보
   */
  @Transactional
  public WaterCommentDto deleteWaterComment(Long commentId, String loginId) {
    WaterComment waterComment = searchWaterComment(commentId, loginId);
    waterComment.getWaterChallenge().updateCurrentIntake(-waterComment.getDrinkingIntake());
    waterCommentRepository.delete(waterComment);
    return WaterCommentDto.from(waterComment);
  }


  /**
   * (관리자) 물마시기 챌린지 전체 확인 서비스 메서드
   * (DB호출 2회) 호출 2
   *
   * @param page        페이지 숫자
   * @param challengeId 챌린지 아이디
   * @param isPass      챌린지 성공 여부
   * @param memberType  회원 정보
   * @return 페이징된 물마시기 챌린지
   */
  public Page<WaterChallengeDto> getAllWaterChallenge(int page,
                                                      Long challengeId,
                                                      Boolean isPass,
                                                      MemberType memberType) {
    verifyAdminByMemberType(memberType);
    Page<WaterChallenge> waterChallenges = waterChallengeRepository.searchAllWaterChallengeByChallengeId(
            page - 1, challengeId, isPass);

    List<WaterChallengeDto> waterChallengeDtos = waterChallenges.stream()
            .map(WaterChallengeDto::fromWithoutComment)
            .toList();

    return new PageImpl<>(waterChallengeDtos, waterChallenges.getPageable(), waterChallenges.getTotalElements());
  }

  /**
   * 관리자 물마시기 댓글 삭제 서비스 메서드
   * 물마시기 댓글을 찾을 수 없을 때, 관리자가 아닐 때 예외 발생
   * (DB호출 3회) 호출 1, 수정 1, 삭제 1
   *
   * @param commentId  댓글 아이디
   * @param memberType 회원 권한
   * @return 삭제된 물마시기 댓글 정보
   */
  @Transactional
  public WaterCommentDto adminDeleteWaterComment(Long commentId, MemberType memberType) {
    verifyAdminByMemberType(memberType);
    WaterComment waterComment = searchWaterComment(commentId);
    waterComment.getWaterChallenge().updateCurrentIntake(-waterComment.getDrinkingIntake());
    waterCommentRepository.delete(waterComment);
    return WaterCommentDto.from(waterComment);
  }


  private WaterComment searchWaterComment(Long commentId) {
    return waterCommentRepository.searchWaterCommentById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WATER_COMMENT));
  }

  private WaterComment searchWaterComment(Long commentId, String loginId) {
    WaterComment waterComment = waterCommentRepository.searchWaterCommentById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WATER_COMMENT));
    if (!waterComment.getMember().getLoginId().equals(loginId)) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_COMMENT);
    }
    return waterComment;
  }

  private WaterChallenge searchWaterChallenge(Long challengeId, String loginId) {
    return waterChallengeRepository
            .searchWaterChallengeByChallengeIdAndLoginId(challengeId, loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_WATER_CHALLENGE));
  }

  private void verifyAdminByMemberType(MemberType memberType) {
    if (memberType != MemberType.ADMIN) {
      throw new CustomException(ErrorCode.NOT_MEMBER_TYPE_ADMIN);
    }
  }

  private void verifyChallenge(Challenge challenge, String loginId) {
    if (challenge.getCategoryType() != CategoryType.WATER) {
      throw new CustomException(ErrorCode.NOT_WATER_CHALLENGE);
    }
    boolean isEntered = challenge.getWaterChallenges().stream()
            .anyMatch(c -> c.getMember().getLoginId().equals(loginId));
    if (isEntered) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_WATER_CHALLENGE);
    }
  }
}
