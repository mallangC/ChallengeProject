package com.zerobase.challengeproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 직접 만든 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(Map.of(
                        "data", ex.getErrorCode().name(),
                        "status", ex.getErrorCode().getHttpStatus(),
                        "message", ex.getMessage()
                ));
    }

    // 인증 실패
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "errorCode", "BAD_CREDENTIALS",
                        "message", "아이디 또는 비밀번호가 올바르지 않습니다."
                ));
    }

    // 그 외 예상하지 못한 예외 처리 (서버 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "errorCode", "INTERNAL_SERVER_ERROR",
                        "message", ex.getMessage()
                ));
    }

}
