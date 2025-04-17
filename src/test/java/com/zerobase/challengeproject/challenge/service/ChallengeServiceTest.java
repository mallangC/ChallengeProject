package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.challenge.domain.dto.BaseResponseDto;
import com.zerobase.challengeproject.challenge.domain.dto.DepositBackDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.domain.form.CreateChallengeForm;
import com.zerobase.challengeproject.challenge.domain.form.UpdateChallengeForm;
import com.zerobase.challengeproject.challenge.entity.MemberChallenge;
import com.zerobase.challengeproject.challenge.repository.MemberChallengeRepository;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
import com.zerobase.challengeproject.comment.repository.CoteChallengeRepository;
import com.zerobase.challengeproject.comment.repository.CoteCommentRepository;
import com.zerobase.challengeproject.comment.repository.DietChallengeRepository;
import com.zerobase.challengeproject.comment.repository.WaterChallengeRepository;
import com.zerobase.challengeproject.type.CategoryType;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private AccountDetailRepository accountDetailRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CoteCommentRepository coteCommentRepository;

    @Mock
    private CoteChallengeRepository coteChallengeRepository;

    @Mock
    private MemberChallengeRepository memberChallengeRepository;

    @Mock
    private DietChallengeRepository dietChallengeRepository;

    @Mock
    private WaterChallengeRepository waterChallengeRepository;


    @Mock
    private UserDetailsImpl userDetails;

    @InjectMocks
    private ChallengeService challengeService;

    private CreateChallengeForm createChallengeForm;
    private UpdateChallengeForm updateChallengeForm;
    private Long challengeId;
    private Member member;
    private Long loginId;

    // Challenge 객체 생성 메서드
    private Challenge createChallenge(Long id, String title) {
        return Challenge.builder()
                .id(id)
                .title(title)
                .member(member)
                .img(null)
                .categoryType(CategoryType.DIET)
                .maxParticipant(10L)
                .description("설명")
                .maxDeposit(3000L)
                .minDeposit(1000L)
                .maxDeposit(5000L)
                .standard("기준")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createAt(LocalDateTime.now())
                .build();
    }

    // ChallengeForm 생성 메서드
    private CreateChallengeForm createChallengeForm() {
        return CreateChallengeForm.builder()
                .title("챌린지 제목")
                .categoryType(CategoryType.COTE)
                .description("설명")
                .standard("기준")
                .memberDeposit(2000L)
                .maxParticipant(10L)
                .minDeposit(1000L)
                .maxDeposit(5000L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();
    }

    private UpdateChallengeForm updateChallengeForm(){
        return UpdateChallengeForm.builder()
                .title("업데이트된 챌린지 제목")
                .categoryType(CategoryType.DIET)
                .standard("업데이트된 인증 기준")
                .img("https://example.com/updated-image.jpg")
                .maxParticipant(20L)
                .description("업데이트된 챌린지 설명")
                .minDeposit(5000L)
                .maxDeposit(10000L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .build();
    }

    @BeforeEach
    void setUp() {

        createChallengeForm = createChallengeForm();
        updateChallengeForm = updateChallengeForm();
        challengeId = 1L;
        loginId = 1L;

        member = Member.builder()
                .id(loginId)  // id를 설정
                .loginId("member123")
                .memberName("user123")
                .account(1000000L)
                .phoneNumber("123-456-7890")
                .email("test@example.com")
                .build();

    }

    @Test
    @DisplayName("챌린지 전체조회 성공")
    void getAllChallenges() {
        // Given
        List<Challenge> challengeList = List.of(
                createChallenge(1L, "챌린지 1"),
                createChallenge(2L, "챌린지 2"),
                createChallenge(3L, "챌린지 3")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Challenge> challengePage = new PageImpl<>(challengeList, pageable, challengeList.size());
        given(challengeRepository.findAll(pageable)).willReturn(challengePage);

        // When
        List<GetChallengeDto> response1 = challengeService.getAllChallenges(pageable);

        // Then
        List<GetChallengeDto> response2 = challengeService.getAllChallenges(pageable);

        // 캐시 사용
        assertThat(response1.get(0).getTitle()).isEqualTo(response2.get(0).getTitle());
        assertThat(response1.get(1).getTitle()).isEqualTo(response2.get(1).getTitle());

        // 데이터 확인
        assertThat(response1).isNotNull();
        assertThat(response1).hasSize(3);
        assertThat(response1.get(0).getTitle()).isEqualTo("챌린지 1");
    }

    @Test
    @DisplayName("챌린지 전체조회 실패")
    void getAllChallengesFailure() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Challenge> emptyPage = Page.empty();

        given(challengeRepository.findAll(pageable)).willReturn(emptyPage);

        // When & Then
        assertThatThrownBy(() -> challengeService.getAllChallenges(pageable))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CHALLENGES.getMessage());
    }

    @Test
    @DisplayName("챌린지 상세조회 성공")
    void getChallengeDetail() {

        // Given
        Challenge challenge = createChallenge(challengeId, "챌린지 제목");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));

        // When
        ResponseEntity<BaseResponseDto<GetChallengeDto>> response = challengeService.getChallengeDetail(challengeId);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("챌린지 제목");
        assertThat(response.getBody().getData().getId()).isEqualTo(1L);
    }
    @Test
    @DisplayName("챌린지 상세조회 실패")
    void getChallengeDetailFailure() {
        // Given
        given(challengeRepository.findById(challengeId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> challengeService.getChallengeDetail(challengeId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CHALLENGE.getMessage());
    }

    @Test
    @DisplayName("챌린지 생성 성공")
    void createChallenge() {
        // Given
        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        CreateChallengeForm form = createChallengeForm();
        Challenge challenge = new Challenge(form, member);
        given(challengeRepository.save(any(Challenge.class))).willReturn(challenge);

        // When
        ResponseEntity<BaseResponseDto<GetChallengeDto>> response = challengeService.createChallenge(form, userDetails);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getTitle()).isEqualTo(challenge.getTitle());
        assertThat(response.getBody().getData().getCategoryType()).isEqualTo(challenge.getCategoryType());
        assertThat(response.getBody().getData().getDescription()).isEqualTo(challenge.getDescription());
        assertThat(response.getBody().getMessage()).isEqualTo("챌린지 생성 성공");
    }

    @Test
    @DisplayName("최소 보증금이 최대 보증금보다 클 경우 예외 발생")
    void createChallengeFailure1() {
        // Given

        createChallengeForm.setMinDeposit(6000L);
        createChallengeForm.setMaxDeposit(5000L);
        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        // When & Then
        assertThatThrownBy(() -> challengeService.createChallenge(createChallengeForm, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦을 경우 예외 발생")
    void createChallengeFailure2() {
        // Given
        createChallengeForm.setStartDate(LocalDateTime.now().plusDays(10));
        createChallengeForm.setEndDate(LocalDateTime.now().plusDays(5));
        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        // When & Then
        assertThatThrownBy(() -> challengeService.createChallenge(createChallengeForm, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DATE_RANGE.getMessage());
    }

    @Test
    @DisplayName("챌린지 수정 성공")
    void updateChallenge() {
        // Given
        Challenge existingChallenge = createChallenge(challengeId, "기존 제목");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(existingChallenge));
        UserDetailsImpl userDetails = new UserDetailsImpl(member);

        // When
        ResponseEntity<BaseResponseDto<GetChallengeDto>> response = challengeService.updateChallenge(challengeId, updateChallengeForm, userDetails);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTitle()).isEqualTo(updateChallengeForm.getTitle());
        assertThat(response.getBody().getData().getDescription()).isEqualTo(updateChallengeForm.getDescription());
    }

    @Test
    @DisplayName("최소 보증금이 최대 보증금보다 클 경우 예외 발생")
    void updateChallengeFailure1() {

        // Given
        Challenge challenge = createChallenge(challengeId, "기존 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        updateChallengeForm.setMinDeposit(6000L);
        updateChallengeForm.setMaxDeposit(5000L);

        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeForm, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("참여인원이 0명일 경우 예외 발생")
    void updateChallengeFailure2() {
        // Given
        Challenge challenge = createChallenge(challengeId, "기존 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        updateChallengeForm.setMaxParticipant(0L);

        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeForm, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_PARTICIPANT_NUMBER.getMessage());
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦을 경우 예외 발생")
    void updateChallengeFailure3() {
        // Given
        Challenge challenge = createChallenge(challengeId, "기존 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        updateChallengeForm.setStartDate(LocalDateTime.now().plusDays(10));
        updateChallengeForm.setEndDate(LocalDateTime.now().minusDays(20));
        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeForm, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DATE_RANGE.getMessage());
    }

    @Test
    @DisplayName("챌린지가 없을 시 예외 발생")
    void updateChallengeFailure4() {
        // Given
        Challenge challenge = createChallenge(challengeId, "기존 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        given(challengeRepository.findById(challengeId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeForm, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CHALLENGE.getMessage());
    }

    @Test
    @DisplayName("챌린지 삭제 성공")
    void deleteChallenge() {
        // Given
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(createChallenge(challengeId,"삭제용 챌린지")));
        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        // When
        ResponseEntity<BaseResponseDto<GetChallengeDto>> response = challengeService.deleteChallenge(challengeId, userDetails);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("챌린지 삭제 성공");
    }


    @Test
    @DisplayName("챌린지 없을 시 예외 발생")
    void deleteChallengeFailure() {
        // Given
        given(challengeRepository.findById(challengeId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> challengeService.deleteChallenge(challengeId, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CHALLENGE.getMessage());
    }

    @Test
    @DisplayName("코테 챌린지 환급 성공")
    void coteChallengeDepositBack() {
        // Given

        when(userDetails.getMember()).thenReturn(member);

        Challenge challenge = new Challenge();
        challenge.setCategoryType(CategoryType.COTE);
        challenge.setEndDate(LocalDateTime.now().minusDays(1));  // 끝난 챌린지

        // CoteChallenge 설정
        CoteChallenge coteChallenge = CoteChallenge.builder()
                .id(1L)
                .startAt(LocalDateTime.now().minusDays(2))
                .build();

        // Mockito 설정
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(coteChallengeRepository.findAllByChallengeId(challengeId)).thenReturn(List.of(coteChallenge));

        // CoteComment 설정: createAt 필드를 설정해야 함
        CoteComment coteComment = CoteComment.builder()
                .coteChallenge(coteChallenge)
                .createAt(LocalDateTime.now().minusDays(2))  // 유효한 LocalDateTime 값 설정
                .build();
        when(coteCommentRepository.findAllByCoteChallengeIdInAndMemberId(anyList(), anyLong()))
                .thenReturn(List.of(coteComment));  // 코멘트 존재

        // MemberChallenge 설정
        MemberChallenge memberChallenge = mock(MemberChallenge.class);
        when(memberChallengeRepository.findByChallengeAndMember(challenge, member))
                .thenReturn(Optional.of(memberChallenge));
        when(memberChallenge.isDepositBack()).thenReturn(false); // 아직 환급 안 됨
        when(memberChallenge.getMemberDeposit()).thenReturn(1000L);

        // When
        ResponseEntity<BaseResponseDto<DepositBackDto>> response = challengeService.challengeDepositBack(challengeId, userDetails);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("챌린지 환급 성공");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getChallengeId()).isEqualTo(challengeId);
        assertThat(response.getBody().getData().getDepositBack()).isEqualTo(2000L);  // 보증금 * 2
    }

    @Test
    @DisplayName("챌린지 종료날짜가 지나지 않았을 때 예외 발생")
    void challengeDepositBackFailure1() {
        // Given
        UserDetailsImpl userDetails = new UserDetailsImpl(member);
        Challenge challenge = new Challenge();
        challenge.setCategoryType(CategoryType.COTE);
        challenge.setEndDate(LocalDateTime.now().plusDays(7));  // 챌린지가 아직 끝나지 않음

        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        // When
        assertThatThrownBy(() -> challengeService.challengeDepositBack(challengeId, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CHALLENGE_NOT_ENDED.getMessage());
    }

    @Test
    @DisplayName("이미 환급 받은 경우 예외 발생")
    void challengeDepositBackFailure2() {
        // Given
        when(userDetails.getMember()).thenReturn(member);

        Challenge challenge = new Challenge();
        challenge.setCategoryType(CategoryType.COTE);
        challenge.setEndDate(LocalDateTime.now().minusDays(1));

        MemberChallenge memberChallenge = mock(MemberChallenge.class);
        when(memberChallengeRepository.findByChallengeAndMember(challenge, member))
                .thenReturn(Optional.of(memberChallenge));
        when(memberChallenge.isDepositBack()).thenReturn(true);  // 이미 환급됨

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));

        // When
        assertThatThrownBy(() -> challengeService.challengeDepositBack(challengeId, userDetails))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_REFUNDED.getMessage());
    }
    @Test
    @DisplayName("다이어트 챌린지 환급 성공")
    void dietChallengeDepositBack() {
        // Given
        when(userDetails.getMember()).thenReturn(member);

        Challenge challenge = new Challenge();
        challenge.setCategoryType(CategoryType.DIET);
        challenge.setEndDate(LocalDateTime.now().minusDays(1));

        DietChallenge dietChallenge = DietChallenge.builder()
                .goalWeight(65.2F)
                .currentWeight(64.5F)
                .build();

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(dietChallengeRepository.findByChallengeId(challengeId)).thenReturn(dietChallenge);

        MemberChallenge memberChallenge = mock(MemberChallenge.class);
        when(memberChallengeRepository.findByChallengeAndMember(challenge, member)).thenReturn(Optional.of(memberChallenge));
        when(memberChallenge.isDepositBack()).thenReturn(false);
        when(memberChallenge.getMemberDeposit()).thenReturn(1000L);

        // When
        ResponseEntity<BaseResponseDto<DepositBackDto>> response = challengeService.challengeDepositBack(challengeId, userDetails);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getDepositBack()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("물마시기 챌린지 달성실패시 환급실패")
    void waterChallengeDepositBackFailure() {
        // Given
        when(userDetails.getMember()).thenReturn(member);

        Challenge challenge = new Challenge();
        challenge.setCategoryType(CategoryType.WATER);
        challenge.setEndDate(LocalDateTime.now().minusDays(1));

        WaterChallenge waterChallenge = WaterChallenge.builder()
                .goalMl(1500)
                .currentMl(800)
                .build();
        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(waterChallengeRepository.findByChallengeId(challengeId)).thenReturn(waterChallenge);

        // When
        ResponseEntity<BaseResponseDto<DepositBackDto>> response = challengeService.challengeDepositBack(challengeId, userDetails);

        // Then
        assertThat(response.getBody().getData().getDepositBack()).isNull();
    }

}
