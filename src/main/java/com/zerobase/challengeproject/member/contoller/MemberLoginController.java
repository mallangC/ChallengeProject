package com.zerobase.challengeproject.member.contoller;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.member.domain.dto.MemberLoginDto;
import com.zerobase.challengeproject.member.domain.dto.MemberLoginResponse;
import com.zerobase.challengeproject.member.domain.dto.MemberLogoutDto;
import com.zerobase.challengeproject.member.domain.dto.RefreshTokenDto;
import com.zerobase.challengeproject.member.domain.form.MemberLoginForm;
import com.zerobase.challengeproject.member.service.MemberLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberLoginController {

    private final MemberLoginService memberLoginService;

    /**
     * 유저가 로그인을 시도할 때 사용하는 컨트롤러 메서드
     * @param form 유저 아이디, 비밀번호
     * @return 유저아이디, 성공 메세지, HttpCode
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponseDto<MemberLoginDto>> login(@RequestBody MemberLoginForm form) {
        MemberLoginResponse response = memberLoginService.login(form);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getRefreshTokenCookie().toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getAccessToken())
                .body(new BaseResponseDto<>(new MemberLoginDto(response.getMemberId()),"로그인에 성공했습니다", HttpStatus.OK));
    }

    /**
     * 로그인한 유저가 로그 아웃을 시도할 때 사용하는 컨트롤러 메서드
     * @param token 로그인시 발핼한 AccessToken
     * @param refreshToken refreshToken이 들어있는 cookie
     * @return 로그인한 유저의 아이디, 아무정보도 없는 쿠키
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponseDto> logout(@RequestHeader("Authorization") String token,
                                                  @CookieValue(value = "refreshToken", required = false)
                                                  String refreshToken) {

        MemberLogoutDto dto = memberLoginService.logout(token,refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, dto.getResponseCookie().toString())
                .body(new BaseResponseDto<>(dto.getMemberId(), "로그아웃 성공했습니다.", HttpStatus.OK));
    }

    /**
     * refreshToken을 이용한 AccessToken 재발급
     * @param refreshToken 로그인시 생성된 refreshToken이 들어있는 쿠키
     * @return AccessToken
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<BaseResponseDto> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken  ) {
        RefreshTokenDto dto = memberLoginService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + dto.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, dto.getRefreshToken())
                .body(new BaseResponseDto<>(null, "토큰이 재 발행되었습니다", HttpStatus.OK));
    }
}
