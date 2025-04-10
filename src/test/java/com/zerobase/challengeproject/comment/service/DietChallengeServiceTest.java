package com.zerobase.challengeproject.comment.service;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.account.domain.dto.PageDto;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.DietChallengeDto;
import com.zerobase.challengeproject.comment.domain.dto.DietCommentDto;
import com.zerobase.challengeproject.comment.domain.form.DietChallengeAddForm;
import com.zerobase.challengeproject.comment.domain.form.DietChallengeUpdateForm;
import com.zerobase.challengeproject.comment.domain.form.DietCommentAddForm;
import com.zerobase.challengeproject.comment.domain.form.DietCommentUpdateForm;
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
class DietChallengeServiceTest {
  @Mock
  private ChallengeRepository challengeRepository;

  @Mock
  private DietChallengeRepository dietChallengeRepository;

  @Mock
  private DietCommentRepository dietCommentRepository;

  @InjectMocks
  private DietChallengeService dietChallengeService;

  Member memberBase = Member.builder()
          .id(1L)
          .loginId("test")
          .memberType(MemberType.USER)
          .memberName("testName")
          .nickname("testNickname")
          .email("test@test.com")
          .account(10000L)
          .memberChallenges(new ArrayList<>())
          .coteComments(new ArrayList<>())
          .accountDetails(new ArrayList<>())
          .build();

  Member memberBad = Member.builder()
          .id(1L)
          .loginId("testBad")
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
          .categoryType(CategoryType.DIET)
          .maxParticipant(10L)
          .currentParticipant(1L)
          .description("challengeDescription")
          .minDeposit(10L)
          .maxDeposit(50L)
          .standard("challengeStandard")
          .member(memberBase)
          .startDate(LocalDateTime.parse("2025-04-10T00:00:00"))
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
          .image("베이스 이미지주소")
          .content("베이스 내용")
          .build();

  UserDetailsImpl userDetailsBase = new UserDetailsImpl(memberBase);

  @Test
  @DisplayName("다이어트 챌린지 추가 성공")
  void addDietChallenge() {
    //given
    given(challengeRepository.searchChallengeWithDietChallengeById(anyLong()))
            .willReturn(challengeBase);

    DietChallengeAddForm form = DietChallengeAddForm.builder()
            .challengeId(1L)
            .image("다이어트 챌린지 추가 이미지주소")
            .currentWeight(65.2f)
            .goalWeight(55.7f)
            .build();
    //when
    BaseResponseDto<DietChallengeDto> result =
            dietChallengeService.addDietChallenge(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 챌린지 추가를 성공했습니다.", result.getMessage());
    assertEquals(65.2f, result.getData().getCurrentWeight());
    assertEquals(55.7f, result.getData().getGoalWeight());
    assertEquals("test", result.getData().getLoginId());
    verify(dietChallengeRepository, times(1)).save(any());
    verify(dietCommentRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("다이어트 챌린지 추가 실패(다이어트 챌린지가 아님)")
  void addDietChallengeFailure1() {
    //given
    given(challengeRepository.searchChallengeWithDietChallengeById(anyLong()))
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
                    .coteChallenges(new ArrayList<>())
                    .dietChallenges(new ArrayList<>())
                    .build());

    DietChallengeAddForm form = DietChallengeAddForm.builder()
            .challengeId(1L)
            .image("다이어트 챌린지 추가 이미지주소")
            .currentWeight(65.2f)
            .goalWeight(55.7f)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.addDietChallenge(form, userDetailsBase));
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
            .willReturn(Challenge.builder()
                    .id(1L)
                    .title("challengeTitle")
                    .img("challengeImg")
                    .categoryType(CategoryType.DIET)
                    .maxParticipant(10L)
                    .currentParticipant(1L)
                    .description("challengeDescription")
                    .minDeposit(10L)
                    .maxDeposit(50L)
                    .standard("challengeStandard")
                    .member(memberBase)
                    .coteChallenges(new ArrayList<>())
                    .dietChallenges(List.of(dietChallengeBase))
                    .build());

    DietChallengeAddForm form = DietChallengeAddForm.builder()
            .challengeId(1L)
            .image("다이어트 챌린지 추가 이미지주소")
            .currentWeight(65.2f)
            .goalWeight(55.7f)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.addDietChallenge(form, userDetailsBase));
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
            .willReturn(dietChallengeBase);

    //when
    BaseResponseDto<DietChallengeDto> result =
            dietChallengeService.getDietChallenge(1L, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 챌린지 단건 조회를 성공했습니다.", result.getMessage());
    assertEquals(65.2f, result.getData().getCurrentWeight());
    assertEquals(55.7f, result.getData().getGoalWeight());
    assertEquals("test", result.getData().getLoginId());
  }

//  @Test
//  @DisplayName("다이어트 챌린지 전체 조회 성공(관리자)")
//  void getAllDietChallenge() {
//    //given
//    given(dietChallengeRepository.searchAllDietChallengeByChallengeId(anyInt(), anyLong(), anyBoolean()))
//            .willReturn(Page.empty());
//
//    //when
//    int page = 1;
//    BaseResponseDto<PageDto<DietChallengeDto>> result =
//            dietChallengeService.getAllDietChallenge(page, 1L, null, userDetailsBase);
//    //then
//    assertEquals(HttpStatus.OK, result.getStatus());
//    assertEquals("다이어트 챌린지 전체 조회를 성공했습니다.(" + page + "페이지)", result.getMessage());
//    assertEquals(0, result.getData().getSize());
//  }

  @Test
  @DisplayName("다이어트 챌린지 수정 성공")
  void updateDietChallenge() {
    //given
    given(dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(dietChallengeBase);

    DietChallengeUpdateForm form = DietChallengeUpdateForm.builder()
            .challengeId(1L)
            .currentWeight(67.2f)
            .goalWeight(57.7f)
            .build();
    //when
    BaseResponseDto<DietChallengeDto> result =
            dietChallengeService.updateDietChallenge(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 챌린지 수정을 성공했습니다.", result.getMessage());
    assertEquals(67.2f, result.getData().getCurrentWeight());
    assertEquals(57.7f, result.getData().getGoalWeight());
    assertEquals("test", result.getData().getLoginId());
  }

  @Test
  @DisplayName("다이어트 챌린지 수정 실패(챌린지가 시작되고 수정 불가)")
  void updateDietChallengeFailure1() {
    //given
    Challenge challenge = Challenge.builder()
            .id(1L)
            .title("challengeTitle")
            .img("challengeImg")
            .categoryType(CategoryType.DIET)
            .maxParticipant(10L)
            .currentParticipant(1L)
            .description("challengeDescription")
            .minDeposit(10L)
            .maxDeposit(50L)
            .standard("challengeStandard")
            .member(memberBase)
            .startDate(LocalDateTime.parse("2025-04-05T00:00:00"))
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
            .willReturn(dietChallenge);

    DietChallengeUpdateForm form = DietChallengeUpdateForm.builder()
            .challengeId(1L)
            .currentWeight(67.2f)
            .goalWeight(57.7f)
            .build();
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.updateDietChallenge(form, userDetailsBase));
    //then
    assertEquals(CANNOT_UPDATE_AFTER_START_CHALLENGE, exception.getErrorCode());
  }


  @Test
  @DisplayName("다이어트 댓글 추가 성공")
  void addDietComment() {
    //given
    given(dietChallengeRepository.searchDietChallengeByChallengeIdAndLoginId(anyLong(), anyString()))
            .willReturn(dietChallengeBase);

    DietCommentAddForm form = DietCommentAddForm.builder()
            .challengeId(1L)
            .image("추가성공이미지주소")
            .content("추가성공내용")
            .currentWeight(50.2f)
            .build();
    //when
    BaseResponseDto<DietCommentDto> result =
            dietChallengeService.addDietComment(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 댓글 추가를 성공했습니다.", result.getMessage());
    assertEquals("추가성공이미지주소", result.getData().getImage());
    assertEquals("추가성공내용", result.getData().getContent());
    assertEquals("test", result.getData().getLoginId());
    verify(dietCommentRepository, times(1)).save(any());
  }


  @Test
  @DisplayName("다이어트 댓글 단건 조회 성공")
  void getDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(dietCommentBase);

    //when
    BaseResponseDto<DietCommentDto> result =
            dietChallengeService.getDietComment(1L);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 댓글 조회를 성공했습니다.", result.getMessage());
    assertEquals("베이스 이미지주소", result.getData().getImage());
    assertEquals("베이스 내용", result.getData().getContent());
    assertEquals("test", result.getData().getLoginId());
  }

  @Test
  @DisplayName("다이어트 댓글 수정 성공")
  void updateDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(dietCommentBase);

    DietCommentUpdateForm form = DietCommentUpdateForm.builder()
            .commentId(1L)
            .image("수정성공이미지주소")
            .content("수정성공내용")
            .currentWeight(57.2f)
            .build();

    //when
    BaseResponseDto<DietCommentDto> result =
            dietChallengeService.updateDietComment(form, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 댓글 수정을 성공했습니다.", result.getMessage());
    assertEquals("수정성공이미지주소", result.getData().getImage());
    assertEquals("수정성공내용", result.getData().getContent());
    assertEquals("test", result.getData().getLoginId());
  }

  @Test
  @DisplayName("다이어트 댓글 수정 실패(회원 본인이 작성한 댓글이 아님)")
  void updateDietCommentFailure() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(DietComment.builder()
                    .id(1L)
                    .dietChallenge(dietChallengeBase)
                    .member(memberBad)
                    .image("베이스 이미지주소")
                    .content("베이스 내용")
                    .build());

    DietCommentUpdateForm form = DietCommentUpdateForm.builder()
            .commentId(1L)
            .image("수정성공이미지주소")
            .content("수정성공내용")
            .currentWeight(57.2f)
            .build();

    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.updateDietComment(form, userDetailsBase));

    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }

  @Test
  @DisplayName("다이어트 댓글 삭제 성공")
  void deleteDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(dietCommentBase);

    //when
    BaseResponseDto<DietCommentDto> result =
            dietChallengeService.deleteDietComment(1L, userDetailsBase);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 댓글 삭제를 성공했습니다.", result.getMessage());
    assertEquals("베이스 이미지주소", result.getData().getImage());
    assertEquals("베이스 내용", result.getData().getContent());
    assertEquals("test", result.getData().getLoginId());
    verify(dietCommentRepository, times(1)).delete(any());
  }


  @Test
  @DisplayName("다이어트 댓글 삭제 실패(회원 본인이 작성한 댓글이 아님)")
  void deleteDietCommentFailure() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(DietComment.builder()
                    .id(1L)
                    .dietChallenge(dietChallengeBase)
                    .member(memberBad)
                    .image("베이스 이미지주소")
                    .content("베이스 내용")
                    .build());
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.deleteDietComment(1L, userDetailsBase));
    //then
    assertEquals(NOT_OWNER_OF_COMMENT, exception.getErrorCode());
  }


  @Test
  @DisplayName("다이어트 댓글 삭제 성공(관리자)")
  void adminDeleteDietComment() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(dietCommentBase);

    UserDetailsImpl userDetails = new UserDetailsImpl(Member.builder()
            .id(1L)
            .memberType(MemberType.ADMIN)
            .build());

    //when
    BaseResponseDto<DietCommentDto> result =
            dietChallengeService.adminDeleteDietComment(1L, userDetails);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("관리자 권한으로 다이어트 댓글 삭제를 성공했습니다.", result.getMessage());
    assertEquals("베이스 이미지주소", result.getData().getImage());
    assertEquals("베이스 내용", result.getData().getContent());
    assertEquals("test", result.getData().getLoginId());
    verify(dietCommentRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("다이어트 댓글 삭제 실패(관리자)(관리자가 아님)")
  void adminDeleteDietCommentFailure() {
    //given
    given(dietCommentRepository.searchDietCommentById(anyLong()))
            .willReturn(dietCommentBase);
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.adminDeleteDietComment(1L, userDetailsBase));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

  @Test
  @DisplayName("다이어트 챌린지 전체 조회 성공(관리자)")
  void getAllDietChallenge() {
    //given
    Pageable pageable = PageRequest.of(0, 20);
    given(dietChallengeRepository.searchAllDietChallengeByChallengeId(anyInt(), anyLong(), anyBoolean()))
            .willReturn(new PageImpl<>(List.of(DietChallengeDto.fromWithoutComments(dietChallengeBase)), pageable, 1));
    UserDetailsImpl userDetails = new UserDetailsImpl(Member.builder()
            .id(1L)
            .memberType(MemberType.ADMIN)
            .build());
    int page = 1;
    //when
    BaseResponseDto<PageDto<DietChallengeDto>> result =
            dietChallengeService.getAllDietChallenge(page, 1L, true, userDetails);
    //then
    assertEquals(HttpStatus.OK, result.getStatus());
    assertEquals("다이어트 챌린지 전체 조회를 성공했습니다.(" + page + "페이지)", result.getMessage());
  }

  @Test
  @DisplayName("다이어트 챌린지 전체 조회 실패(관리자가 아님)")
  void getAllDietChallengeFailure() {
    //given
    int page = 1;
    //when
    CustomException exception = assertThrows(CustomException.class, () ->
            dietChallengeService.getAllDietChallenge(page, 1L, true, userDetailsBase));
    //then
    assertEquals(NOT_MEMBER_TYPE_ADMIN, exception.getErrorCode());
  }

}