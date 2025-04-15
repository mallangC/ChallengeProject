package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.DietChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.DietCommentDto;
import com.zerobase.challengeproject.comment.domain.request.DietChallengeAddRequest;
import com.zerobase.challengeproject.comment.domain.request.DietChallengeUpdateRequest;
import com.zerobase.challengeproject.comment.domain.request.DietCommentAddRequest;
import com.zerobase.challengeproject.comment.domain.request.DietCommentUpdateRequest;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.comment.entity.DietComment;
import com.zerobase.challengeproject.comment.repository.DietChallengeRepository;
import com.zerobase.challengeproject.comment.repository.DietCommentRepository;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietChallengeService {
  private final ChallengeRepository challengeRepository;
  private final DietChallengeRepository dietChallengeRepository;
  private final DietCommentRepository dietCommentRepository;

  //DB호출 횟수에서 제일 처음 회원호출은 언제나 호출되기 때문에 제외

  /**
   * 다이어트 챌린지 추가 서비스 메서드(참여할 때 작성)
   * 챌린지 CategoryType이 다이어트가 아닐 때, 이미 다이어트 챌린지를 작성 했을 때,
   * 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 3회) 호출 1, 저장 2
   *
   * @param form   챌린지아이디, 이미지 주소, 목표 몸무게, 현재 몸무게
   * @param member 회원 객체
   * @return 다이어트 챌린지 정보
   */
  public DietChallengeDto addDietChallenge(DietChallengeAddRequest form,
                                           Member member) {
    Challenge challenge = challengeRepository.searchChallengeWithDietChallengeById(form.getChallengeId())
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

    verifyChallenge(challenge, member.getLoginId());

    DietChallenge dietChallenge = DietChallenge.from(form, member, challenge);
    DietComment dietComment = DietComment.builder()
            .dietChallenge(dietChallenge)
            .member(member)
            .imageUrl(form.getImageUrl())
            .content("참여 인증")
            .build();
    dietChallengeRepository.save(dietChallenge);
    dietCommentRepository.save(dietComment);

    return DietChallengeDto.from(dietChallenge);
  }

  /**
   * 회원 본인이 작성한 다이어트 챌린지 조회 서비스 메서드
   * 다이어트 챌린지를 찾을 수 없을 때 예외 발생(참여하지 않았을 경우)
   * (DB호출 1회) 호출 1
   *
   * @param challengeId 챌린지 아이디
   * @param loginId     로그인 아이디
   * @return 다이어트 챌린지 정보
   */
  public DietChallengeDto getDietChallenge(Long challengeId, String loginId) {
    DietChallenge dietChallenge = searchDietChallenge(challengeId, loginId);
    return DietChallengeDto.from(dietChallenge);
  }


  /**
   * 다이어트 챌린지 수정 서비스 메서드
   * (DB호출 2회) 호출 1, 업데이트 1
   *
   * @param form    챌린지 아이디, 목표 몸무게, 현재 몸무게
   * @param loginId 로그인 아이디
   * @return 수정된 다이어트 챌린지 정보
   */
  @Transactional
  public DietChallengeDto updateDietChallenge(DietChallengeUpdateRequest form,
                                              String loginId) {
    DietChallenge dietChallenge = searchDietChallenge(form.getChallengeId(), loginId);

    if (dietChallenge.getChallenge().getStartDate().isBefore(LocalDateTime.now())) {
      throw new CustomException(ErrorCode.CANNOT_UPDATE_AFTER_START_CHALLENGE);
    }
    dietChallenge.update(form);
    return DietChallengeDto.from(dietChallenge);
  }

  /**
   * 다이어트 댓글 추가 서비스 메서드
   * 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 3회) 호출 1, 저장 1, 업데이트
   *
   * @param form   챌린지 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param member 회원 객체
   * @return 추가된 다이어트 댓글 정보
   */
  @Transactional
  public DietCommentDto addDietComment(DietCommentAddRequest form,
                                       Member member) {
    DietChallenge dietChallenge = searchDietChallenge(form.getChallengeId(), member.getLoginId());
    DietComment dietComment = DietComment.from(form, dietChallenge, member);
    dietCommentRepository.save(dietComment);
    dietChallenge.updateWeight(form.getCurrentWeight());
    return DietCommentDto.from(dietComment);
  }

  /**
   * 다이어트 댓글 단건 조회 서비스 메서드
   * 다이어트 댓글을 찾을 수 없을 때 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param commentId 댓글 아이디
   * @return 조회한 다이어트 댓글 정보
   */
  public DietCommentDto getDietComment(Long commentId) {
    DietComment dietComment = seartchDietComment(commentId);
    return DietCommentDto.from(dietComment);
  }

  /**
   * 다이어트 댓글 수정 서비스 메서드
   * 다이어트 댓글을 찾을 수 없을 때 예외 발생
   * (DB호출 2회) 호출 1, 업데이트 1
   *
   * @param form   댓글 아이디, 이미지 주소, 현재 몸무게, 내용
   * @param member 회원 객체
   * @return 수정한 다이어트 댓글 정보
   */
  @Transactional
  public DietCommentDto updateDietComment(DietCommentUpdateRequest form, Member member) {
    DietComment dietComment = seartchDietComment(form.getCommentId());
    verifyMemberOwnerOfComment(member, dietComment);
    dietComment.update(form);
    dietComment.getDietChallenge().updateWeight(form.getCurrentWeight());
    return DietCommentDto.from(dietComment);
  }

  /**
   * 다이어트 댓글 삭제 서비스 메서드
   * (DB호출 2회) 호출 1, 삭제 1
   *
   * @param commentId 댓글 아이디
   * @param member    회원 객체
   * @return 삭제된 다이어트 댓글 정보
   */
  @Transactional
  public DietCommentDto deleteDietComment(Long commentId, Member member) {
    DietComment dietComment = seartchDietComment(commentId);
    verifyMemberOwnerOfComment(member, dietComment);
    dietCommentRepository.delete(dietComment);
    return DietCommentDto.from(dietComment);
  }

  /**
   * 관리자 다이어트 챌린지 전체 조회 서비스 메서드
   * 다이어트 챌린지가 없는 경우 비어있는 리스트 반환
   * (DB호출 1회) 호출 1
   *
   * @param page        페이지 번호
   * @param challengeId 챌린지 아이디
   * @param isPass      챌린지 성공 여부
   * @param memberType  회원 권한
   * @return 페이징이된 다이어트 챌린지 리스트
   */
  public Page<DietChallengeDto> getAllDietChallenge(int page,
                                                    Long challengeId,
                                                    Boolean isPass,
                                                    MemberType memberType) {
    verifyMemberType(memberType);
    Page<DietChallenge> dietChallenges = dietChallengeRepository
            .searchAllDietChallengeByChallengeId(page - 1, challengeId, isPass);

    List<DietChallengeDto> dietChallengeDtos = dietChallenges.stream()
            .map(DietChallengeDto::fromWithoutComments)
            .toList();

    return new PageImpl<>(dietChallengeDtos, dietChallenges.getPageable(),
            dietChallenges.getTotalElements());
  }

  /**
   * (관리자) 다이어트 댓글 삭제 서비스 메서드
   * (DB호출 2회) 호출 1, 삭제 1
   *
   * @param commentId  댓글 아이디
   * @param memberType 회원 권한
   * @return 삭제된 다이어트 댓글 정보
   */
  @Transactional
  public DietCommentDto adminDeleteDietComment(Long commentId, MemberType memberType) {
    verifyMemberType(memberType);
    DietComment dietComment = seartchDietComment(commentId);
    dietCommentRepository.delete(dietComment);
    return DietCommentDto.from(dietComment);
  }


  private DietComment seartchDietComment(Long commentId) {
    return dietCommentRepository.searchDietCommentById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DIET_COMMENT));
  }

  private DietChallenge searchDietChallenge(Long challengeId, String loginId) {
    return dietChallengeRepository
            .searchDietChallengeByChallengeIdAndLoginId(challengeId, loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DIET_CHALLENGE));
  }

  private void verifyMemberType(MemberType memberType) {
    if (memberType != MemberType.ADMIN) {
      throw new CustomException(ErrorCode.NOT_MEMBER_TYPE_ADMIN);
    }
  }

  private void verifyChallenge(Challenge challenge, String loginId) {
    if (challenge.getCategoryType() != CategoryType.DIET) {
      throw new CustomException(ErrorCode.NOT_DIET_CHALLENGE);
    }
    boolean isExist = challenge.getDietChallenges().stream()
            .anyMatch(c -> c.getMember().getLoginId().equals(loginId));
    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_DIET_CHALLENGE);
    }
  }

  private void verifyMemberOwnerOfComment(Member member, DietComment comment) {
    if (!member.getLoginId().equals(comment.getMember().getLoginId())) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_COMMENT);
    }
  }

}