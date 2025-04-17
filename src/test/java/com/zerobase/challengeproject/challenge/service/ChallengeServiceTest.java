package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.challenge.domain.dto.DepositBackDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.domain.request.CreateChallengeRequest;
import com.zerobase.challengeproject.challenge.domain.request.UpdateChallengeRequest;
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
import com.zerobase.challengeproject.type.MemberType;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


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

    private CreateChallengeRequest createChallengeRequest;
    private UpdateChallengeRequest updateChallengeRequest;
    private Long challengeId;
    private Member member;
    private Long id;

    // Challenge 객체 생성 메서드
    private Challenge createChallenge(Long id, String title) {
        return Challenge.builder()
                .id(id)
                .title(title)
                .member(member)
                .imageUrl(null)
                .categoryType(CategoryType.DIET)
                .maxParticipant(10L)
                .description("설명")
                .maxDeposit(3000L)
                .minDeposit(1000L)
                .maxDeposit(5000L)
                .standard("기준")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ChallengeForm 생성 메서드
    private CreateChallengeRequest createChallengeRequest() {
        return CreateChallengeRequest.builder()
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

    private UpdateChallengeRequest updateChallengeRequest(){
        return UpdateChallengeRequest.builder()
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
        createChallengeRequest = createChallengeRequest();
        updateChallengeRequest = updateChallengeRequest();
        challengeId = 1L;
        id = 1L;

        member = Member.builder()
                .id(id)
                .loginId("member123")
                .memberName("user123")
                .account(1000000L)
                .phoneNum("123-456-7890")
                .email("test@example.com")
                .memberType(MemberType.USER) // 👈 이 부분 추가!
                .build();
        UserDetailsImpl mockUserDetails = new UserDetailsImpl(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
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
        GetChallengeDto challengeDto = challengeService.getChallengeDetail(challengeId);

        // Then
        assertThat(challengeDto.getTitle()).isEqualTo("챌린지 제목");
        assertThat(challengeDto.getId()).isEqualTo(1L);
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
        Long memberId = member.getId();
        CreateChallengeRequest form = createChallengeRequest();
        Challenge challenge = new Challenge(form, member);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(challengeRepository.save(any(Challenge.class))).willReturn(challenge);
        AccountDetail savedDetail = mock(AccountDetail.class);
        MemberChallenge savedMemberChallenge = mock(MemberChallenge.class);

        given(accountDetailRepository.save(any(AccountDetail.class))).willReturn(savedDetail);
        given(memberChallengeRepository.save(any(MemberChallenge.class))).willReturn(savedMemberChallenge);

        // When
        GetChallengeDto result = challengeService.createChallenge(form, memberId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(challenge.getTitle());
        assertThat(result.getCategoryType()).isEqualTo(challenge.getCategoryType());
        assertThat(result.getDescription()).isEqualTo(challenge.getDescription());
    }

    @Test
    @DisplayName("최소 보증금이 최대 보증금보다 클 경우 예외 발생")
    void createChallengeFailure1() {
        // Given
        createChallengeRequest.setMinDeposit(6000L);
        createChallengeRequest.setMaxDeposit(5000L);
        Long memberId = member.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // When & Then
        assertThatThrownBy(() -> challengeService.createChallenge(createChallengeRequest, memberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦을 경우 예외 발생")
    void createChallengeFailure2() {
        // Given
        createChallengeRequest.setStartDate(LocalDateTime.now().plusDays(10));
        createChallengeRequest.setEndDate(LocalDateTime.now().plusDays(5));
        Long memberId = member.getId();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // When & Then
        assertThatThrownBy(() -> challengeService.createChallenge(createChallengeRequest, memberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DATE_RANGE.getMessage());
    }

    @Test
    @DisplayName("챌린지 수정 성공")
    void updateChallengeSuccess() {
        // Given
        Challenge existingChallenge = createChallenge(challengeId, "기존 제목");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(existingChallenge));

        // When
        GetChallengeDto response = challengeService.updateChallenge(challengeId, updateChallengeRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(updateChallengeRequest.getTitle());
        assertThat(response.getDescription()).isEqualTo(updateChallengeRequest.getDescription());
    }

    @Test
    @DisplayName("최소 보증금이 최대 보증금보다 클 경우 예외 발생")
    void updateChallengeFailure1() {

        // Given

        Challenge challenge = createChallenge(challengeId, "기존 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        updateChallengeRequest.setMinDeposit(6000L);
        updateChallengeRequest.setMaxDeposit(5000L);

        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DEPOSIT_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("참여인원이 0명일 경우 예외 발생")
    void updateChallengeFailure2() {
        // Given
        Challenge challenge = createChallenge(challengeId, "기존 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        updateChallengeRequest.setMaxParticipant(0L);

        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_PARTICIPANT_NUMBER.getMessage());
    }

    @Test
    @DisplayName("시작일이 종료일보다 늦을 경우 예외 발생")
    void updateChallengeFailure3() {
        // Given
        Challenge challenge = createChallenge(challengeId, "기존 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        updateChallengeRequest.setStartDate(LocalDateTime.now().plusDays(10));
        updateChallengeRequest.setEndDate(LocalDateTime.now().minusDays(20));

        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_DATE_RANGE.getMessage());
    }

    @Test
    @DisplayName("챌린지가 없을 시 예외 발생")
    void updateChallengeFailure4() {
        // Given
        given(challengeRepository.findById(challengeId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> challengeService.updateChallenge(challengeId, updateChallengeRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND_CHALLENGE.getMessage());
    }


    @Test
    @DisplayName("챌린지 삭제 성공")
    void deleteChallengeSuccess() {
        // Given
        Challenge challenge = createChallenge(challengeId, "삭제용 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        given(memberChallengeRepository.countByChallengeAndMemberNot(challenge, member)).willReturn(0L);

        // When
        challengeService.deleteChallenge(challengeId, member);

        // Then
        verify(challengeRepository).delete(challenge);
    }

    @Test
    @DisplayName("삭제 권한 없는 사용자 예외 발생")
    void deleteChallengeFailure1() {
        // Given
        Challenge challenge = createChallenge(challengeId, "삭제용 챌린지");

        Member anotherMember = Member.builder()
                .id(2L)
                .loginId("anotherUser")
                .email("test@example.com")
                .build();

        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));

        // When & Then
        assertThatThrownBy(() -> challengeService.deleteChallenge(challengeId, anotherMember))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.FORBIDDEN_DELETE_CHALLENGE.getMessage());
    }

    @Test
    @DisplayName("참여자가 있는 챌린지 삭제 시 예외 발생")
    void deleteChallengeFailure2() {
        // Given
        Challenge challenge = createChallenge(challengeId, "참여자 있는 챌린지");
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        given(memberChallengeRepository.countByChallengeAndMemberNot(challenge, member)).willReturn(1L);

        // When & Then
        assertThatThrownBy(() -> challengeService.deleteChallenge(challengeId, member))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CANNOT_DELETE_HAS_PARTICIPANTS.getMessage());
    }

    @Test
    @DisplayName("코테 챌린지 환급 성공")
    void coteChallengeDepositBack() {
        // Given
        when(userDetails.getMember()).thenReturn(member);

        Challenge challenge = new Challenge();
        challenge.setCategoryType(CategoryType.COTE);
        challenge.setEndDate(LocalDateTime.now().minusDays(1));  // 끝난 챌린지

        CoteChallenge coteChallenge = CoteChallenge.builder()
                .id(1L)
                .startAt(LocalDateTime.now().minusDays(2))
                .build();

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge));
        when(coteChallengeRepository.findAllByChallengeId(challengeId)).thenReturn(List.of(coteChallenge));
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
        DepositBackDto depositBackDto = challengeService.challengeDepositBack(challengeId, userDetails.getMember());

        // Then
        assertThat(depositBackDto.getDepositBackAmount()).isEqualTo(2000L);  // 보증금 * 2
        assertThat(depositBackDto.getChallengeId()).isEqualTo(challengeId);
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
        assertThatThrownBy(() -> challengeService.challengeDepositBack(challengeId, userDetails.getMember()))
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
        assertThatThrownBy(() -> challengeService.challengeDepositBack(challengeId, userDetails.getMember()))
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
        DepositBackDto depositBackDto = challengeService.challengeDepositBack(challengeId, userDetails.getMember());

        // Then
        assertThat(depositBackDto.getDepositBackAmount()).isEqualTo(2000L);
    }

    @Test
    @DisplayName("물마시기 챌린지 달성실패시 환급실패")
    void waterChallengeDepositBackFailure() {
        // Given
        Long challengeId = 1L;
        Challenge challenge = createChallenge(challengeId, "물챌린지");
        challenge.setCategoryType(CategoryType.WATER);
        challenge.setEndDate(LocalDateTime.now().minusDays(1));  // 종료된 챌린지

        List<WaterChallenge> waterChallenges = List.of(
                WaterChallenge.builder()
                        .goalMl(1500)
                        .currentMl(800)
                        .build()
        );
        given(challengeRepository.findById(challengeId)).willReturn(Optional.of(challenge));
        given(waterChallengeRepository.findAllByChallengeIdAndMember(challengeId, member)).willReturn(waterChallenges);
        given(userDetails.getMember()).willReturn(member);

        // When & Then
        assertThatThrownBy(() -> challengeService.challengeDepositBack(challengeId, userDetails.getMember()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_MET_CHALLENGE_GOAL.getMessage());
    }
}
