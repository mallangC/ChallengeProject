package com.zerobase.challengeproject.comment.service;

import com.github.javafaker.Faker;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
class DietChallengeServiceTest {
  @Mock
  private ChallengeRepository challengeRepository;

  @Mock
  private DietChallengeRepository dietChallengeRepository;

  @Mock
  private DietCommentRepository dietCommentRepository;

  @InjectMocks
  private DietChallengeService dietChallengeService;

  private final Faker faker = new Faker();

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

  Member memberBad = Member.builder()
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
          .img(faker.internet().url())
          .categoryType(CategoryType.DIET)
          .maxParticipant(10L)
          .currentParticipant(1L)
          .description(faker.lorem().sentence(15))
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard(faker.lorem().sentence(5))
          .member(memberBase)
          .startDate(LocalDateTime.now().plusDays(1))
          .coteChallenges(new ArrayList<>())
          .dietChallenges(new ArrayList<>())
          .build();

  DietChallenge dietChallengeBase = DietChallenge.builder()
          .id(1L)
          .challenge(challengeBase)
          .member(memberBase)
          .currentWeight(65.2f)
          .goalWeight(55.7f)
          .comments(new ArrayList<>())
          .build();

  DietComment dietCommentBase = DietComment.builder()
          .id(1L)
          .dietChallenge(dietChallengeBase)
          .member(memberBase)
          .imageUrl(faker.internet().url())
          .content(faker.lorem().sentence(10))
          .build();

  UserDetailsImpl userDetailsBase = new UserDetailsImpl(memberBase);

  @Test
  @DisplayName("다이어트 챌린지 추가 성공")
  void addDietChallenge() {
    //given
    given(challengeRepository.searchChallengeWithDietChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(challengeBase));

    DietChallengeAddRequest form = DietChallengeAddRequest.builder()
            .challengeId(1L)
            .imageUrl(faker.internet().url())
            .currentWeight(65.2f)
            .goalWeight(55.7f)
            .build();
    //when
    DietChallengeDto result =
            dietChallengeService.addDietChallenge(form, userDetailsBase.getMember());
    //then
    assertEquals(form.getCurrentWeight(), result.getCurrentWeight());
    assertEquals(form.getGoalWeight(), result.getGoalWeight());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
    verify(dietChallengeRepository, times(1)).save(any());
    verify(dietCommentRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("다이어트 챌린지 추가 실패(다이어트 챌린지가 아님)")
  void addDietChallengeFailure1() {
    //given
    given(challengeRepository.searchChallengeWithDietChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(Challenge.builder()
                    .id(1L)
                    .title(faker.lorem().sentence(5))
                    .img(faker.internet().url())
                    .categoryType(CategoryType.COTE)
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .description(faker.lorem().sentence(15))
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.lorem().sentence(5))
                    .member(memberBase)
                    .coteChallenges(new ArrayList<>())
                    .dietChallenges(new ArrayList<>())
                    .build()));

    DietChallengeAddRequest form = DietChallengeAddRequest.builder()
            .challengeId(1L)
            .imageUrl(faker.internet().url())
            .currentWeight(65.2f)
            .goalWeight(55.7f)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.addDietChallenge(form, userDetailsBase.getMember()));
    //then
    assertEquals(NOT_DIET_CHALLENGE, exception.getErrorCode());
    verify(dietChallengeRepository, times(0)).save(any());
    verify(dietCommentRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("다이어트 챌린지 추가 실패(이미 다이어트 챌린지가 있음(이미 참여함))")
  void addDietChallengeFailure2() {
    //given
    given(challengeRepository.searchChallengeWithDietChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(Challenge.builder()
                    .id(1L)
                    .title(faker.lorem().sentence(5))
                    .img(faker.internet().url())
                    .categoryType(CategoryType.DIET)
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .description(faker.lorem().sentence(15))
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.lorem().sentence(5))
                    .member(memberBase)
                    .coteChallenges(new ArrayList<>())
                    .dietChallenges(List.of(dietChallengeBase))
                    .build()));

    DietChallengeAddRequest form = DietChallengeAddRequest.builder()
            .challengeId(1L)
            .imageUrl(faker.internet().url())
            .currentWeight(65.2f)
            .goalWeight(55.7f)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.addDietChallenge(form, userDetailsBase.getMember()));
    //then
    assertEquals(ALREADY_ADDED_DIET_CHALLENGE, exception.getErrorCode());
    verify(dietChallengeRepository, times(0)).save(any());
    verify(dietCommentRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("다이어트 챌린지 조회 성공")
  void getDietChallenge() {
    //given
    given(dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(dietChallengeBase));

    //when
    DietChallengeDto result =
            dietChallengeService.getDietChallenge(1L, userDetailsBase.getUsername());
    //then
    assertEquals(dietChallengeBase.getCurrentWeight(), result.getCurrentWeight());
    assertEquals(dietChallengeBase.getGoalWeight(), result.getGoalWeight());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
  }

  @Test
  @DisplayName("다이어트 챌린지 수정 성공")
  void updateDietChallenge() {
    //given
    given(dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(dietChallengeBase));

    DietChallengeUpdateRequest form = DietChallengeUpdateRequest.builder()
            .challengeId(1L)
            .currentWeight(67.2f)
            .goalWeight(57.7f)
            .build();
    //when
    DietChallengeDto result =
            dietChallengeService.updateDietChallenge(form, userDetailsBase.getUsername());
    //then
    assertEquals(form.getCurrentWeight(), result.getCurrentWeight());
    assertEquals(form.getGoalWeight(), result.getGoalWeight());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
  }

  @Test
  @DisplayName("다이어트 챌린지 수정 실패(챌린지가 시작되고 수정 불가)")
  void updateDietChallengeFailure1() {
    //given
    Challenge challenge = Challenge.builder()
            .id(1L)
            .title(faker.lorem().sentence(5))
            .img(faker.internet().url())
            .categoryType(CategoryType.DIET)
            .maxParticipant(10L)
            .currentParticipant(1L)
            .description(faker.lorem().sentence(15))
            .minDeposit(10L)
            .maxDeposit(50L)
            .standard(faker.lorem().sentence(5))
            .member(memberBase)
            .startDate(LocalDateTime.now().minusDays(5))
            .coteChallenges(new ArrayList<>())
            .dietChallenges(new ArrayList<>())
            .build();

    DietChallenge dietChallenge = DietChallenge.builder()
            .id(1L)
            .challenge(challenge)
            .member(memberBase)
            .currentWeight(65.2f)
            .goalWeight(55.7f)
            .comments(new ArrayList<>())
            .build();

    given(dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(dietChallenge));

    DietChallengeUpdateRequest form = DietChallengeUpdateRequest.builder()
            .challengeId(1L)
            .currentWeight(67.2f)
            .goalWeight(57.7f)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.updateDietChallenge(form, userDetailsBase.getUsername()));
    //then
    assertEquals(CANNOT_UPDATE_AFTER_START_CHALLENGE, exception.getErrorCode());
  }


  @Test
  @DisplayName("다이어트 댓글 추가 성공")
  void addDietComment() {
    //given
    given(dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(dietChallengeBase));

    DietCommentAddRequest form = DietCommentAddRequest.builder()
            .challengeId(1L)
            .imageUrl(faker.internet().url())
            .content(faker.lorem().sentence(15))
            .currentWeight(50.2f)
            .build();
    //when
    DietCommentDto result =
            dietChallengeService.addDietComment(form, userDetailsBase.getMember());
    //then
    assertEquals(form.getImageUrl(), result.getImageUrl());
    assertEquals(form.getContent(), result.getContent());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
    verify(dietCommentRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("다이어트 댓글 단건 조회 성공")
  void getDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(Optional.ofNullable(dietCommentBase));

    //when
    DietCommentDto result =
            dietChallengeService.getDietComment(1L);
    //then
    assertEquals(dietCommentBase.getImageUrl(), result.getImageUrl());
    assertEquals(dietCommentBase.getContent(), result.getContent());
    assertEquals(dietCommentBase.getMember().getLoginId(), result.getLoginId());
  }

  @Test
  @DisplayName("다이어트 댓글 수정 성공")
  void updateDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(Optional.ofNullable(dietCommentBase));

    DietCommentUpdateRequest form = DietCommentUpdateRequest.builder()
            .commentId(1L)
            .imageUrl(faker.internet().url())
            .content(faker.lorem().sentence(15))
            .currentWeight(57.2f)
            .build();

    //when
    DietCommentDto result =
            dietChallengeService.updateDietComment(form, userDetailsBase.getMember());
    //then
    assertEquals(form.getImageUrl(), result.getImageUrl());
    assertEquals(form.getContent(), result.getContent());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
  }

  @Test
  @DisplayName("다이어트 댓글 수정 실패(회원 본인이 작성한 댓글이 아님)")
  void updateDietCommentFailure() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(Optional.ofNullable(DietComment.builder()
                    .id(1L)
                    .dietChallenge(dietChallengeBase)
                    .member(memberBad)
                    .imageUrl(faker.internet().url())
                    .content(faker.lorem().sentence(15))
                    .build()));

    DietCommentUpdateRequest form = DietCommentUpdateRequest.builder()
            .commentId(1L)
            .imageUrl(faker.internet().url())
            .content(faker.lorem().sentence(15))
            .currentWeight(57.2f)
            .build();

    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.updateDietComment(form, userDetailsBase.getMember()));

    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("다이어트 댓글 삭제 성공")
  void deleteDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(Optional.ofNullable(dietCommentBase));

    //when
    DietCommentDto result =
            dietChallengeService.deleteDietComment(1L, userDetailsBase.getMember());
    //then
    assertEquals(dietCommentBase.getImageUrl(), result.getImageUrl());
    assertEquals(dietCommentBase.getContent(), result.getContent());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
    verify(dietCommentRepository, times(1)).delete(any());
  }


  @Test
  @DisplayName("다이어트 댓글 삭제 실패(회원 본인이 작성한 댓글이 아님)")
  void deleteDietCommentFailure() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(Optional.ofNullable(DietComment.builder()
                    .id(1L)
                    .dietChallenge(dietChallengeBase)
                    .member(memberBad)
                    .imageUrl(faker.internet().url())
                    .content(faker.lorem().sentence(15))
                    .build()));
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.deleteDietComment(1L, userDetailsBase.getMember()));
    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }


  @Test
  @DisplayName("다이어트 댓글 삭제 성공(관리자)")
  void adminDeleteDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(Optional.ofNullable(dietCommentBase));

    //when
    DietCommentDto result =
            dietChallengeService.adminDeleteDietComment(1L, MemberType.ADMIN);
    //then
    assertEquals(dietCommentBase.getImageUrl(), result.getImageUrl());
    assertEquals(dietCommentBase.getContent(), result.getContent());
    assertEquals(dietCommentBase.getMember().getLoginId(), result.getLoginId());
    verify(dietCommentRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("다이어트 댓글 삭제 실패(관리자)(관리자가 아님)")
  void adminDeleteDietCommentFailure() {
    //given
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.adminDeleteDietComment(1L, MemberType.USER));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

  @Test
  @DisplayName("다이어트 챌린지 전체 조회 성공(관리자)")
  void getAllDietChallenge() {
    //given
    Pageable pageable = PageRequest.of(0, 20);
    given(dietChallengeRepository.searchAllDietChallengeByChallengeId(anyInt(), anyLong(), anyBoolean()))
            .willReturn(new PageImpl<>(List.of(dietChallengeBase), pageable, 1));
    int page = 1;
    //when
    Page<DietChallengeDto> result =
            dietChallengeService.getAllDietChallenge(page, 1L, true, MemberType.ADMIN);
    //then
    assertEquals(dietChallengeBase.getMember().getLoginId(), result.getContent().get(0).getLoginId());
    assertEquals(dietChallengeBase.getGoalWeight(), result.getContent().get(0).getGoalWeight());
    assertEquals(dietChallengeBase.getCurrentWeight(), result.getContent().get(0).getCurrentWeight());
  }

  @Test
  @DisplayName("다이어트 챌린지 전체 조회 실패(관리자가 아님)")
  void getAllDietChallengeFailure() {
    //given
    int page = 1;
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.getAllDietChallenge(page, 1L, true, MemberType.USER));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

}