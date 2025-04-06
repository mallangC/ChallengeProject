package com.zerobase.challengeproject.challenge.domain.dto;


import com.zerobase.challengeproject.type.CategoryType;
import com.zerobase.challengeproject.challenge.entity.Challenge;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParticipationChallengeDto {
    private Long id;
    private String title;
    private String img;
    private CategoryType categoryType;
    private Long maxParticipant;
    private String description;
    private Long min_deposit;
    private Long max_deposit;
    private String standard;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public ParticipationChallengeDto(Challenge challenge) {
        this.id = challenge.getId();
        this.updateAt = challenge.getUpdateAt();
        this.createAt = challenge.getCreateAt();
        this.endDate = challenge.getEndDate();
        this.startDate = challenge.getStartDate();
        this.standard = challenge.getStandard();
        this.max_deposit = challenge.getMaxDeposit();
        this.min_deposit = challenge.getMinDeposit();
        this.description = challenge.getDescription();
        this.maxParticipant = challenge.getMaxParticipant();
        this.categoryType = challenge.getCategoryType();
        this.img = challenge.getImg();
        this.title = challenge.getTitle();
    }
}
