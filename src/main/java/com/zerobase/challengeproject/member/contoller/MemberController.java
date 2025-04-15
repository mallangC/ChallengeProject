package com.zerobase.challengeproject.member.contoller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberProfileDto;
import com.zerobase.challengeproject.member.domain.form.ChangePasswordForm;
import com.zerobase.challengeproject.member.domain.form.MemberProfileFrom;
import com.zerobase.challengeproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 유저가 회원 정보를 조회할 때 사용되는 컨트롤러 메서드
     * @param userDetails 로그인한 유저의 정보
     * @return 유저 정보(로그인아이디, 이름, 닉네임, 전화번호, 이메일주소)
     */
    @GetMapping("/profile")
    public ResponseEntity<HttpApiResponse<MemberProfileDto>> getProfile (
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(
                new HttpApiResponse<>(
                        memberService.getProfile(userDetails),
                        "회원 정보 불러오기를 성공했습니다",
                        HttpStatus.OK
                        ));
    }

    /**
     * 회원 정보를 수정하는 컨트롤러 메서드
     *
     * @param form 수정 정보 form(nickname, phoneNum)
     * @param userDetails 로그인한 유저의 정보
     * @return 수정한 유저의 정보
     */
    @PatchMapping("/profile")
    public ResponseEntity<HttpApiResponse<MemberProfileDto>> updateProfile (
            @RequestBody MemberProfileFrom form,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ) {
        return ResponseEntity.ok(
                new HttpApiResponse<>(
                        memberService.updateProfile(userDetails, form),
                        "회원 정보 수정 성공했습니다",
                        HttpStatus.OK
                )
        );
    }

    /**
     * 유저의 비밀번호를 수정하는 메서드
     * @param form 현재비밀번호, 새비밀번호, 새비밀번호확인
     * @param userDetails 로그인한 유저의 정보
     * @return 수정 성공한 유저의 아이디
     */
    @PatchMapping("/change-password")
    public ResponseEntity<HttpApiResponse<String>> changePassword (
            @RequestBody ChangePasswordForm form,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
                new HttpApiResponse<>(
                        memberService.changePassword(userDetails, form),
                        "비밀 번호 수정 성공했습니다",
                        HttpStatus.OK
                )
        );
    }
}
