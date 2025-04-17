package com.zerobase.challengeproject.comment.service;

import com.github.javafaker.Faker;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.entity.MemberChallenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.CoteChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.CoteCommentDto;
import com.zerobase.challengeproject.comment.domain.request.CoteChallengeRequest;
import com.zerobase.challengeproject.comment.domain.request.CoteChallengeUpdateRequest;
import com.zerobase.challengeproject.comment.domain.request.CoteCommentRequest;
import com.zerobase.challengeproject.comment.domain.request.CoteCommentUpdateRequest;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import com.zerobase.challengeproject.comment.repository.CoteChallengeRepository;
import com.zerobase.challengeproject.comment.repository.CoteCommentRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.type.CategoryType;
import com.zerobase.challengeproject.type.MemberType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  private CoteChallengeRepository coteChallengeRepository;

  @Mock
  private CoteCommentRepository coteCommentRepository;

  @Mock
  private ChallengeRepository challengeRepository;

  @InjectMocks
  private CoteChallengeService coteChallengeService;

  private final Faker faker = new Faker();

  LocalDateTime startAt = LocalDateTime.now().plusDays(1);

  Member memberBase = Member.builder()
          .id(1L)
          .loginId(faker.name().username())
          .memberType(MemberType.USER)
          .memberName(faker.name().name())
          .nickname(faker.name().username())
          .email(faker.internet().emailAddress())
          .account(10000L)
          .memberChallenges(new ArrayList<>())
          .coteComments(new ArrayList<>())
          .accountDetails(new ArrayList<>())
          .build();


  Challenge challengeBase = Challenge.builder()
          .id(1L)
          .title(faker.name().username())
          .imageUrl(faker.internet().domainName())
          .categoryType(CategoryType.COTE)
          .description(faker.lorem().sentence(15))
          .maxParticipant(10L)
          .currentParticipant(1L)
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard(faker.lorem().sentence(5))
          .member(memberBase)
          .startDate(startAt)
          .endDate(LocalDateTime.now().plusMonths(1))
          .coteChallenges(new ArrayList<>())
          .build();


  CoteChallenge coteChallengeBase = CoteChallenge.builder()
          .id(1L)
          .challenge(challengeBase)
          .title(faker.lorem().sentence(5))
          .question(faker.internet().url())
          .startAt(startAt)
          .comments(List.of(CoteComment.builder()
                  .id(1L)
                  .member(memberBase)
                  .build()))
          .build();

  Challenge badChallenge = Challenge.builder()
          .id(1L)
          .title(faker.lorem().sentence(5))
          .imageUrl(faker.internet().url())
          .categoryType(CategoryType.COTE)
          .description(faker.lorem().sentence(15))
          .maxParticipant(10L)
          .currentParticipant(1L)
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard(faker.lorem().sentence(5))
          .member(Member.builder()
                  .loginId(faker.name().username())
                  .build())
          .coteChallenges(new ArrayList<>())
          .build();

  CoteComment commentBase = CoteComment.builder()
          .id(1L)
          .member(memberBase)
          .coteChallenge(coteChallengeBase)
          .imageUrl(faker.internet().url())
          .content(faker.name().username())
          .build();

  UserDetailsImpl userDetailsBase = new UserDetailsImpl(memberBase);

  @Test
  @DisplayName("코테 챌린지 추가 성공")
  void accCoteChallenge() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(challengeBase));

    CoteChallengeRequest form = CoteChallengeRequest.builder()
            .challengeId(1L)
            .title(faker.name().username())
            .question(faker.name().username())
            .startAt(startAt.plusDays(1))
            .build();
    //when
    CoteChallengeDto result =
            coteChallengeService.addCoteChallenge(form, userDetailsBase.getUsername());
    //then
    assertEquals(startAt.plusDays(1).toLocalDate(), result.getStartAt().toLocalDate());
    assertEquals(form.getTitle(), result.getTitle());
    assertEquals(form.getQuestion(), result.getQuestion());
    verify(coteChallengeRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("코테 챌린지 추가 실패(내가 만든 챌린지가 아님)")
  void accCoteChallengeFailure2() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(badChallenge));

    CoteChallengeRequest form = CoteChallengeRequest.builder()
            .challengeId(1L)
            .title(faker.lorem().sentence(5))
            .question(faker.internet().url())
            .startAt(startAt)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addCoteChallenge(form, userDetailsBase.getUsername()));

    //then
    assertEquals(NOT_OWNER_OF_CHALLENGE, exception.getErrorCode());
    verify(coteChallengeRepository, times(0)).save(any());
  }


  @Test
  @DisplayName("코테 챌린지 추가 실패(코테 챌린지가 아님)")
  void accCoteChallengeFailure1() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable((Challenge.builder()
                    .id(1L)
                    .title(faker.lorem().sentence(5))
                    .imageUrl(faker.internet().url())
                    .categoryType(CategoryType.DIET)
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .description(faker.lorem().sentence(15))
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.lorem().sentence(5))
                    .member(memberBase)
                    .coteChallenges(List.of(coteChallengeBase))
                    .startDate(startAt)
                    .endDate(LocalDateTime.now().plusMonths(1))
                    .build())));

    CoteChallengeRequest form = CoteChallengeRequest.builder()
            .challengeId(1L)
            .title(faker.lorem().sentence(5))
            .question(faker.internet().url())
            .startAt(startAt)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addCoteChallenge(form, userDetailsBase.getUsername()));

    //then
    assertEquals(NOT_COTE_CHALLENGE, exception.getErrorCode());
    verify(coteChallengeRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("코테 챌린지 추가 실패(챌린지 기간이 아님)")
  void accCoteChallengeFailure3() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(Challenge.builder()
                    .id(1L)
                    .title(faker.name().username())
                    .imageUrl(faker.name().username())
                    .categoryType(CategoryType.COTE)
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .description(faker.name().username())
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.name().username())
                    .member(memberBase)
                    .coteChallenges(List.of(coteChallengeBase))
                    .startDate(startAt)
                    .endDate(LocalDateTime.now().plusMonths(1))
                    .build()));

    CoteChallengeRequest form = CoteChallengeRequest.builder()
            .challengeId(1L)
            .title(faker.lorem().sentence(5))
            .question(faker.internet().url())
            .startAt(LocalDateTime.now().plusMonths(2))
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addCoteChallenge(form, userDetailsBase.getUsername()));

    //then
    assertEquals(NOT_ADDED_COTE_CHALLENGE, exception.getErrorCode());
    verify(coteChallengeRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("코테 챌린지 추가 실패(이미 코테챌린지가 추가됨)")
  void accCoteChallengeFailure4() {
    //given
    given(challengeRepository.searchChallengeWithCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(Challenge.builder()
                    .id(1L)
                    .title(faker.lorem().sentence(5))
                    .imageUrl(faker.internet().url())
                    .categoryType(CategoryType.COTE)
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .description(faker.lorem().sentence(15))
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.lorem().sentence(5))
                    .member(memberBase)
                    .coteChallenges(List.of(coteChallengeBase))
                    .startDate(startAt)
                    .endDate(LocalDateTime.now().plusMonths(1))
                    .build()));

    CoteChallengeRequest form = CoteChallengeRequest.builder()
            .challengeId(1L)
            .title(faker.lorem().sentence(5))
            .question(faker.internet().url())
            .startAt(startAt)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addCoteChallenge(form, userDetailsBase.getUsername()));

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
    CoteChallengeDto result =
            coteChallengeService.getCoteChallenge(1L);
    //then
    assertEquals(1L, result.getChallengeId());
    assertEquals(1L, result.getId());
    assertEquals(coteChallengeBase.getTitle(), result.getTitle());
    assertEquals(coteChallengeBase.getQuestion(), result.getQuestion());
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
            .willReturn(Optional.ofNullable(coteChallengeBase));

    CoteChallengeUpdateRequest form = CoteChallengeUpdateRequest.builder()
            .coteChallengeId(1L)
            .title(faker.name().username())
            .question(faker.name().username())
            .build();
    //when
    CoteChallengeDto result =
            coteChallengeService.updateCoteChallenge(form, userDetailsBase.getUsername());
    //then
    assertEquals(1L, result.getChallengeId());
    assertEquals(form.getTitle(), result.getTitle());
    assertEquals(form.getQuestion(), result.getQuestion());
  }

  @Test
  @DisplayName("코테 챌린지 수정 실패(내가 만든 챌린지가 아님)")
  void updateCoteChallengeFailure() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(CoteChallenge.builder()
                    .id(1L)
                    .title(faker.name().username())
                    .question(faker.name().username())
                    .startAt(startAt)
                    .challenge(badChallenge)
                    .build()));

    CoteChallengeUpdateRequest form = CoteChallengeUpdateRequest.builder()
            .coteChallengeId(1L)
            .title(faker.name().username())
            .question(faker.name().username())
            .build();
    //when

    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.updateCoteChallenge(form, userDetailsBase.getUsername()));
    //then
    assertEquals(NOT_OWNER_OF_CHALLENGE, exception.getErrorCode());
  }


  @Test
  @DisplayName("코테 챌린지 삭제 성공")
  void deleteCoteChallenge() {
    //given
    CoteChallenge cote = CoteChallenge.builder()
            .id(1L)
            .challenge(challengeBase)
            .title(faker.name().username())
            .question(faker.name().username())
            .startAt(startAt)
            .comments(new ArrayList<>())
            .build();

    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(cote));

    //when
    CoteChallengeDto result = coteChallengeService
            .deleteCoteChallenge(1L, userDetailsBase.getUsername());

    //then
    assertEquals(1L, result.getId());
    assertEquals(1L, result.getChallengeId());
    assertEquals(cote.getTitle(), result.getTitle());
    assertEquals(cote.getQuestion(), result.getQuestion());
    assertEquals(startAt, result.getStartAt());
  }


  @Test
  @DisplayName("코테 챌린지 삭제 성공(내가 만든 챌린지가 아님)")
  void deleteCoteChallengeFailure1() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(CoteChallenge.builder()
                    .id(1L)
                    .challenge(Challenge.builder()
                            .id(1L)
                            .title(faker.name().username())
                            .imageUrl(faker.internet().domainName())
                            .categoryType(CategoryType.COTE)
                            .description(faker.address().fullAddress())
                            .maxParticipant(10L)
                            .currentParticipant(1L)
                            .minDeposit(10L)
                            .maxDeposit(50L)
                            .standard(faker.address().cityName())
                            .member(Member.builder()
                                    .id(2L)
                                    .loginId(faker.name().username())
                                    .build())
                            .startDate(startAt)
                            .endDate(LocalDateTime.now().plusMonths(1))
                            .coteChallenges(new ArrayList<>())
                            .build())
                    .title(faker.name().username())
                    .question(faker.name().username())
                    .startAt(startAt)
                    .comments(new ArrayList<>())
                    .build()));
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.deleteCoteChallenge(1L, userDetailsBase.getUsername()));

    //then
    assertEquals(NOT_OWNER_OF_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("코테 챌린지 삭제 성공(내가 만든 챌린지가 아님)")
  void deleteCoteChallengeFailure2() {
    //given
    given(coteChallengeRepository.searchCoteChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(coteChallengeBase));
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.deleteCoteChallenge(1L, userDetailsBase.getUsername()));
    //then
    assertEquals(CANNOT_DELETE_HAVE_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("인증 댓글 추가 성공")
  void addComment() {
    //given
    CoteChallenge coteChallenge = CoteChallenge.builder()
            .id(1L)
            .challenge(challengeBase)
            .title(faker.name().username())
            .question(faker.name().username())
            .startAt(startAt.plusMonths(1).minusDays(20))
            .comments(new ArrayList<>())
            .build();

    given(coteChallengeRepository.searchCoteChallengeByStartAt(anyLong(), anyString(), any()))
            .willReturn(Optional.ofNullable(coteChallenge));

    CoteCommentRequest form = CoteCommentRequest.builder()
            .challengeId(1L)
            .imageUrl(faker.name().username())
            .content(faker.name().username())
            .build();

    Member member = Member.builder()
            .id(1L)
            .loginId(faker.name().username())
            .memberChallenges(List.of(MemberChallenge.builder()
                    .challenge(challengeBase)
                    .build()))
            .build();

    //when
    CoteCommentDto result =
            coteChallengeService.addComment(form, member);
    //then
    assertEquals(form.getImageUrl(), result.getImageUrl());
    assertEquals(form.getContent(), result.getContent());
    assertEquals(member.getLoginId(), result.getLoginId());
    assertEquals(1L, result.getCoteChallengeId());
    verify(coteCommentRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("인증 댓글 추가 실패(챌린지에 참여하지 않은 회원)")
  void addCommentFailure1() {
    //given

    CoteChallenge coteChallenge = CoteChallenge.builder()
            .id(1L)
            .challenge(challengeBase)
            .title(faker.name().username())
            .question(faker.name().username())
            .startAt(startAt.plusMonths(1).minusDays(20))
            .comments(new ArrayList<>())
            .build();

    given(coteChallengeRepository.searchCoteChallengeByStartAt(anyLong(), anyString(), any()))
            .willReturn(Optional.ofNullable(coteChallenge));

    CoteCommentRequest form = CoteCommentRequest.builder()
            .challengeId(1L)
            .imageUrl("이미지 링크")
            .content("어렵다 어려워")
            .build();

    //when

    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.addComment(form, userDetailsBase.getMember()));
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
    CoteCommentDto result = coteChallengeService.getComment(1L);
    //then
    assertEquals(1L, result.getCoteChallengeId());
    assertEquals(commentBase.getMember().getLoginId(), result.getLoginId());
    assertEquals(commentBase.getImageUrl(), result.getImageUrl());
    assertEquals(commentBase.getContent(), result.getContent());
  }


  @Test
  @DisplayName("인증 댓글 수정 성공")
  void updateComment() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(Optional.ofNullable(commentBase));
    CoteCommentUpdateRequest form = CoteCommentUpdateRequest.builder()
            .commentId(1L)
            .content(faker.name().username())
            .imageUrl(faker.internet().url())
            .build();
    //when
    CoteCommentDto result =
            coteChallengeService.updateComment(form, userDetailsBase.getUsername());

    //then
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
    assertEquals(form.getImageUrl(), result.getImageUrl());
    assertEquals(form.getContent(), result.getContent());
    assertEquals(1L, result.getCoteChallengeId());
  }

  @Test
  @DisplayName("인증 댓글 수정 실패(내가 작성한 인증 댓글이 아님)")
  void updateCommentFailure1() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(Optional.ofNullable(CoteComment.builder()
                    .id(1L)
                    .member(Member.builder()
                            .id(1L)
                            .loginId("실패멤버아이디")
                            .build())
                    .build()));
    CoteCommentUpdateRequest form = CoteCommentUpdateRequest.builder()
            .commentId(1L)
            .content("수정한 내용")
            .imageUrl("수정한 이미지 링크")
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.updateComment(form, userDetailsBase.getUsername()));

    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("인증 댓글 삭제 성공")
  void deleteComment() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(Optional.ofNullable(commentBase));
    //when
    CoteCommentDto result = coteChallengeService
            .deleteComment(1L, userDetailsBase.getUsername());
    //then
    assertEquals(commentBase.getMember().getLoginId(), result.getLoginId());
    assertEquals(commentBase.getImageUrl(), result.getImageUrl());
    assertEquals(commentBase.getContent(), result.getContent());
    assertEquals(1L, result.getCoteChallengeId());
  }


  @Test
  @DisplayName("인증 댓글 삭제 실패(내가 작성한 인증 댓글이 아님)")
  void deleteCommentFailure() {
    //given
    given(coteCommentRepository.searchCoteCommentById(anyLong()))
            .willReturn(Optional.ofNullable(CoteComment.builder()
                    .id(1L)
                    .member(Member.builder()
                            .id(1L)
                            .loginId("실패멤버아이디")
                            .build())
                    .build()));
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.deleteComment(1L, userDetailsBase.getUsername()));
    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("코테 댓글 삭제 성공 (관리자)")
  void adminDeleteComment() {
    //given
    given(coteCommentRepository.findById(anyLong())).willReturn(Optional.ofNullable(commentBase));

    //when
    CoteCommentDto result = coteChallengeService
            .adminDeleteComment(1L, MemberType.ADMIN);
    //then
    assertEquals(commentBase.getMember().getLoginId(), result.getLoginId());
    assertEquals(commentBase.getImageUrl(), result.getImageUrl());
    assertEquals(commentBase.getContent(), result.getContent());
    assertEquals(1L, result.getCoteChallengeId());
  }

  @Test
  @DisplayName("코테 댓글 삭제 실패 (관리자가 아님)")
  void adminDeleteCommentFailure() {
    //given
    given(coteCommentRepository.findById(anyLong())).willReturn(Optional.ofNullable(commentBase));

    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            coteChallengeService.adminDeleteComment(1L, MemberType.USER));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

}