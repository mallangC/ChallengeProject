package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.challenge.domain.dto.*;
import com.zerobase.challengeproject.challenge.domain.form.CreateChallengeForm;
import com.zerobase.challengeproject.challenge.domain.form.RegistrationChallengeForm;
import com.zerobase.challengeproject.challenge.domain.form.UpdateChallengeForm;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.entity.MemberChallenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.challenge.repository.MemberChallengeRepository;
import com.zerobase.challengeproject.comment.entity.CoteChallenge;
import com.zerobase.challengeproject.comment.entity.CoteComment;
import com.zerobase.challengeproject.comment.entity.DietChallenge;
import com.zerobase.challengeproject.comment.entity.WaterChallenge;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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

    /**
     * 전체 챌린지조회
     */
    @Cacheable(value = "challenges", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
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
    public ResponseEntity<BaseResponseDto<GetChallengeDto>> getChallengeDetail(@PathVariable Long challengeId){

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() ->  new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
        GetChallengeDto challengeDto = new GetChallengeDto(challenge);
        return ResponseEntity.ok(new BaseResponseDto<GetChallengeDto>(challengeDto,"챌린지 상제정보 조회 성공", HttpStatus.OK));
    }

    /**
     * 사용자가 만든 챌린지 조회
     */
    public ResponseEntity<BaseResponseDto<Page<GetChallengeDto>>> getChallengesMadeByUser(Pageable pageable, UserDetailsImpl userDetails){

        Long memberId = userDetails.getMember().getId();
        Page<Challenge> userChallenges = challengeRepository.findByMemberId(memberId, pageable);
        Page<GetChallengeDto> challengeDtos = userChallenges.map(userChallenge -> new GetChallengeDto(userChallenge));

            if (challengeDtos.isEmpty()) {
                throw new CustomException(ErrorCode.NOT_FOUND_CHALLENGES);
            }
        return ResponseEntity.ok(new BaseResponseDto<Page<GetChallengeDto>>(challengeDtos, "유저가 생성한 챌린지 조회 성공", HttpStatus.OK));
    }

    /**
     * 사용자가 참여중인 챌린지 조회
     */
    public ResponseEntity<BaseResponseDto<Page<ParticipationChallengeDto>>> getOngoingChallenges(Pageable pageable, UserDetailsImpl userDetails) {

        Long memberId = userDetails.getMember().getId();
        Page<MemberChallenge> memberChallenges = memberChallengeRepository.findByMemberId(memberId, pageable);
        Page<ParticipationChallengeDto> challengeDtos = memberChallenges.map(memberChallenge -> new ParticipationChallengeDto(memberChallenge.getChallenge()));
        return ResponseEntity.ok(new BaseResponseDto<Page<ParticipationChallengeDto>>(challengeDtos, "유저가 참여중인 챌린지 조회 성공", HttpStatus.OK));
    }

    /**
     * 사용자가 챌린지에 참여
     */
    public ResponseEntity<BaseResponseDto<EnterChallengeDto>> enterChallenge(Long challengeId, RegistrationChallengeForm form, UserDetailsImpl userDetails){

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        form.validate(challenge);
        Member member = userDetails.getMember();
        EnterChallengeDto enterChallengeDto = new EnterChallengeDto(challenge, form.getMemberDeposit());

        boolean isAlreadyEntered = memberChallengeRepository.existsByChallengeAndMember(challenge, member);
        if (isAlreadyEntered) {
            throw new CustomException(ErrorCode.ALREADY_ENTERED_CHALLENGE);
        }

        MemberChallenge memberChallenge = MemberChallenge.builder()
                .isDepositBack(false)
                .entered_at(LocalDateTime.now())
                .challenge(challenge)
                .memberDeposit(form.getMemberDeposit())
                .member(member)
                .build();

        /**
         * 보증금차감, 챌린지인원업데이트 및 저장
         */
        challenge.registration();
        member.depositAccount(form.getMemberDeposit());
        memberRepository.save(member);
        memberChallengeRepository.save(memberChallenge);
        accountDetailRepository.save(AccountDetail.deposit(member, form.getMemberDeposit()));
        return ResponseEntity.ok(new BaseResponseDto<EnterChallengeDto>(enterChallengeDto,"챌린지 참여에 성공했습니다.", HttpStatus.OK));
    }

    /**
     * 챌린지 참여 취소
     *
     */
    public ResponseEntity<BaseResponseDto<GetChallengeDto>> cancelChallenge(Long challengeId, UserDetailsImpl userDetails){

        Member member = userDetails.getMember();
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
        MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PARTICIPATION));

        if(LocalDateTime.now().isBefore(challenge.getStartDate())){
            throw new CustomException(ErrorCode.ALREADY_STARTED_CHALLENGE);
        }
        
        // 참여취소하면 로그인된 유저의 다이어트 챌린지 삭제
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
        return ResponseEntity.ok(new BaseResponseDto<GetChallengeDto>(null, "챌린지참여가 취소되었습니다.", HttpStatus.OK));
    }

    /**
     * 챌린지 생성
     */
    public ResponseEntity<BaseResponseDto<GetChallengeDto>> createChallenge(@RequestBody CreateChallengeForm form,
                                                                      UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        Challenge challenge = new Challenge(form, member);
        GetChallengeDto challengeDto = new GetChallengeDto(challenge);
        form.validate();

        /**
         * 생성한 사람은 바로 참여
         */
        MemberChallenge memberChallenge = MemberChallenge.builder()
                .isDepositBack(false)
                .challenge(challenge)
                .memberDeposit(form.getMemberDeposit())
                .entered_at(LocalDateTime.now())
                .member(member)
                .build();
        /**
         * 보증금차감 및 저장
         */
        AccountDetail depositDetail = AccountDetail.deposit(member, form.getMemberDeposit());
        member.depositAccount(form.getMemberDeposit());
        memberRepository.save(member);
        challengeRepository.save(challenge);
        memberChallengeRepository.save(memberChallenge);
        accountDetailRepository.save(AccountDetail.deposit(member, form.getMemberDeposit()));

        return ResponseEntity.ok(new BaseResponseDto<GetChallengeDto>(challengeDto, "챌린지 생성 성공", HttpStatus.OK));
    }

    /**
     * 챌린지 수정
     */
    public ResponseEntity<BaseResponseDto<GetChallengeDto>> updateChallenge(@PathVariable Long challengeId, @RequestBody UpdateChallengeForm form, UserDetailsImpl userDetails) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
        form.validate();
        if (!challenge.getMember().getId().equals(userDetails.getMember().getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_UPDATE_CHALLENGE);
        }

        challenge.update(form);
        GetChallengeDto challengeDto = new GetChallengeDto(challenge);
        challengeRepository.save(challenge);

        return ResponseEntity.ok(new BaseResponseDto<GetChallengeDto>(challengeDto, "챌린지 수정 성공", HttpStatus.OK));
    }

    /**
     * 챌린지 삭제
     */
    public ResponseEntity<BaseResponseDto<GetChallengeDto>> deleteChallenge(@PathVariable Long challengeId, UserDetailsImpl userDetails){

        Member member = userDetails.getMember();
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        if (!challenge.getMember().getId().equals(userDetails.getMember().getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_DELETE_CHALLENGE);
        }

        long participantCount = memberChallengeRepository.countByChallengeAndMemberNot(challenge, member);
        if (participantCount > 0) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_HAS_PARTICIPANTS);
        }

        challengeRepository.delete(challenge);
        return ResponseEntity.ok(new BaseResponseDto<GetChallengeDto>(null, "챌린지 삭제 성공", HttpStatus.OK));
    }

    /**
     * 챌린지 환급
     */
    public ResponseEntity<BaseResponseDto<DepositBackDto>> challengeDepositBack(@PathVariable Long challengeId, UserDetailsImpl userDetails){

        Member member = userDetails.getMember();
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
        DepositBackDto depositBackDto = new DepositBackDto();
        validateChallengeEnded(challenge);

        if(challenge.getCategoryType().equals(CategoryType.COTE)){
            List<CoteChallenge> coteChallenges = coteChallengeRepository.findAllByChallengeId(challengeId);
            // coteChallengeId 리스트 추출
            List<Long> coteChallengeIds = coteChallenges.stream()
                    .map(CoteChallenge::getId)
                    .toList();
            // 코멘트 조회 (코테챌린지id 리스트 + 로그인한 사용자 ID 기준)
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
            // 성공 여부 판단, 매일 올라온 코테챌린지날짜와 인증작성날짜가 모두 매칭되면 인증성공
            if(matchedCount != coteChallenges.size()){
                throw new CustomException(ErrorCode.CHALLENGE_FAIL);
            }
            MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
            // 한번 환급받으면 더 이상 환급 불가
            if(memberChallenge.isDepositBack()){
                throw new CustomException(ErrorCode.ALREADY_REFUNDED);
            }
            Long depositBackAmount = depositBackProcess(memberChallenge, member);
            depositBackDto.setDepositBackDto(challengeId, depositBackAmount, member.getAccount());

        }else if (challenge.getCategoryType().equals(CategoryType.DIET)) {
            DietChallenge dietChallenge = dietChallengeRepository.findByChallengeId(challengeId);
            if(dietChallenge.getCurrentWeight() > dietChallenge.getGoalWeight()){
                throw new CustomException(ErrorCode.CHALLENGE_FAIL);
            }
            MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
            if (memberChallenge.isDepositBack()) {
                throw new CustomException(ErrorCode.ALREADY_REFUNDED);
            }
            Long depositBackAmount = depositBackProcess(memberChallenge, member);
            depositBackDto.setDepositBackDto(challengeId, depositBackAmount, member.getAccount());

        }else{

            WaterChallenge waterChallenge = waterChallengeRepository.findByChallengeId(challengeId);
            if(waterChallenge.getCurrentMl() >= waterChallenge.getGoalMl()){
                MemberChallenge memberChallenge = memberChallengeRepository.findByChallengeAndMember(challenge, member)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
                if (memberChallenge.isDepositBack()) {
                    throw new CustomException(ErrorCode.ALREADY_REFUNDED);
                }
                Long depositBackAmount = depositBackProcess(memberChallenge, member);
                depositBackDto.setDepositBackDto(challengeId, depositBackAmount, member.getAccount());
            }
        }

        return ResponseEntity.ok(new BaseResponseDto<DepositBackDto>(depositBackDto, "챌린지 환급 성공", HttpStatus.OK));
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
