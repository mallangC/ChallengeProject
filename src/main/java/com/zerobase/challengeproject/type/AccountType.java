package com.zerobase.challengeproject.type;

import lombok.Getter;

@Getter
public enum AccountType {
    CHARGE,
    REFUND,
    DEPOSIT,
    DEPOSIT_BACK
}