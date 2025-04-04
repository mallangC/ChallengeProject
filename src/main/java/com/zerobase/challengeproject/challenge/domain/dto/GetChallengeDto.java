package com.zerobase.challengeproject.challenge.domain.dto;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class GetChallengeDto {
    private Long id;
    private Long memberId;
    private String title;
    private String img;
    private CategoryType categoryType;
    private Integer participant;
    private String description;
    private Integer min_deposit;
    private Integer max_deposit;
    private String standard;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public GetChallengeDto(Challenge challenge) {
        this.memberId = challenge.getMember().getId();
        this.id = challenge.getId();
        this.updateAt = challenge.getUpdateAt();
        this.createAt = challenge.getCreateAt();
        this.endDate = challenge.getEndDate();
        this.startDate = challenge.getStartDate();
        this.standard = challenge.getStandard();
        this.max_deposit = challenge.getMaxDeposit();
        this.min_deposit = challenge.getMinDeposit();
        this.description = challenge.getDescription();
        this.participant = challenge.getParticipant();
        this.categoryType = challenge.getCategoryType();
        this.img = challenge.getImg();
        this.title = challenge.getTitle();
    }
}
