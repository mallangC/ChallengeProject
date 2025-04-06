package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.CoteChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.CoteCommentDto;
import com.zerobase.challengeproject.comment.domain.form.CoteChallengeForm;
import com.zerobase.challengeproject.comment.domain.form.CoteChallengeUpdateForm;
import com.zerobase.challengeproject.comment.domain.form.CoteCommentForm;
import com.zerobase.challengeproject.comment.domain.form.CoteCommentUpdateForm;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import com.zerobase.challengeproject.comment.repository.CoteChallengeRepository;
import com.zerobase.challengeproject.comment.repository.CoteCommentRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CoteChallengeService {
  private final CoteCommentRepository coteCommentRepository;
  private final CoteChallengeRepository coteChallengeRepository;
  private final ChallengeRepository challengeRepository;
  private final MemberRepository memberRepository;

  //DB호출 횟수에서 제일 처음 회원호출은 언제나 호출되기 때문에 제외

  /**
   * 날짜를 기준으로 코테 문제를 추가하는 서비스 메서드
   * 폼에 적은 날짜에 이미 CoteChallenge를 추가 했거나, Challenge를 찾을 수 없거나,
   * 추가 하려는 회원이 챌린지를 만든 회원이 아닐 때 예외발생
   * (DB호출 2회) 호출 1, 저장 1
   *
   * @param form        챌린지 아이디, 제목, 문제 링크, 날짜
   * @param userDetails 회원 정보
   * @return 추가한 코테 챌린지 정보
   */
  public BaseResponseDto<CoteChallengeDto> addCoteChallenge(
          CoteChallengeForm form,
          UserDetailsImpl userDetails) {
    Challenge challenge = challengeRepository.searchChallengeWithCoteChallengeById(form.getChallengeId());
    if (challenge.getCategoryType() != CategoryType.COTE) {
      throw new CustomException(ErrorCode.NOT_COTE_CHALLENGE);
    }
    if (!Objects.equals(challenge.getMember().getLoginId(), userDetails.getUsername())) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_CHALLENGE);
    }

    boolean isExist = challenge.getCoteChallenges().stream()
            .anyMatch(c -> c.getStartAt().isEqual(form.getStartAt()));
    if (isExist) {
      throw new CustomException(ErrorCode.ALREADY_ADDED_THAT_DATE);
    }

    CoteChallenge coteChallenge = CoteChallenge.from(form, challenge);
    coteChallengeRepository.save(coteChallenge);

    return new BaseResponseDto<>(
            CoteChallengeDto.from(coteChallenge),
            "코테 챌린지 생성을 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 코테 챌린지를 단건 조회하는 서비스 메서드
   * 코테 챌린지 아이디로 찾을 수 없는 경우 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param coteChallengeId 코테 챌린지 아이디
   * @return 댓글을 제외한 코테 챌린지의 정보
   */
  public BaseResponseDto<CoteChallengeDto> getCoteChallenge(Long coteChallengeId) {

    CoteChallenge coteChallenge = coteChallengeRepository.findById(coteChallengeId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COTE_CHALLENGE));

    return new BaseResponseDto<>(
            CoteChallengeDto.fromWithoutComments(coteChallenge),
            "코테 챌린지 단건 조회를 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 코테 챌린지를 전체 조회하는 서비스 메서드
   * (DB호출 1회) 호출 1
   *
   * @param page        페이지
   * @param challengeId 챌린지 아이디
   * @return 댓글을 제외한 모든 코테 챌린지의 정보
   */
  public BaseResponseDto<PageDto<CoteChallengeDto>> getAllCoteChallenge(int page,
                                                                        Long challengeId) {
    Page<CoteChallengeDto> coteChallenge =
            coteChallengeRepository.searchAllCoteChallengeByChallengeId(page - 1, challengeId);
    return new BaseResponseDto<>(
            PageDto.from(coteChallenge),
            "코테 챌린지 전체 조회를 성공했습니다.(" + page + "페이지)",
            HttpStatus.OK);
  }


  /**
   * 코테 챌린지를 수정하기 위한 서비스 메서드
   * 내가 만든 챌린지가 아닐 때, 코테 챌린지를 찾을 수 없을 때 예외 발생
   * (DB호출 2회) - 호출 1, 업데이트 1
   *
   * @param form        수정할 코테 챌린지 아이디, 수정할 코테 문제, 수정할 코테 링크
   * @param userDetails 자신이 만든 챌린지 인지 확인을 위한 회원 정보
   * @return 댓글을 제외한 수정된 코테 챌린지의 정보
   */
  @Transactional
  public BaseResponseDto<CoteChallengeDto> updateCoteChallenge(
          CoteChallengeUpdateForm form,
          UserDetailsImpl userDetails) {
    CoteChallenge coteChallenge = searchCoteChallengeByIdAndOwnerCheck(
            form.getCoteChallengeId(), userDetails.getUsername());

    coteChallenge.update(form);
    return new BaseResponseDto<>(
            CoteChallengeDto.fromWithoutComments(coteChallenge),
            "코테 챌린지 수정을 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 코테 챌린지(문제) 삭제를 위한 서비스 메서드
   * 내가 만든 챌린지가 아닐 때, 코테 챌린지를 찾을 수 없을 때,
   * 댓글이 있을 때 예외 발생
   * (DB호출 2회) - 호출 1, 삭제 1
   *
   * @param coteChallengeId 코테 챌린지 아이디
   * @param userDetails     자신이 만든 챌린지 인지 확인을 위한 회원 정보
   * @return 삭제된 코테 챌린지의 정보
   */
  @Transactional
  public BaseResponseDto<CoteChallengeDto> deleteCoteChallenge(
          Long coteChallengeId,
          UserDetailsImpl userDetails) {
    CoteChallenge coteChallenge = searchCoteChallengeByIdAndOwnerCheck(
            coteChallengeId, userDetails.getUsername());

    if (!coteChallenge.getComments().isEmpty()) {
      throw new CustomException(ErrorCode.CANNOT_DELETE_HAVE_COMMENT);
    }

    coteChallengeRepository.delete(coteChallenge);
    return new BaseResponseDto<>(
            CoteChallengeDto.from(coteChallenge),
            "코테 챌린지 삭제를 성공했습니다.",
            HttpStatus.OK);
  }


  /**
   * 코테 챌린지 인증 댓글 작성을 위한 서비스 메서드
   * 오늘 날짜에 이미 댓글을 썼거나, 챌린지 아이디로 챌린지를 찾을 수 없거나(아이디를 잘못썼거나, 있어야할 CoteChallenge 가 없거나),
   * 챌린지에 참여하지 않은 사람이 댓글을 쓰려고 할 때 예외발생
   * (DB호출 3번) 호출 2, 저장 1
   *
   * @param form        챌린지 아이디, 인증하기 위한 이미지주소, 설명
   * @param userDetails username 사용
   * @return 인증 댓글 정보
   */
  public BaseResponseDto<CoteCommentDto> addComment(CoteCommentForm form, UserDetailsImpl userDetails) {

    Member member = memberRepository.searchByLoginId(userDetails.getUsername());

    CoteChallenge coteChallenge = coteChallengeRepository.searchCoteChallengeByStartAt(
            form.getChallengeId(), member.getLoginId(), parseToday());

    boolean isEnter = member.getMemberChallenges().stream()
            .anyMatch(challenge ->
                    challenge.getChallenge()
                            .getId()
                            .equals(form.getChallengeId()));
    if (!isEnter) {
      throw new CustomException(ErrorCode.NOT_ENTERED_CHALLENGE);
    }

    CoteComment coteComment = CoteComment.from(form, member, coteChallenge);
    coteCommentRepository.save(coteComment);
    return new BaseResponseDto<>(
            CoteCommentDto.from(coteComment),
            "인증 댓글 추가를 성공했습니다.",
            HttpStatus.OK);
  }

  /**
   * 코테 챌린지 인증 댓글을 조회하기 위한 서비스 메서드
   * 인증 댓글 아이디가 맞지 않으면 예외 발생
   * (DB호출 1회) 호출 1
   *
   * @param commentId 댓글 아이디
   * @return 인증 댓글 정보
   */
  public BaseResponseDto<CoteCommentDto> getComment(Long commentId) {
    CoteComment coteComment = searchCoteCommentById(commentId);
    return new BaseResponseDto<>(
            CoteCommentDto.from(coteComment),
            "인증 댓글 조회를 성공했습니다.",
            HttpStatus.OK);
  }


  /**
   * 코테 챌린지 인증 댓글을 수정하기 위한 서비스 메서드
   * 인증 댓글 아이디가 맞지 않거나, 회원 자신이 작성한 댓글이 아니면 예외 발생
   * (DB호출 2회) 호출 1, 업데이트 1
   *
   * @param form        댓글 아이디, 수정할 이미지 주소, 수정할 문제풀이
   * @param userDetails 회원 정보
   * @return 수정된 인증 댓글 정보
   */
  @Transactional
  public BaseResponseDto<CoteCommentDto> updateComment(CoteCommentUpdateForm form,
                                                       UserDetailsImpl userDetails) {
    CoteComment coteComment = searchCoteCommentById(form.getCommentId(), userDetails.getUsername());
    coteComment.update(form);
    return new BaseResponseDto<>(
            CoteCommentDto.from(coteComment),
            "인증 댓글 수정을 성공했습니다.",
            HttpStatus.OK);
  }


  /**
   * 코테 챌린지 인증 댓글을 삭제하기 위한 서비스 메서드
   * 인증 댓글 아이디가 맞지 않거나, 회원 자신이 작성한 댓글이 아니면 예외 발생
   * (DB호출 2회) 호출 1, 삭제 1
   *
   * @param commentId   댓글 아이디
   * @param userDetails 회원 정보
   * @return 삭제된 인증 댓글 정보
   */
  @Transactional
  public BaseResponseDto<CoteCommentDto> deleteComment(Long commentId,
                                                       UserDetailsImpl userDetails) {
    CoteComment coteComment = searchCoteCommentById(commentId, userDetails.getUsername());
    coteCommentRepository.delete(coteComment);
    return new BaseResponseDto<>(
            CoteCommentDto.from(coteComment),
            "인증 댓글 삭제를 성공했습니다.",
            HttpStatus.OK);
  }

  private CoteComment searchCoteCommentById(Long commentId) {
    return coteCommentRepository.findById(commentId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COTE_COMMENT));
  }

  private CoteComment searchCoteCommentById(Long commentId, String username) {
    CoteComment coteComment = coteCommentRepository.searchCoteCommentById(commentId);
    if (!coteComment.getMember().getLoginId().equals(username)) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_COMMENT);
    }
    return coteComment;
  }

  private CoteChallenge searchCoteChallengeByIdAndOwnerCheck(Long coteChallengeId, String username) {
    CoteChallenge coteChallenge = coteChallengeRepository.searchCoteChallengeById(coteChallengeId);
    boolean isOwner = coteChallenge.getChallenge().getMember().getLoginId().equals(username);
    if (!isOwner) {
      throw new CustomException(ErrorCode.NOT_OWNER_OF_CHALLENGE);
    }
    return coteChallenge;
  }

  private LocalDateTime parseToday() {
    LocalDateTime now = LocalDateTime.now();
    return LocalDateTime.parse(String.format("%04d-%02d-%02dT00:00:00", now.getYear(), now.getMonthValue(), now.getDayOfMonth()));
  }

}
