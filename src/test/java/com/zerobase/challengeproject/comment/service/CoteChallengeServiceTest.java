package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.entity.MemberChallenge;
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
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.CategoryType;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zerobase.challengeproject.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CoteChallengeServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private CoteChallengeRepository coteChallengeRepository;

  @Mock
  private CoteCommentRepository coteCommentRepository;

  @Mock
  private ChallengeRepository challengeRepository;

  @InjectMocks
  private CoteChallengeService coteChallengeService;

  LocalDateTime startAt = LocalDateTime.parse("2025-04-04T00:00:00");

  Member memberBase = Member.builder()
          .id(1L)
          .memberId("test")
          .memberType(MemberType.USER)
          .memberName("testName")
          .nickname("testNickname")
          .email("test@test.com")
          .account(10000L)
          .memberChallenges(new ArrayList<>())
          .coteComments(new ArrayList<>())
          .accountDetails(new ArrayList<>())
          .build();


  Challenge challengeBase = Challenge.builder()
          .id(1L)
          .title("challengeTitle")
          .img("challengeImg")
          .categoryType(CategoryType.COTE)
          .description("challengeDescription")
          .maxParticipant(10L)
          .currentParticipant(1L)
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard("challengeStandard")
          .member(memberBase)
          .coteChallenges(new ArrayList<>())
          .build();


  CoteChallenge coteChallengeBase = CoteChallenge.builder()
          .id(1L)
          .challenge(challengeBase)
          .title("coteChallengeTitle")
          .question("문제 링크")
          .startAt(startAt)
          .build();

  Challenge badChallenge = Challenge.builder()
          .id(1L)
          .title("challengeTitle")
          .img("challengeImg")
          .categoryType(CategoryType.COTE)
          .description("challengeDescription")
          .maxParticipant(10L)
          .currentParticipant(1L)
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard("challengeStandard")
          .member(Member.builder()
                  .memberId("틀리다")
                  .build())
          .coteChallenges(new ArrayList<>())
          .build();

  CoteComment commentBase = CoteComment.builder()
          .id(1L)
          .member(memberBase)
          .coteChallenge(coteChallengeBase)
          .image("인증 댓글 이미지 링크")
          .content("정말 어려웠다")
          .build();

  UserDetailsImpl userDetailsBase = new UserDetailsImpl(memberBase);

  @Test
  @DisplayName("코테 챌린지 추가 성공")
  void accCoteChallenge() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(challengeBase);

    CoteChallengeForm form = CoteChallengeForm.builder()
            .challengeId(1L)
            .title("문제 제목")
            .question("코테 문제 링크")
            .startAt(startAt)
            .build();
    //when
    BaseResponseDto<CoteChallengeDto> result =
            coteChallengeService.addCoteChallenge(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("코테 챌린지 생성을 성공했습니다.", result.getMessage());
    assertEquals(startAt, result.getData().getStartAt());
    assertEquals("문제 제목", result.getData().getTitle());
    assertEquals("코테 문제 링크", result.getData().getQuestion());
    verify(coteChallengeRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("코테 챌린지 추가 실패(내가 만든 챌린지가 아님)")
  void accCoteChallengeFailure1() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(badChallenge);

    CoteChallengeForm form = CoteChallengeForm.builder()
            .challengeId(1L)
            .title("문제 제목")
            .question("코테 문제 링크")
            .startAt(startAt)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addCoteChallenge(form, userDetailsBase));

    //then
    assertEquals(NOT_OWNER_OF_CHALLENGE, exception.getErrorCode());
    verify(coteChallengeRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("코테 챌린지 추가 실패(이미 코테챌린지가 추가됨)")
  void accCoteChallengeFailure2() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(Challenge.builder()
                    .id(1L)
                    .title("challengeTitle")
                    .img("challengeImg")
                    .categoryType(CategoryType.COTE)
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .description("challengeDescription")
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard("challengeStandard")
                    .member(memberBase)
                    .coteChallenges(List.of(coteChallengeBase))
                    .build());

    CoteChallengeForm form = CoteChallengeForm.builder()
            .challengeId(1L)
            .title("문제 제목")
            .question("코테 문제 링크")
            .startAt(startAt)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addCoteChallenge(form, userDetailsBase));

    //then
    assertEquals(ALREADY_ADDED_THAT_DATE, exception.getErrorCode());
    verify(coteChallengeRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("코테 챌린지 조회 성공")
  void getCoteChallenge() {
    //given
    given(coteChallengeRepository.findById(anyLong()))
            .willReturn(Optional.of(coteChallengeBase));
    //when
    BaseResponseDto<CoteChallengeDto> result =
            coteChallengeService.getCoteChallenge(1L);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("코테 챌린지 단건 조회를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals(1L, result.getData().getId());
    assertEquals(coteChallengeBase.getTitle(), result.getData().getTitle());
    assertEquals(coteChallengeBase.getQuestion(), result.getData().getQuestion());
  }

  @Test
  @DisplayName("코테 챌린지 조회 실패(잘못된 챌린지 아이디)")
  void getCoteChallengeFailure() {
    //given
    given(coteChallengeRepository.findById(anyLong()))
            .willReturn(Optional.empty());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.getCoteChallenge(1L));
    //then
    assertEquals(NOT_FOUND_COTE_CHALLENGE, exception.getErrorCode());
  }


  @Test
  @DisplayName("코테 챌린지 수정 성공")
  void updateCoteChallenge() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(coteChallengeBase);

    CoteChallengeUpdateForm form = CoteChallengeUpdateForm.builder()
            .coteChallengeId(1L)
            .title("업데이트 제목")
            .question("업데이트 문제")
            .build();
    //when
    BaseResponseDto<CoteChallengeDto> result =
            coteChallengeService.updateCoteChallenge(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("코테 챌린지 수정을 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("업데이트 제목", result.getData().getTitle());
    assertEquals("업데이트 문제", result.getData().getQuestion());
  }

  @Test
  @DisplayName("코테 챌린지 수정 실패(내가 만든 챌린지가 아님)")
  void updateCoteChallengeFailure() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(CoteChallenge.builder()
                    .id(1L)
                    .title("제목")
                    .question("문제")
                    .startAt(startAt)
                    .challenge(badChallenge)
                    .build());

    CoteChallengeUpdateForm form = CoteChallengeUpdateForm.builder()
            .coteChallengeId(1L)
            .title("업데이트 제목")
            .question("업데이트 문제")
            .build();
    //when

    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.updateCoteChallenge(form, userDetailsBase));
    //then
    assertEquals(NOT_OWNER_OF_CHALLENGE, exception.getErrorCode());
  }


  @Test
  @DisplayName("코테 챌린지 삭제 성공")
  void deleteCoteChallenge() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(CoteChallenge.builder()
                    .id(1L)
                    .challenge(challengeBase)
                    .title("삭제 제목")
                    .question("삭제 문제 링크")
                    .startAt(startAt)
                    .comments(new ArrayList<>())
                    .build());
    //when
    BaseResponseDto<CoteChallengeDto> result =
            coteChallengeService.deleteCoteChallenge(1L, userDetailsBase);

    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("코테 챌린지 삭제를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getId());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("삭제 제목", result.getData().getTitle());
    assertEquals("삭제 문제 링크", result.getData().getQuestion());
    assertEquals(startAt, result.getData().getStartAt());
  }


  @Test
  @DisplayName("코테 챌린지 삭제 성공(내가 만든 챌린지가 아님)")
  void deleteCoteChallengeFailure1() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(CoteChallenge.builder()
                    .id(1L)
                    .challenge(badChallenge)
                    .title("삭제 제목")
                    .question("삭제 문제 링크")
                    .startAt(startAt)
                    .comments(new ArrayList<>())
                    .build());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.deleteCoteChallenge(1L, userDetailsBase));

    //then
    assertEquals(NOT_OWNER_OF_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("코테 챌린지 삭제 성공(내가 만든 챌린지가 아님)")
  void deleteCoteChallengeFailure2() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(CoteChallenge.builder()
                    .id(1L)
                    .challenge(challengeBase)
                    .title("삭제 제목")
                    .question("삭제 문제 링크")
                    .startAt(startAt)
                    .comments(List.of(CoteComment.builder()
                            .id(1L)
                            .build()))
                    .build());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.deleteCoteChallenge(1L, userDetailsBase));
    //then
    assertEquals(CANNOT_DELETE_HAVE_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("인증 댓글 추가 성공")
  void addComment() {
    //given
    given(memberRepository.searchByLoginId(anyString()))
            .willReturn(Member.builder()
                    .id(1L)
                    .memberId("인증댓글추가멤버아이디")
                    .memberChallenges(List.of(MemberChallenge.builder()
                            .id(1L)
                            .challenge(challengeBase)
                            .build()))
                    .build());
    given(coteChallengeRepository.searchCoteChallengeByStartAt(anyLong(), anyString(), any()))
            .willReturn(coteChallengeBase);

    CoteCommentForm form = CoteCommentForm.builder()
            .challengeId(1L)
            .image("이미지 링크")
            .content("어렵다 어려워")
            .build();

    //when
    BaseResponseDto<CoteCommentDto> result =
            coteChallengeService.addComment(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("인증 댓글 추가를 성공했습니다.", result.getMessage());
    assertEquals("이미지 링크", result.getData().getImage());
    assertEquals("어렵다 어려워", result.getData().getContent());
    assertEquals("인증댓글추가멤버아이디", result.getData().getUserId());
    assertEquals(1L, result.getData().getCoteChallengeId());
    verify(coteCommentRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("인증 댓글 추가 실패(챌린지에 참여하지 않은 회원)")
  void addCommentFailure1() {
    //given
    given(memberRepository.searchByLoginId(anyString()))
            .willReturn(Member.builder()
                    .id(1L)
                    .memberId("인증댓글추가멤버아이디")
                    .memberChallenges(new ArrayList<>())
                    .build());
    given(coteChallengeRepository.searchCoteChallengeByStartAt(anyLong(), anyString(), any()))
            .willReturn(coteChallengeBase);

    CoteCommentForm form = CoteCommentForm.builder()
            .challengeId(1L)
            .image("이미지 링크")
            .content("어렵다 어려워")
            .build();

    //when

    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addComment(form, userDetailsBase));
    //then
    assertEquals(NOT_ENTERED_CHALLENGE, exception.getErrorCode());
    verify(coteCommentRepository, times(0)).save(any());
  }


  @Test
  @DisplayName("인증 댓글 조회 성공")
  void getComment() {
    //given
    given(coteCommentRepository.findById(anyLong()))
            .willReturn(Optional.of(commentBase));
    //when
    BaseResponseDto<CoteCommentDto> result = coteChallengeService.getComment(1L);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("인증 댓글 조회를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getCoteChallengeId());
    assertEquals("test", result.getData().getUserId());
    assertEquals("인증 댓글 이미지 링크", result.getData().getImage());
    assertEquals("정말 어려웠다", result.getData().getContent());
  }


  @Test
  @DisplayName("인증 댓글 수정 성공")
  void updateComment() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(commentBase);
    CoteCommentUpdateForm form = CoteCommentUpdateForm.builder()
            .commentId(1L)
            .content("수정한 내용")
            .image("수정한 이미지 링크")
            .build();
    //when
    BaseResponseDto<CoteCommentDto> result =
            coteChallengeService.updateComment(form, userDetailsBase);

    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("인증 댓글 수정을 성공했습니다.", result.getMessage());
    assertEquals("test", result.getData().getUserId());
    assertEquals("수정한 이미지 링크", result.getData().getImage());
    assertEquals("수정한 내용", result.getData().getContent());
    assertEquals(1L, result.getData().getCoteChallengeId());
  }

  @Test
  @DisplayName("인증 댓글 수정 실패(내가 작성한 인증 댓글이 아님)")
  void updateCommentFailure1() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(CoteComment.builder()
                    .id(1L)
                    .member(Member.builder()
                            .id(1L)
                            .memberId("실패멤버아이디")
                            .build())
                    .build());
    CoteCommentUpdateForm form = CoteCommentUpdateForm.builder()
            .commentId(1L)
            .content("수정한 내용")
            .image("수정한 이미지 링크")
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.updateComment(form, userDetailsBase));

    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("인증 댓글 삭제 성공")
  void deleteComment() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(commentBase);
    //when
    BaseResponseDto<CoteCommentDto> result = coteChallengeService.deleteComment(1L, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("인증 댓글 삭제를 성공했습니다.", result.getMessage());
    assertEquals("test", result.getData().getUserId());
    assertEquals("인증 댓글 이미지 링크", result.getData().getImage());
    assertEquals("정말 어려웠다", result.getData().getContent());
    assertEquals(1L, result.getData().getCoteChallengeId());
  }


  @Test
  @DisplayName("인증 댓글 삭제 실패(내가 작성한 인증 댓글이 아님)")
  void deleteCommentFailure() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(CoteComment.builder()
                    .id(1L)
                    .member(Member.builder()
                            .id(1L)
                            .memberId("실패멤버아이디")
                            .build())
                    .build());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.deleteComment(1L, userDetailsBase));
    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

}