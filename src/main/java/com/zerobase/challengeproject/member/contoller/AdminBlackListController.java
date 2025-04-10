package com.zerobase.challengeproject.member.contoller;

import com.zerobase.challengeproject.BaseResponseDto;
import com.zerobase.challengeproject.member.domain.form.BlackListRegisterForm;
import com.zerobase.challengeproject.member.service.AdminBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminBlackListController {

    private final AdminBlacklistService adminBlacklistService;

    @PostMapping("/blacklist")
    public ResponseEntity<BaseResponseDto> registerBlacklist(
            @RequestBody BlackListRegisterForm form) {
        return ResponseEntity.ok(
                new BaseResponseDto(
                        adminBlacklistService.registerBlacklist(form),
                        form.getBlacklistUserLoginId() + "블랙리스트 등록 성공",
                        HttpStatus.OK
                )
        );
    }
}
