package com.zerobase.challengeproject.member.domain.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginForm {
    @NotBlank
    private String memberId;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
            message = "비밀번호는 8 ~ 15자이며, 최소 하나의 영문자, 숫자, 특수 문자를 포함해야 합니다."
    )
    private String password;
}
