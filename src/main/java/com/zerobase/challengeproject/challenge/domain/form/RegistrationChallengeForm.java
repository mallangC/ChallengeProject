package com.zerobase.challengeproject.challenge.domain.form;


import com.zerobase.challengeproject.challenge.entity.Challenge;
import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegistrationChallengeForm {

    @Min(value = 0, message = "나의 보증금을 입력해 주세요. 챌린지의 최소보증금보다 높아야 합니다.")
    private Long memberDeposit;

    public void validate(Challenge challenge) {
        if (memberDeposit != null && challenge.getMinDeposit() != null && memberDeposit < challenge.getMinDeposit() && memberDeposit > challenge.getMaxDeposit()) {
            throw new CustomException(ErrorCode.INVALID_DEPOSIT_AMOUNT);
        }
        if (challenge.getMaxParticipant().equals(challenge.getCurrentParticipant())){
            throw new CustomException(ErrorCode.CHALLENGE_FULL);
        }
        if (LocalDateTime.now().isAfter(challenge.getEndDate())){
            throw new CustomException(ErrorCode.CHALLENGE_ALREADY_ENDED);
        }
    }
}
