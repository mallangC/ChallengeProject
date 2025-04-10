package com.zerobase.challengeproject.member.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackListRegisterForm {
    private String blacklistUserLoginId;
}
