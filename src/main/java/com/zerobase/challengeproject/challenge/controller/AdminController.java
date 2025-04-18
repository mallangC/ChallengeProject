package com.zerobase.challengeproject.challenge.controller;


import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.challenge.domain.dto.GetChallengeDto;
import com.zerobase.challengeproject.challenge.service.AdminChallengeService;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/challenge")
@RequiredArgsConstructor
public class AdminController {

    private final AdminChallengeService adminChallengeService;

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<HttpApiResponse<GetChallengeDto>> deleteChallengeByAdmin(@PathVariable Long challengeId, @AuthenticationPrincipal UserDetailsImpl userDetails){

        Member member = userDetails.getMember();
        adminChallengeService.deleteChallengeByAdmin(challengeId, member);
        return ResponseEntity.ok(new HttpApiResponse<>(null, "챌린지 삭제 성공", HttpStatus.OK));
    }
}
