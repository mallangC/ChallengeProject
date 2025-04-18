package com.zerobase.challengeproject.challenge.domain.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepositBackDto {

    private Long challengeId;
    private Long depositBackAmount;
    private Long currentDeposit;



    public void setDepositBackDto(Long challengeId, Long depositBackAmount, Long currentDeposit) {
        this.challengeId = challengeId;
        this.depositBackAmount = depositBackAmount;
        // 반환받은 보증금 + 현재계좌금액
        this.currentDeposit = currentDeposit + depositBackAmount;
    }
}
