package com.zerobase.challengeproject.challenge.domain.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepositBackDto {

    private Long challengeId;
    private Long depositBack;
    private Long currentDeposit;



    public void setDepositBackDto(Long challengeId, Long depositBack, Long currentDeposit) {
        this.challengeId = challengeId;
        this.depositBack = depositBack;
        // 반환받은 보증금 + 현재계좌금액
        this.currentDeposit = currentDeposit + depositBack;
    }
}
