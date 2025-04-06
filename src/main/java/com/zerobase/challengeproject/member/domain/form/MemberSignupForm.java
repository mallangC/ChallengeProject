package com.zerobase.challengeproject.member.domain.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSignupForm {
    @NotBlank
    private String loginId;
    @NotBlank
    private String memberName;
    @NotBlank
    private String nickname;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String phoneNum;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
            message = "비밀번호는 8 ~ 15자이며, 최소 하나의 영문자, 숫자, 특수 문자를 포함해야 합니다."
    )
    private String password;
    @NotBlank
    private String confirmPassword;

}
