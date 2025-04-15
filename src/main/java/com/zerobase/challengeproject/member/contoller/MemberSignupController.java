package com.zerobase.challengeproject.member.contoller;

import com.zerobase.challengeproject.HttpApiResponse;
import com.zerobase.challengeproject.member.components.jwt.UserDetailsImpl;
import com.zerobase.challengeproject.member.domain.dto.MemberEmailAuthDto;
import com.zerobase.challengeproject.member.domain.dto.MemberSignupDto;
import com.zerobase.challengeproject.member.domain.form.MemberSignupForm;
import com.zerobase.challengeproject.member.service.MemberSignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberSignupController {

    private final MemberSignupService memberService;

    /**
     * 회원 가입시 사용되는 컨틀로러 메서드 비밀번호는 8 ~ 15자 사이며, 최소 하나의 영문자, 숫자, 특수문자를 포함해야 함.
     * @param memberSignupForm 회원 가입 정보들(memberId,memberName,nickname, email, phoneNum, password, confirmPassword)
     * @return 회원 가입한 유저의 비밀번호를 제외한 정보
     */
    @PostMapping("/sign-up")
    public ResponseEntity<HttpApiResponse<MemberSignupDto>> signUp(@Valid @RequestBody MemberSignupForm memberSignupForm) {
        return ResponseEntity.ok(new HttpApiResponse<>(memberService.signup(memberSignupForm), "회원 가입 요청 성공했습니다.", HttpStatus.OK));
    }

    /**
     * 이메일 인증 위한 컨트롤러 메서드 인증키로 유저를 찾음. 유저가 없을시, 이미 인증한 메일이면 예외를 발생
     * @param emailAuthKey 회원 가입시 생성된 키
     * @return 유저의 아이디, 인증 확인, 인증 날짜
     */
    @GetMapping("/email-auth")
    public ResponseEntity<HttpApiResponse<MemberEmailAuthDto>> verifyEmail(@RequestParam("id") String emailAuthKey){
        return ResponseEntity.ok(new HttpApiResponse<>(memberService.verifyEmail(emailAuthKey),"이메일 인증 완료되었습니다.", HttpStatus.OK));
    }

    /**
     * 회원 탈퇴 요청시 사용되는 컨트롤러 메서드
     * @param userDetails 로그인한 유저의 정보
     * @return 아무 정보도 없는 쿠키, 회원 탈퇴 성공 메세지
     */
    @DeleteMapping("/unregister")
    public ResponseEntity<HttpApiResponse> unregister(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResponseCookie cookie = memberService.unregister(userDetails);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(new HttpApiResponse<>(null,"회원 탈퇴 성공했습니다.", HttpStatus.OK));
    }
}
