package com.zerobase.challengeproject.member.domain.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileFrom {
    @NotBlank
    private String nickname;
    @NotBlank
    private String phoneNum;
}
