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
    private Long minDeposit;
    private Long maxDeposit;
    private String standard;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ParticipationChallengeDto(Challenge challenge) {
        this.id = challenge.getId();
        this.updatedAt = challenge.getUpdatedAt();
        this.createdAt = challenge.getCreatedAt();
        this.endDate = challenge.getEndDate();
        this.startDate = challenge.getStartDate();
        this.standard = challenge.getStandard();
        this.maxDeposit = challenge.getMaxDeposit();
        this.minDeposit = challenge.getMinDeposit();
        this.description = challenge.getDescription();
        this.maxParticipant = challenge.getMaxParticipant();
        this.categoryType = challenge.getCategoryType();
        this.img = challenge.getImageUrl();
        this.title = challenge.getTitle();
    }
}
