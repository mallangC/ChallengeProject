package com.zerobase.challengeproject.comment.service;

import com.github.javafaker.Faker;
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
class WaterChallengeServiceTest {

  @Mock
  private WaterChallengeRepository waterChallengeRepository;

  @Mock
  private WaterCommentRepository waterCommentRepository;

  @Mock
  private ChallengeRepository challengeRepository;

  @InjectMocks
  private WaterChallengeService waterChallengeService;

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

  Challenge challengeBase = Challenge.builder()
          .id(1L)
          .title(faker.lorem().sentence(5))
          .img(faker.internet().url())
          .categoryType(CategoryType.WATER)
          .description(faker.lorem().sentence(15))
          .maxParticipant(10L)
          .currentParticipant(1L)
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard(faker.lorem().sentence(5))
          .member(memberBase)
          .startDate(LocalDateTime.now().plusDays(1))
          .coteChallenges(new ArrayList<>())
          .build();

  WaterChallenge waterChallengeBase = WaterChallenge.builder()
          .id(1L)
          .member(memberBase)
          .challenge(challengeBase)
          .currentIntake(0)
          .goalIntake(1000)
          .build();

  WaterComment waterCommentBase = WaterComment.builder()
          .id(1L)
          .member(memberBase)
          .waterChallenge(waterChallengeBase)
          .drinkingIntake(200)
          .imageUrl(faker.internet().url())
          .build();

  UserDetailsImpl userDetailsBase = new UserDetailsImpl(memberBase);


  @Test
  @DisplayName("물마시기 챌린지 추가 성공")
  void addWaterChallenge() {
    //given
    given(challengeRepository.searchChallengeWithWaterChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(Challenge.builder()
                    .id(1L)
                    .title(faker.lorem().sentence(5))
                    .img(faker.internet().url())
                    .categoryType(CategoryType.WATER)
                    .description(faker.lorem().sentence(15))
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.lorem().sentence(5))
                    .member(memberBase)
                    .waterChallenges(new ArrayList<>())
                    .build()));
    WaterChallengeRequest form = WaterChallengeRequest.builder()
            .challengeId(1L)
            .goalIntake(1000)
            .build();
    //when
    WaterChallengeDto result =
            waterChallengeService.addWaterChallenge(form, userDetailsBase.getMember());
    //then
    assertEquals(1L, result.getChallengeId());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
    assertEquals(form.getGoalIntake(), result.getGoalIntake());
    assertEquals(0, result.getCurrentIntake());
    verify(waterChallengeRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("물마시기 챌린지 추가 실패(물마시기 챌린지가 아님)")
  void addWaterChallengeFailure1() {
    //given
    given(challengeRepository.searchChallengeWithWaterChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(Challenge.builder()
                    .id(1L)
                    .title(faker.lorem().sentence(5))
                    .img(faker.internet().url())
                    .categoryType(CategoryType.COTE)
                    .description(faker.lorem().sentence(15))
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.lorem().sentence(5))
                    .member(memberBase)
                    .waterChallenges(new ArrayList<>())
                    .build()));
    WaterChallengeRequest form = WaterChallengeRequest.builder()
            .challengeId(1L)
            .goalIntake(1000)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.addWaterChallenge(form, userDetailsBase.getMember()));
    //then
    assertEquals(NOT_WATER_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 챌린지 추가 실패(이미 추가함)")
  void addWaterChallengeFailure2() {
    //given
    given(challengeRepository.searchChallengeWithWaterChallengeById(anyLong()))
            .willReturn(Optional.ofNullable(Challenge.builder()
                    .id(1L)
                    .title(faker.lorem().sentence(5))
                    .img(faker.internet().url())
                    .categoryType(CategoryType.WATER)
                    .description(faker.lorem().sentence(15))
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard(faker.lorem().sentence(5))
                    .waterChallenges(List.of(WaterChallenge.builder()
                            .member(memberBase)
                            .build()))
                    .build()));
    WaterChallengeRequest form = WaterChallengeRequest.builder()
            .challengeId(1L)
            .goalIntake(1000)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.addWaterChallenge(form, userDetailsBase.getMember()));
    //then
    assertEquals(ALREADY_ADDED_WATER_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("오늘의 물마시기 챌린지 조회 성공")
  void getWaterChallenge() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(waterChallengeBase));
    //when
    WaterChallengeDto result =
            waterChallengeService.getWaterChallenge(1L, userDetailsBase.getMember());
    //then
    assertEquals(waterChallengeBase.getChallenge().getId(), result.getChallengeId());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
    assertEquals(waterChallengeBase.getGoalIntake(), result.getGoalIntake());
    assertEquals(waterChallengeBase.getCurrentIntake(), result.getCurrentIntake());
  }

  @Test
  @DisplayName("물마시기 챌린지 전체 조회 성공(관리자)")
  void getAllWaterChallenge() {
    //given
    Pageable pageable = PageRequest.of(0, 20);
    given(waterChallengeRepository.searchAllWaterChallengeByChallengeId(anyInt(), anyLong(), anyBoolean()))
            .willReturn(new PageImpl<>(List.of(waterChallengeBase), pageable, 1));
    //when
    Page<WaterChallengeDto> result =
            waterChallengeService.getAllWaterChallenge(1, 1L, true, MemberType.ADMIN);
    //then
    assertEquals(waterChallengeBase.getChallenge().getId(), result.getContent().get(0).getChallengeId());
    assertEquals(waterChallengeBase.getMember().getLoginId(), result.getContent().get(0).getLoginId());
    assertEquals(waterChallengeBase.getGoalIntake(), result.getContent().get(0).getGoalIntake());
    assertEquals(0, result.getContent().get(0).getCurrentIntake());
  }

  @Test
  @DisplayName("물마시기 챌린지 전체 조회 실패(관리자)")
  void getAllWaterChallengeFailure() {
    //given
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.getAllWaterChallenge(1, 1L, true, MemberType.USER));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 챌린지 수정 성공")
  void updateWaterChallenge() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(waterChallengeBase));
    WaterChallengeRequest form = WaterChallengeRequest.builder()
            .challengeId(1L)
            .goalIntake(1200)
            .build();
    //when
    WaterChallengeDto result =
            waterChallengeService.updateWaterChallenge(form, userDetailsBase.getUsername());
    //then
    assertEquals(form.getChallengeId(), result.getChallengeId());
    assertEquals(userDetailsBase.getUsername(), result.getLoginId());
    assertEquals(form.getGoalIntake(), result.getGoalIntake());
    assertEquals(0, result.getCurrentIntake());
  }

  @Test
  @DisplayName("물마시기 챌린지 수정 실패(챌린지가 시작해서 수정 불가)")
  void updateWaterChallengeFailure() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(WaterChallenge.builder()
                    .id(1L)
                    .member(memberBase)
                    .challenge(Challenge.builder()
                            .id(1L)
                            .title("challengeTitle")
                            .img("challengeImg")
                            .categoryType(CategoryType.WATER)
                            .description("challengeDescription")
                            .maxParticipant(10L)
                            .currentParticipant(1L)
                            .minDeposit(10L)
                            .maxDeposit(50L)
                            .standard("challengeStandard")
                            .member(memberBase)
                            .startDate(LocalDateTime.now().minusDays(2))
                            .coteChallenges(new ArrayList<>())
                            .build())
                    .currentIntake(0)
                    .goalIntake(1000)
                    .build()));
    WaterChallengeRequest form = WaterChallengeRequest.builder()
            .challengeId(1L)
            .goalIntake(1200)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.updateWaterChallenge(form, userDetailsBase.getUsername()));
    //then
    assertEquals(CANNOT_UPDATE_AFTER_START_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 댓글 추가 성공")
  void addWaterComment() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(Optional.ofNullable(waterChallengeBase));
    WaterCommentAddRequest form = WaterCommentAddRequest.builder()
            .challengeId(1L)
            .drinkingIntake(200)
            .imageUrl(faker.internet().url())
            .build();
    //when
    WaterCommentDto result =
            waterChallengeService.addWaterComment(form, userDetailsBase.getMember());
    //then
    assertEquals(form.getChallengeId(), result.getChallengeId());
    assertEquals(form.getDrinkingIntake(), result.getDrinkingIntake());
    assertEquals(form.getImageUrl(), result.getImageUrl());
    assertEquals(userDetailsBase.getUsername(), result.getLonginId());
    verify(waterCommentRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("물마시기 댓글 단건 조회 성공")
  void getWaterComment() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(Optional.ofNullable(waterCommentBase));
    //when
    WaterCommentDto result =
            waterChallengeService.getWaterComment(1L);
    //then
    assertEquals(1L, result.getChallengeId());
    assertEquals(waterCommentBase.getMember().getLoginId(), result.getLonginId());
    assertEquals(waterCommentBase.getImageUrl(), result.getImageUrl());
    assertEquals(200, result.getDrinkingIntake());
  }

  @Test
  @DisplayName("물마시기 댓글 수정 성공")
  void updateWaterComment() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(Optional.ofNullable(waterCommentBase));
    WaterCommentUpdateRequest form = WaterCommentUpdateRequest.builder()
            .commentId(1L)
            .drinkingIntake(200)
            .imageUrl(faker.internet().url())
            .build();
    //when
    WaterCommentDto result =
            waterChallengeService.updateWaterComment(form, userDetailsBase.getUsername());
    //then
    assertEquals(1L, result.getChallengeId());
    assertEquals(userDetailsBase.getUsername(), result.getLonginId());
    assertEquals(form.getImageUrl(), result.getImageUrl());
    assertEquals(200, result.getDrinkingIntake());
  }

  @Test
  @DisplayName("물마시기 댓글 수정 실패(댓글의 작성자가 아님)")
  void updateWaterCommentFailure() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(Optional.ofNullable(waterCommentBase));
    WaterCommentUpdateRequest form = WaterCommentUpdateRequest.builder()
            .commentId(1L)
            .drinkingIntake(200)
            .imageUrl(faker.internet().url())
            .build();
    UserDetailsImpl userDetails = new UserDetailsImpl(Member.builder()
            .loginId(faker.name().username())
            .build());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.updateWaterComment(form, userDetails.getUsername()));
    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 댓글 삭제 성공(관리자)")
  void adminDeleteWaterComment() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(Optional.ofNullable(waterCommentBase));
    //when
    WaterCommentDto result = waterChallengeService
            .adminDeleteWaterComment(1L, MemberType.ADMIN);
    //then
    assertEquals(1L, result.getChallengeId());
    assertEquals(waterCommentBase.getMember().getLoginId(), result.getLonginId());
    assertEquals(waterCommentBase.getImageUrl(), result.getImageUrl());
    assertEquals(200, result.getDrinkingIntake());
  }

  @Test
  @DisplayName("물마시기 댓글 삭제 실패(관리자)(관리자가 아님)")
  void adminDeleteWaterCommentFailure() {
    //given
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.adminDeleteWaterComment(1L, MemberType.USER));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

}