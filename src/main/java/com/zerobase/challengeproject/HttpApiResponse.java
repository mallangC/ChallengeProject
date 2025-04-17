package com.zerobase.challengeproject;

import org.springframework.http.HttpStatus;

public record HttpApiResponse<T>(T data, String message, HttpStatus status) {
}