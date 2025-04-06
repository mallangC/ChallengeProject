package com.zerobase.challengeproject.challenge.domain.form;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import com.zerobase.challengeproject.type.CategoryType;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UpdateChallengeForm {

    @Size(min = 3, max = 100, message = "제목은 3자 이상, 100자 이하로 입력해야 합니다.")
    private String title;

    private CategoryType categoryType;

    private String standard;

    private String img;

    @Min(value = 1, message = "참여 인원은 최소 1명이상이어야 합니다.")
    @Max(value = 100, message = "참여 인원은 최대 100명이하여야 합니다.")
    private Long maxParticipant;

    @Size(min = 10, max = 500, message = "내용은 10자 이상, 500자 이하로 입력해야 합니다.")
    private String description;

    @Min(value = 0, message = "최소 보증금을 입력해 주세요.")
    private Long minDeposit;

    @Max(value = 1_000_000, message = "최대 보증금은 1,000,000원 이하로 설정해 주세요.")
    private Long maxDeposit;

    @FutureOrPresent(message = "시작날짜를 지정해 주세요.")
    private LocalDateTime startDate;

    @Future(message = "종료날짜를 지정해 주세요.")
    private LocalDateTime endDate;

    public void validate() {
        if (minDeposit != null && maxDeposit != null && minDeposit > maxDeposit) {
            throw new CustomException(ErrorCode.INVALID_DEPOSIT_AMOUNT);
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (maxParticipant != null && maxParticipant <= 0) {
            throw new CustomException(ErrorCode.INVALID_PARTICIPANT_NUMBER);
        }
    }

}
