package com.zerobase.challengeproject.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class BlacklistedMemberException extends UsernameNotFoundException {

    private final ErrorCode errorCode;

    public BlacklistedMemberException(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
