package com.zerobase.challengeproject.member.contoller;

import com.zerobase.challengeproject.HttpApiPageResponse;
import com.zerobase.challengeproject.member.domain.form.BlackListRegisterForm;
import com.zerobase.challengeproject.member.service.AdminBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminBlackListController {

    private final AdminBlacklistService adminBlacklistService;

    /**
     * 관리자가 회원을 블랙리스트로 등록하는 컨트롤러 메서드
     *
     * @param form 블랙리스트로 등록하려는 회원 로그인 아이디
     * @return 블랙리스트 맴버 아이디
     */
    @PostMapping("/blacklist")
    public ResponseEntity<HttpApiPageResponse> registerBlacklist(
            @RequestBody BlackListRegisterForm form) {
        return ResponseEntity.ok(
                new HttpApiPageResponse(
                        adminBlacklistService.registerBlacklist(form),
                        form.getBlacklistUserLoginId() + "블랙리스트 등록 성공",
                        HttpStatus.OK
                )
        );
    }
    /**
     * 관리자가 회원을 블랙리스트로 해제하는 컨트롤러 메서드
     * @param form 블랙리스트로 해제하려는 회원 로그인 아이디
     * @return 블랙리스트가 헤제된 맴버 아이디
     */
    @PatchMapping("/blacklist")
    public ResponseEntity<HttpApiPageResponse> unRegisterBlacklist(
            @RequestBody BlackListRegisterForm form
    ){
        return ResponseEntity.ok(
                new HttpApiPageResponse(
                        adminBlacklistService.unRegisterBlacklist(form),
                        form.getBlacklistUserLoginId() + "블랙리스트 해제 성공",
                        HttpStatus.OK
                )
        );
    }
}
