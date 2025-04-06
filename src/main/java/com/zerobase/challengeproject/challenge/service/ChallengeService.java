package com.zerobase.challengeproject.challenge.service;


import com.zerobase.challengeproject.account.entity.AccountDetail;
import com.zerobase.challengeproject.account.repository.AccountDetailRepository;
import com.zerobase.challengeproject.challenge.domain.dto.BaseResponseDto;
import com.zerobase.challengeproject.challenge.domain.dto.EnterChallengeDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.domain.dto.ParticipationChallengeDto;
import com.zerobase.challengeproject.challenge.domain.form.CreateChallengeForm;
import com.zerobase.challengeproject.challenge.domain.form.RegistrationChallengeForm;
import com.zerobase.challengeproject.challenge.domain.form.UpdateChallengeForm;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.challenge.entity.MemberChallenge;
import com.zerobase.challengeproject.challenge.repository.ChallengeRepository;
import com.zerobase.challengeproject.challenge.repository.MemberChallengeRepository;
import com.zerobase.challengeproject.comment.repository.CoteChallengeRepository;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import com.zerobase.challengeproject.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

  private final ChallengeRepository challengeRepository;
  private final MemberChallengeRepository memberChallengeRepository;
  private final CoteChallengeRepository coteChallengeRepository;
  private final AccountDetailRepository accountDetailRepository;
  private final MemberRepository memberRepository;

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

        /**
         * 이미 참여한 경우 예외
         */
        boolean isAlreadyEntered = memberChallengeRepository.existsByChallengeAndMember(challenge, member);
        if (isAlreadyEntered) {
            throw new CustomException(ErrorCode.ALREADY_ENTERED_CHALLENGE);
        }

        MemberChallenge memberChallenge = MemberChallenge.builder()
                .entered_at(LocalDateTime.now())
                .challenge(challenge)
                .memberDeposit(form.getMemberDeposit())
                .member(member)
                .build();

        /**
         * 보증금차감, 챌린지인원업데이트 및 저장
         */
        member.chargeAccount(100000L);
        member.depositAccount(form.getMemberDeposit());
        challenge.registration();
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

        // 시작전에만 취소가능
        if (LocalDateTime.now().isBefore(challenge.getStartDate())) {
            memberChallengeRepository.delete(memberChallenge);
            Long refundAmount = memberChallenge.getMemberDeposit();
            AccountDetail refundRecord = AccountDetail.depositBack(member, refundAmount);
            member.chargeAccount(refundAmount);
            accountDetailRepository.save(refundRecord);
            memberRepository.save(member);
            return ResponseEntity.ok(new BaseResponseDto<GetChallengeDto>(null, "챌린지참여가 취소되었습니다.", HttpStatus.OK));
        }else{
            throw new CustomException(ErrorCode.ALREADY_STARTED_CHALLENGE);
        }
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
                .challenge(challenge)
                .memberDeposit(form.getMemberDeposit())
                .entered_at(LocalDateTime.now())
                .member(member)
                .build();
        /**
         * 보증금차감 및 저장
         */
        member.chargeAccount(100000L);
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
}
