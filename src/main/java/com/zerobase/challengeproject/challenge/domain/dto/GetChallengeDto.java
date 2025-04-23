package com.zerobase.challengeproject.challenge.domain.dto;

import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.comment.domain.dto.CoteCommentDto;
import com.zerobase.challengeproject.comment.domain.dto.DietCommentDto;
import com.zerobase.challengeproject.comment.domain.dto.WaterCommentDto;
import com.zerobase.challengeproject.type.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class GetChallengeDto {
    private Long id;
    private Long memberId;
    private String title;
    private String imgUrl;
    private CategoryType categoryType;
    private Long maxParticipant;
    private Long currentParticipant;
    private String description;
    private Long minDeposit;
    private Long maxDeposit;
    private String standard;
    private List<CoteCommentDto> coteComments;
    private List<DietCommentDto> dietComments;
    private List<WaterCommentDto> waterComments;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GetChallengeDto(Challenge challenge) {
        this.memberId = challenge.getMember().getId();
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
        this.currentParticipant = challenge.getCurrentParticipant();
        this.categoryType = challenge.getCategoryType();
        this.imgUrl = challenge.getImageUrl();
        this.title = challenge.getTitle();

    }

}
