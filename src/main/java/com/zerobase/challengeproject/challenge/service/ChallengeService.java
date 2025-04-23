package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.challenge.domain.dto.DepositBackDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.domain.dto.ParticipationChallengeDto;
import com.zerobase.challengeproject.challenge.domain.dto.RegistrationChallengeDto;
import com.zerobase.challengeproject.challenge.domain.request.CreateChallengeRequest;
import com.zerobase.challengeproject.challenge.domain.request.RegistrationChallengeRequest;
import com.zerobase.challengeproject.challenge.domain.request.UpdateChallengeRequest;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.entity.MemberChallenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.challenge.repository.MemberChallengeRepository;
import com.zerobase.challengeproject.comment.domain.dto.CoteCommentDto;
import com.zerobase.challengeproject.comment.domain.dto.DietCommentDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterCommentDto;
import com.zerobase.challengeproject.comment.entity.*;
import com.zerobase.challengeproject.comment.repository.*;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import com.zerobase.challengeproject.type.CategoryType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableCaching
public class ChallengeService {

  private final AccountDetailRepository accountDetailRepository;
  private final ChallengeRepository challengeRepository;
  private final MemberChallengeRepository memberChallengeRepository;
  private final MemberRepository memberRepository;
  private final CoteChallengeRepository coteChallengeRepository;
  private final CoteCommentRepository coteCommentRepository;
  private final DietChallengeRepository dietChallengeRepository;
  private final DietCommentRepository dietCommentRepository;
  private final WaterChallengeRepository waterChallengeRepository;
  private final WaterCommentRepository waterCommentRepository;

    /**
     * 전체 챌린지조회
     */
    @Cacheable(value = "challenges", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public List<GetChallengeDto> getAllChallenges(Pageable pageable) {
        Page<Challenge> allChallenges = challengeRepository.findAll(pageable);
        if (allChallenges.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGES);
        }

        return allChallenges.map(GetChallengeDto::new).getContent();
    }

    /**
     *  특정챌린지 상세 조회
     */
    public GetChallengeDto getChallengeDetail(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        CategoryType categoryType = challenge.getCategoryType();

        GetChallengeDto getChallengeDto = new GetChallengeDto(challenge);

        if (categoryType.equals(CategoryType.COTE)) {
            List<CoteChallenge> coteChallenges = coteChallengeRepository.findAllByChallengeId(challengeId);
            List<Long> coteChallengeIds = coteChallenges.stream()
                    .map(CoteChallenge::getId)
                    .toList();
            List<CoteComment> coteComments = coteCommentRepository.findAllByCoteChallengeIdIn(coteChallengeIds);
            List<CoteCommentDto> commentDtos = coteComments.stream()
                    .map(CoteCommentDto::from)
                    .toList();
            getChallengeDto.setCoteComments(commentDtos);

        } else if (categoryType.equals(CategoryType.WATER)) {
            List<WaterChallenge> waterChallenges = waterChallengeRepository.findAllByChallengeId(challengeId);
            List<Long> waterChallengeIds = waterChallenges.stream()
                    .map(WaterChallenge::getId)
                    .toList();
            List<WaterComment> waterComments = waterCommentRepository.findAllByWaterChallengeIdIn(waterChallengeIds);
            List<WaterCommentDto> commentDtos = waterComments.stream()
                    .map(WaterCommentDto::from)
                    .toList();
            getChallengeDto.setWaterComments(commentDtos);

        } else if (categoryType.equals(CategoryType.DIET)) {
            List<DietChallenge> dietChallenges = dietChallengeRepository.findAllByChallengeId(challengeId);
            List<Long> dietChallengeIds = dietChallenges.stream()
                    .map(DietChallenge::getId)
                    .toList();
            List<DietComment> dietComments = dietCommentRepository.findAllByDietChallengeIdIn(dietChallengeIds);
            List<DietCommentDto> commentDtos = dietComments.stream()
                    .map(DietCommentDto::from)
                    .toList();
            getChallengeDto.setDietComments(commentDtos);
        }

        return getChallengeDto;
    }


    /**
     * 사용자가 만든 챌린지 조회
     */
    public Page<GetChallengeDto> getChallengesMadeByUser(Pageable pageable, Long memberId) {
        Page<Challenge> userChallenges = challengeRepository.findByMemberId(memberId, pageable);

        if (userChallenges.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGES);
        }
        return userChallenges.map(GetChallengeDto::new);
    }

    /**
     * 사용자가 참여중인 챌린지 조회
     */
    public Page<ParticipationChallengeDto> getOngoingChallenges(Pageable pageable, Long memberId) {
        Page<MemberChallenge> memberChallenges = memberChallengeRepository.findByMemberId(memberId, pageable);
        if (memberChallenges.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGES);
        }
        return memberChallenges.map(memberChallenge -> new ParticipationChallengeDto(memberChallenge.getChallenge()));
    }

    /**
     * 사용자가 챌린지에 참여
     */
    public RegistrationChallengeDto registerChallenge(Long challengeId, RegistrationChallengeRequest form, Long memberId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        form.validate(challenge);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        boolean isAlreadyEntered = memberChallengeRepository.existsByChallengeAndMember(challenge, member);
        if (isAlreadyEntered) {
            throw new CustomException(ErrorCode.ALREADY_ENTERED_CHALLENGE);
        }

        MemberChallenge memberChallenge = MemberChallenge.builder()
                .isDepositBack(false)
                .enteredAt(LocalDateTime.now())
                .challenge(challenge)
                .memberDeposit(form.getMemberDeposit())
                .member(member)
                .build();

        // 보증금 차감, 챌린지 인원 업데이트 및 저장
        challenge.registration();
        member.depositAccount(form.getMemberDeposit());
        memberRepository.save(member);
        memberChallengeRepository.save(memberChallenge);
        accountDetailRepository.save(AccountDetail.deposit(member, form.getMemberDeposit()));

        return new RegistrationChallengeDto(challenge, form.getMemberDeposit());
    }

    /**
     * 챌린지 참여 취소
     *
     */
    @Transactional
    public void cancelChallenge(Long challengeId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PARTICIPATION));

        if (LocalDateTime.now().isAfter(challenge.getStartDate())) {
            throw new CustomException(ErrorCode.ALREADY_STARTED_CHALLENGE);
        }

        if (challenge.getCategoryType().equals(CategoryType.DIET)) {
            DietChallenge dietChallenge = dietChallengeRepository
                    .findByChallengeAndMember(challenge, member)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DIET_CHALLENGE));
            dietChallengeRepository.delete(dietChallenge);
        }
        Long refundAmount = memberChallenge.getMemberDeposit();
        AccountDetail refundRecord = AccountDetail.depositBack(member, refundAmount);

        member.chargeAccount(refundAmount);
        memberChallengeRepository.delete(memberChallenge);
        accountDetailRepository.save(refundRecord);
        memberRepository.save(member);
    }

    /**
     * 챌린지 생성
     */
    @Transactional
    public GetChallengeDto createChallenge(CreateChallengeRequest form, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
        form.validate();

        Challenge challenge = new Challenge(form, member);
        MemberChallenge memberChallenge = MemberChallenge.builder()
                .isDepositBack(false)
                .challenge(challenge)
                .memberDeposit(form.getMemberDeposit())
                .enteredAt(LocalDateTime.now())
                .member(member)
                .build();

        AccountDetail depositDetail = AccountDetail.deposit(member, form.getMemberDeposit());
        member.depositAccount(form.getMemberDeposit());

        memberRepository.save(member);
        challengeRepository.save(challenge);
        memberChallengeRepository.save(memberChallenge);
        accountDetailRepository.save(depositDetail);  // <- 기존 코드에서 중복 생성 제거

        return new GetChallengeDto(challenge);
    }

    /**
     * 챌린지 수정
     */
    public GetChallengeDto updateChallenge(Long challengeId, UpdateChallengeRequest form, Long memberId) {


        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        if (!challenge.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_UPDATE_CHALLENGE);
        }

        form.validate();
        challenge.update(form);
        challengeRepository.save(challenge);

        return new GetChallengeDto(challenge);
    }

    /**
     * 챌린지 삭제
     */
    @Transactional
    public void deleteChallenge(Long challengeId, Member loginMember) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        if (!challenge.getMember().getId().equals(loginMember.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_DELETE_CHALLENGE);
        }

        long participantCount = memberChallengeRepository.countByChallengeAndMemberNot(challenge, loginMember);
        if (participantCount > 0) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_HAS_PARTICIPANTS);
        }

        challengeRepository.delete(challenge);
    }

    /**
     * 챌린지 환급
     */
    @Transactional
    public DepositBackDto challengeDepositBack(Long challengeId, Member member) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        validateChallengeEnded(challenge);

        DepositBackDto depositBackDto = new DepositBackDto();

        if (challenge.getCategoryType().equals(CategoryType.COTE)) {
            List<CoteChallenge> coteChallenges = coteChallengeRepository.findAllByChallengeId(challengeId);
            List<Long> coteChallengeIds = coteChallenges.stream()
                    .map(CoteChallenge::getId)
                    .toList();

            List<CoteComment> coteComments = coteCommentRepository
                    .findAllByCoteChallengeIdInAndMemberId(coteChallengeIds, member.getId());

            int matchedCount = 0;
            for (CoteChallenge coteChallenge : coteChallenges) {
                LocalDate coteDate = coteChallenge.getStartAt().toLocalDate();

                boolean hasMatchingComment = coteComments.stream()
                        .anyMatch(comment ->
                                comment.getCoteChallenge().getId().equals(coteChallenge.getId()) &&
                                        comment.getCreateAt().toLocalDate().isEqual(coteDate)
                        );
                if (hasMatchingComment) {
                    matchedCount++;
                }
            }

            // 성공 여부 판단, 매일 올라온 코테챌린지 날짜와 인증 작성 날짜가 모두 매칭되면 인증 성공
            if (matchedCount != coteChallenges.size()) {
                throw new CustomException(ErrorCode.CHALLENGE_FAIL);
            }

            MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

            // 한번 환급받으면 더 이상 환급 불가
            if (memberChallenge.isDepositBack()) {
                throw new CustomException(ErrorCode.ALREADY_REFUNDED);
            }

            Long depositBackAmount = depositBackProcess(memberChallenge, member);
            depositBackDto.setDepositBackDto(challengeId, depositBackAmount, member.getAccount());

        } else if (challenge.getCategoryType().equals(CategoryType.DIET)) {
            DietChallenge dietChallenge = dietChallengeRepository.findByChallengeId(challengeId);
            if (dietChallenge.getCurrentWeight() > dietChallenge.getGoalWeight()) {
                throw new CustomException(ErrorCode.CHALLENGE_FAIL);
            }

            MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

            if (memberChallenge.isDepositBack()) {
                throw new CustomException(ErrorCode.ALREADY_REFUNDED);
            }

            Long depositBackAmount = depositBackProcess(memberChallenge, member);
            depositBackDto.setDepositBackDto(challengeId, depositBackAmount, member.getAccount());

        } else {
            List<WaterChallenge> waterChallenges = waterChallengeRepository.findAllByChallengeIdAndMember(challengeId, member);
            boolean allDaysMetGoal = waterChallenges.stream()
                    .allMatch(wc -> wc.getCurrentIntake() >= wc.getGoalIntake());

            if (!allDaysMetGoal) {
                throw new CustomException(ErrorCode.NOT_MET_CHALLENGE_GOAL);
            }

            MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

            if (memberChallenge.isDepositBack()) {
                throw new CustomException(ErrorCode.ALREADY_REFUNDED);
            }

            Long depositBackAmount = depositBackProcess(memberChallenge, member);
            depositBackDto.setDepositBackDto(challengeId, depositBackAmount, member.getAccount());
        }

        return depositBackDto;
    }
    private void validateChallengeEnded(Challenge challenge) {
        if (LocalDateTime.now().isBefore(challenge.getEndDate())) {
            throw new CustomException(ErrorCode.CHALLENGE_NOT_ENDED);
        }
    }

    private Long depositBackProcess(MemberChallenge memberChallenge, Member member) {
        memberChallenge.setDepositBack(true);
        Long refundAmount = memberChallenge.getMemberDeposit() * 2;
        member.chargeAccount(refundAmount);
        accountDetailRepository.save(AccountDetail.depositBack(member, refundAmount));
        memberRepository.save(member);
        memberChallengeRepository.save(memberChallenge);

        return refundAmount;
    }
}
