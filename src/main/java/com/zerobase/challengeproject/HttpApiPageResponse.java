package com.zerobase.challengeproject;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpApiPageResponse<T> {
  private final T data;
  private final String message;
  private final HttpStatus status;

  public HttpApiPageResponse(T data, String message, HttpStatus status) {
    this.data = data;
    this.message = message;
    this.status = status;
  }
}