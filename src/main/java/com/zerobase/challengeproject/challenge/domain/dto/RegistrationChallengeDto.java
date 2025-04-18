package com.zerobase.challengeproject.challenge.domain.dto;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationChallengeDto {
    private Long challengeId;
    private String title;
    private String categoryType;
    private Long myDeposit;

    public RegistrationChallengeDto(Challenge challenge, Long memberDeposit) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.categoryType = challenge.getCategoryType().toString();
        this.myDeposit = memberDeposit;
    }
}
