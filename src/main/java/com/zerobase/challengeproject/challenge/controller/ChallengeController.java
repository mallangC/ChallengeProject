package com.zerobase.challengeproject.challenge.controller;


import com.zerobase.challengeproject.PagenatedResponseDto;
import com.zerobase.challengeproject.challenge.domain.dto.*;
import com.zerobase.challengeproject.challenge.domain.request.CreateChallengeRequest;
import com.zerobase.challengeproject.challenge.domain.request.RegistrationChallengeRequest;
import com.zerobase.challengeproject.challenge.domain.request.UpdateChallengeRequest;
import com.zerobase.challengeproject.challenge.service.ChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@AllArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    /**
     * 챌린지 전체 조회
     */
    @GetMapping
    public ResponseEntity<PagenatedResponseDto<GetChallengeDto>> getAllChallenges(Pageable pageable) {
        List<GetChallengeDto> challengeList = challengeService.getAllChallenges(pageable);

        Page<GetChallengeDto> challengePage = new PageImpl<>(challengeList, pageable, challengeList.size());
        return ResponseEntity.ok(PagenatedResponseDto.from(challengePage, "전체 챌린지 조회 성공", HttpStatus.OK));
    }


    /**
     * 챌린지 상세조회
     */
    @GetMapping("/{challengeId}")
    public ResponseEntity<HttpApiResponse<GetChallengeDto>> getChallengeDetail(@PathVariable Long challengeId) {
        GetChallengeDto challengeDto = challengeService.getChallengeDetail(challengeId);
        return ResponseEntity.ok(new HttpApiResponse<>(challengeDto, "챌린지 상세정보 조회 성공", HttpStatus.OK));
    }

    /**
     * 사용자가 생성한 챌린지 조회
     */
    @GetMapping("/my-challenge")
    public ResponseEntity<PagenatedResponseDto<GetChallengeDto>> getChallengesMadeByUser(Pageable pageable,
                                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long memberId = userDetails.getMember().getId();
        Page<GetChallengeDto> usersChallengePages = challengeService.getChallengesMadeByUser(pageable, memberId);

        return ResponseEntity.ok(PagenatedResponseDto.from(usersChallengePages, "유저가 생성한 챌린지 조회 성공", HttpStatus.OK));

    }

    /**
     * 사용자가 참여중인 챌린지 조회
     */
    @GetMapping("/participation")
    public ResponseEntity<PagenatedResponseDto<ParticipationChallengeDto>> getOngoingChallenges(Pageable pageable,
                                                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        Page<ParticipationChallengeDto> challengeDtos = challengeService.getOngoingChallenges(pageable, memberId);

        return ResponseEntity.ok(PagenatedResponseDto.from(challengeDtos, "유저가 참여중인 챌린지 조회 성공", HttpStatus.OK)
        );
    }

    /**
     * 사용자가 챌린지에 참여
     */
    @PostMapping("/registrations/{challengeId}")
    public ResponseEntity<HttpApiResponse<RegistrationChallengeDto>> registerChallenge(
            @PathVariable Long challengeId,
            @Valid @RequestBody RegistrationChallengeRequest registrationChallengeRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long memberId = userDetails.getMember().getId();
        RegistrationChallengeDto registrationChallengeDto = challengeService.registerChallenge(challengeId, registrationChallengeRequest, memberId);

        return ResponseEntity.ok(new HttpApiResponse<>(registrationChallengeDto, "챌린지 참여에 성공했습니다.", HttpStatus.OK)
        );
    }

    /**
     * 참여자가 챌린지참여 취소
     *
     */
    @DeleteMapping("/cancel/{challengeId}")
    public ResponseEntity<HttpApiResponse<Void>> cancelChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long memberId = userDetails.getMember().getId();
        challengeService.cancelChallenge(challengeId, memberId);
        return ResponseEntity.ok(new HttpApiResponse<>(null, "챌린지 참여가 취소되었습니다.", HttpStatus.OK)
        );
    }

    /**
     * 챌린지 생성
     */
    @PostMapping
    public ResponseEntity<HttpApiResponse<GetChallengeDto>> createChallenge(
            @Valid @RequestBody CreateChallengeRequest form,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long memberId = userDetails.getMember().getId();
        GetChallengeDto challengeDto = challengeService.createChallenge(form, memberId);

        return ResponseEntity.ok(new HttpApiResponse<>(challengeDto, "챌린지 생성 성공", HttpStatus.OK)
        );
    }

    /**
     * 챌린지 수정
     */
    @PutMapping("/{challengeId}")
    public ResponseEntity<HttpApiResponse<GetChallengeDto>> updateChallenge(
            @PathVariable Long challengeId,
            @Valid @RequestBody UpdateChallengeRequest form,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        GetChallengeDto updatedChallenge = challengeService.updateChallenge(challengeId, form);
        return ResponseEntity.ok(new HttpApiResponse<GetChallengeDto>(updatedChallenge, "챌린지 수정 성공", HttpStatus.OK)
        );
    }

    /**
     * 챌린지 삭제
     */
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<HttpApiResponse<Void>> deleteChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        challengeService.deleteChallenge(challengeId, userDetails.getMember());

        return ResponseEntity.ok(new HttpApiResponse<>(null, "챌린지 삭제 성공", HttpStatus.OK)
        );
    }

    /**
     * 챌린지 환급
     */
    @PostMapping("/{challengeId}")
    public ResponseEntity<HttpApiResponse<DepositBackDto>> challengeDepositBack(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        DepositBackDto dto = challengeService.challengeDepositBack(challengeId, userDetails.getMember());

        return ResponseEntity.ok(new HttpApiResponse<>(dto, "챌린지 환급 성공", HttpStatus.OK));
    }
}
