    package com.zerobase.challengeproject.challenge.domain.form;


    import com.zerobase.challengeproject.exception.CustomException;
    import com.zerobase.challengeproject.exception.ErrorCode;
    import com.zerobase.challengeproject.type.CategoryType;
    import jakarta.validation.constraints.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CreateChallengeForm {

        @NotBlank(message = "제목을 작성해 주세요.")
        @Size(min = 3, max = 100, message = "제목은 3자 이상, 100자 이하로 입력해야 합니다.")
        private String title;

        @NotNull(message = "카테고리를 선택해 주세요.")
        private CategoryType categoryType;

        @NotBlank(message = "챌린지 인증기준을 작성해 주세요.")
        private String standard;

        private String img;

        @NotNull
        @Min(value = 1, message = "참여 인원은 최소 1명이상이어야 합니다.")
        @Max(value = 100, message = "참여 인원은 최대 100명이하여야 합니다.")
        private Long maxParticipant;

        @NotBlank(message = "내용은 필수 항목입니다.")
        @Size(min = 10, max = 500, message = "내용은 10자 이상, 500자 이하로 입력해야 합니다.")
        private String description;

        @Min(value = 0, message = "최소 보증금을 입력해 주세요.")
        private Long minDeposit;

        @NotNull
        @Max(value = 1_000_000, message = "최대 보증금은 1,000,000원 이하로 설정해 주세요.")
        private Long maxDeposit;

        @Min(value = 0, message = "보증금은 최소 0원 이상이어야 합니다.")
        @Max(value = 1_000_000, message = "보증금은 최대 1,000,000원 이하로 설정해 주세요.")
        private Long memberDeposit;

        @FutureOrPresent(message = "시작날짜를 지정해 주세요.")
        private LocalDateTime startDate;

        @Future(message = "종료날짜를 지정해 주세요.")
        private LocalDateTime endDate;

        public void validate() {
            if (minDeposit > maxDeposit) {
                throw new CustomException(ErrorCode.INVALID_DEPOSIT_AMOUNT);
            }
            if (startDate.isAfter(endDate)) {
                throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
            }
            if (memberDeposit < minDeposit) {
                throw new CustomException(ErrorCode.INVALID_DEPOSIT_AMOUNT);
            }
            if (maxParticipant <= 0) {
                throw new CustomException(ErrorCode.INVALID_PARTICIPANT_NUMBER);
            }
        }
    }
