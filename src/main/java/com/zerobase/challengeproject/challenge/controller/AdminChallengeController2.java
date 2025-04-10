package com.zerobase.challengeproject.challenge.controller;


import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.service.AdminChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/challenge")
@RequiredArgsConstructor
public class AdminChallengeController2 {

    private final AdminChallengeService adminChallengeService;

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<BaseResponseDto<GetChallengeDto>> deleteChallengeByAdmin(@PathVariable Long challengeId, @AuthenticationPrincipal UserDetailsImpl userDetails){

        return adminChallengeService.deleteChallengeByAdmin(challengeId, userDetails);
    }
}
