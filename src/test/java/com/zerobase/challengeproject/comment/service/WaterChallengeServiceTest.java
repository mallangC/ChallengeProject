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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
          .categoryType(CategoryType.WATER)
          .description("challengeDescription")
          .maxParticipant(10L)
          .currentParticipant(1L)
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard("challengeStandard")
          .member(memberBase)
          .startDate(LocalDateTime.of(2025, 4, 10, 0, 0, 0))
          .coteChallenges(new ArrayList<>())
          .build();

  WaterChallenge waterChallengeBase = WaterChallenge.builder()
          .id(1L)
          .member(memberBase)
          .challenge(challengeBase)
          .currentMl(0)
          .goalMl(1000)
          .build();

  WaterComment waterCommentBase = WaterComment.builder()
          .id(1L)
          .member(memberBase)
          .waterChallenge(waterChallengeBase)
          .drinkingMl(200)
          .image("댓글베이스 이미지주소")
          .build();

  UserDetailsImpl userDetailsBase = new UserDetailsImpl(memberBase);


  @Test
  @DisplayName("물마시기 챌린지 추가 성공")
  void addWaterChallenge() {
    //given
    given(challengeRepository.searchChallengeWithWaterChallengeById(anyLong()))
            .willReturn(Challenge.builder()
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
                    .waterChallenges(new ArrayList<>())
                    .build());
    WaterChallengeForm form = WaterChallengeForm.builder()
            .challengeId(1L)
            .goalMl(1000)
            .build();
    //when
    BaseResponseDto<WaterChallengeDto> result =
            waterChallengeService.addWaterChallenge(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("물마시기 챌린지 추가를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("test", result.getData().getLoginId());
    assertEquals(1000, result.getData().getGoalMl());
    assertEquals(0, result.getData().getCurrentMl());
    verify(waterChallengeRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("물마시기 챌린지 추가 실패(물마시기 챌린지가 아님)")
  void addWaterChallengeFailure1() {
    //given
    given(challengeRepository.searchChallengeWithWaterChallengeById(anyLong()))
            .willReturn(Challenge.builder()
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
                    .waterChallenges(new ArrayList<>())
                    .build());
    WaterChallengeForm form = WaterChallengeForm.builder()
            .challengeId(1L)
            .goalMl(1000)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.addWaterChallenge(form, userDetailsBase));
    //then
    assertEquals(NOT_WATER_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 챌린지 추가 실패(이미 추가함)")
  void addWaterChallengeFailure2() {
    //given
    given(challengeRepository.searchChallengeWithWaterChallengeById(anyLong()))
            .willReturn(Challenge.builder()
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
                    .waterChallenges(List.of(WaterChallenge.builder()
                            .member(memberBase)
                            .build()))
                    .build());
    WaterChallengeForm form = WaterChallengeForm.builder()
            .challengeId(1L)
            .goalMl(1000)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.addWaterChallenge(form, userDetailsBase));
    //then
    assertEquals(ALREADY_ADDED_DIET_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("오늘의 물마시기 챌린지 조회 성공")
  void getWaterChallenge() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(waterChallengeBase);
    //when
    BaseResponseDto<WaterChallengeDto> result =
            waterChallengeService.getWaterChallenge(1L, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("오늘의 물마시기 챌린지 조회를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("test", result.getData().getLoginId());
    assertEquals(1000, result.getData().getGoalMl());
    assertEquals(0, result.getData().getCurrentMl());
  }

  @Test
  @DisplayName("물마시기 챌린지 전체 조회 성공(관리자)")
  void getAllWaterChallenge() {
    //given
    UserDetailsImpl userDetails = new UserDetailsImpl(Member.builder()
            .memberType(MemberType.ADMIN)
            .build());
    Pageable pageable = PageRequest.of(0, 20);
    given(waterChallengeRepository.searchAllWaterChallengeByChallengeId(anyInt(), anyLong(), anyBoolean()))
            .willReturn(new PageImpl<>(List.of(WaterChallengeDto.fromWithoutComment(waterChallengeBase)), pageable, 0));
    //when
    BaseResponseDto<PageDto<WaterChallengeDto>> result =
            waterChallengeService.getAllWaterChallenge(1, 1L, true, userDetails);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("물마시기 챌린지 전체 조회를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getContent().get(0).getChallengeId());
    assertEquals("test", result.getData().getContent().get(0).getLoginId());
    assertEquals(1000, result.getData().getContent().get(0).getGoalMl());
    assertEquals(0, result.getData().getContent().get(0).getCurrentMl());
  }

  @Test
  @DisplayName("물마시기 챌린지 전체 조회 실패(관리자)")
  void getAllWaterChallengeFailure() {
    //given
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.getAllWaterChallenge(1, 1L, true, userDetailsBase));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 챌린지 수정 성공")
  void updateWaterChallenge() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(waterChallengeBase);
    WaterChallengeForm form = WaterChallengeForm.builder()
            .challengeId(1L)
            .goalMl(1200)
            .build();
    //when
    BaseResponseDto<WaterChallengeDto> result =
            waterChallengeService.updateWaterChallenge(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("물마시기 챌린지 수정을 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("test", result.getData().getLoginId());
    assertEquals(1200, result.getData().getGoalMl());
    assertEquals(0, result.getData().getCurrentMl());
  }

  @Test
  @DisplayName("물마시기 챌린지 수정 실패(챌린지가 시작해서 수정 불가)")
  void updateWaterChallengeFailure() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(WaterChallenge.builder()
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
                            .startDate(LocalDateTime.of(2025, 4, 5, 0, 0, 0))
                            .coteChallenges(new ArrayList<>())
                            .build())
                    .currentMl(0)
                    .goalMl(1000)
                    .build());
    WaterChallengeForm form = WaterChallengeForm.builder()
            .challengeId(1L)
            .goalMl(1200)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.updateWaterChallenge(form, userDetailsBase));
    //then
    assertEquals(CANNOT_UPDATE_AFTER_START_CHALLENGE, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 댓글 추가 성공")
  void addWaterComment() {
    //given
    given(waterChallengeRepository.searchWaterChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(waterChallengeBase);
    WaterCommentAddForm form = WaterCommentAddForm.builder()
            .challengeId(1L)
            .drinkingMl(200)
            .image("댓글추가 이미지주소")
            .build();
    //when
    BaseResponseDto<WaterCommentDto> result =
            waterChallengeService.addWaterComment(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("물마시기 댓글 추가를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals(200, result.getData().getDrinkingMl());
    assertEquals("댓글추가 이미지주소", result.getData().getImage());
    assertEquals("test", result.getData().getLonginId());
    verify(waterCommentRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("물마시기 댓글 단건 조회 성공")
  void getWaterComment() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(waterCommentBase);
    //when
    BaseResponseDto<WaterCommentDto> result =
            waterChallengeService.getWaterComment(1L);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("물마시기 댓글 단건 조회를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("test", result.getData().getLonginId());
    assertEquals("댓글베이스 이미지주소", result.getData().getImage());
    assertEquals(200, result.getData().getDrinkingMl());
  }

  @Test
  @DisplayName("물마시기 댓글 수정 성공")
  void updateWaterComment() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(waterCommentBase);
    WaterCommentUpdateForm form = WaterCommentUpdateForm.builder()
            .commentId(1L)
            .drinkingMl(200)
            .image("수정된 이미지주소")
            .build();
    //when
    BaseResponseDto<WaterCommentDto> result =
            waterChallengeService.updateWaterComment(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("물마시기 댓글 수정을 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("test", result.getData().getLonginId());
    assertEquals("수정된 이미지주소", result.getData().getImage());
    assertEquals(200, result.getData().getDrinkingMl());
  }

  @Test
  @DisplayName("물마시기 댓글 수정 실패(댓글의 작성자가 아님)")
  void updateWaterCommentFailure() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(waterCommentBase);
    WaterCommentUpdateForm form = WaterCommentUpdateForm.builder()
            .commentId(1L)
            .drinkingMl(200)
            .image("수정된 이미지주소")
            .build();
    UserDetailsImpl userDetails = new UserDetailsImpl(Member.builder()
            .memberId("tttest")
            .build());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.updateWaterComment(form, userDetails));
    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("물마시기 댓글 삭제 성공(관리자)")
  void adminDeleteWaterComment() {
    //given
    given(waterCommentRepository.searchWaterCommentById(anyLong()))
            .willReturn(waterCommentBase);
    UserDetailsImpl userDetails = new UserDetailsImpl(Member.builder()
            .memberId("admin")
            .memberType(MemberType.ADMIN)
            .build());
    //when
    BaseResponseDto<WaterCommentDto> result = waterChallengeService.adminDeleteWaterComment(1L, userDetails);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("물마시기 댓글 삭제를 성공했습니다.", result.getMessage());
    assertEquals(1L, result.getData().getChallengeId());
    assertEquals("test", result.getData().getLonginId());
    assertEquals("댓글베이스 이미지주소", result.getData().getImage());
    assertEquals(200, result.getData().getDrinkingMl());
  }

  @Test
  @DisplayName("물마시기 댓글 삭제 실패(관리자)(관리자가 아님)")
  void adminDeleteWaterCommentFailure() {
    //given
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            waterChallengeService.adminDeleteWaterComment(1L, userDetailsBase));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }


}