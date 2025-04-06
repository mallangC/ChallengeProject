package com.zerobase.challengeproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원을 찾을 수 없습니다."),
  ALREADY_REGISTER_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),
  ALREADY_REGISTER_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
  ALREADY_VERIFY_EMAIL(HttpStatus.BAD_REQUEST, "이미 인증된 메일입니다."),
  EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
  CONFIRM_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "확인 비밀번호가 일치하지 않습니다."),
  MATCHES_PREVIOUS_PASSWORD(HttpStatus.BAD_REQUEST,"이전 비밀번호와 동일합니다." ),

  INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
  TOKEN_IS_EXPIRATION(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다"),
  TOKEN_IS_INVALID_OR_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 잘 못되었거나 만료되었습니다"),
  TOKEN_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "리프레시 토큰이 제공되지 않았습니다."),

  NOT_ENOUGH_MONEY_TO_REFUND(HttpStatus.BAD_REQUEST, "환불할 금액이 부족합니다."),
  NOT_FOUND_ACCOUNT_DETAIL(HttpStatus.BAD_REQUEST, "계좌 내역을 찾을 수 없습니다."),
  NOT_CHARGE_DETAIL(HttpStatus.BAD_REQUEST, "충전 내역이 아닙니다."),
  ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "이미 환불된 내역입니다."),
  ALREADY_SPENT_MONEY(HttpStatus.BAD_REQUEST, "이미 사용한 금액은 환불할 수 없습니다."),
  ALREADY_DONE(HttpStatus.BAD_REQUEST, "이미 처리된 환불 신청입니다."),
  ALREADY_REFUND_REQUEST(HttpStatus.BAD_REQUEST, "이미 환불 신청한 내역입니다."),
  NOT_FOUND_REFUND(HttpStatus.BAD_REQUEST, "환불 신청을 찾을 수 없습니다."),

  NOT_FOUND_CHALLENGES(HttpStatus.BAD_REQUEST, "챌린지를 찾을 수 없습니다."),
  NOT_FOUND_CHALLENGE(HttpStatus.BAD_REQUEST, "챌린지를 조회할 수 없습니다."),
  ALREADY_ENTERED_CHALLENGE(HttpStatus.BAD_REQUEST, "이미 참여한 챌린지입니다."),
  INVALID_DEPOSIT_AMOUNT(HttpStatus.BAD_REQUEST, "최소보증금이 최대보증금보다 작아야 합니다."),
  INVALID_PARTICIPANT_NUMBER(HttpStatus.BAD_REQUEST, "최소참여인원은 1명이상이어야 합니다."),
  INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작날짜는 종료날짜보다 먼저이어야 합니다."),
  FORBIDDEN_UPDATE_CHALLENGE(HttpStatus.BAD_REQUEST, "챌린지를 생성한 아이디와 다릅니다." ),
  FORBIDDEN_DELETE_CHALLENGE(HttpStatus.BAD_REQUEST, "챌린지를 생성한 유저만 삭제가 가능합니다." ),
  CHALLENGE_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "이미 종료된 챌린지입니다." ),

  NOT_FOUND_COTE_CHALLENGE(HttpStatus.BAD_REQUEST, "코테 챌린지를 찾을 수 없습니다."),
  NOT_FOUND_COTE_COMMENT(HttpStatus.BAD_REQUEST, "코테 댓글을 찾을 수 없습니다."),
  ALREADY_ADDED_THAT_DATE(HttpStatus.BAD_REQUEST, "입력한 날짜에 이미 문제가 추가되어 있습니다."),
  ALREADY_ADDED_COMMENT_TODAY(HttpStatus.BAD_REQUEST, "오늘 이미 인증했습니다."),
  NOT_ENTERED_CHALLENGE(HttpStatus.BAD_REQUEST, "참여하지 않은 챌린지에 댓글을 달 수 없습니다."),
  NOT_OWNER_OF_CHALLENGE(HttpStatus.BAD_REQUEST, "챌린지를 만든 회원이 아닙니다."),
  CANNOT_DELETE_HAVE_COMMENT(HttpStatus.BAD_REQUEST, "인증 댓글이 있으면 코테 챌린지를 삭제할 수 없습니다."),
  NOT_OWNER_OF_COMMENT(HttpStatus.BAD_REQUEST, "댓글을 작성한 회원이 아닙니다."),
  NOT_DEPOSIT_DETAIL(HttpStatus.BAD_REQUEST, "보증금 내역이 아닙니다."),

  NOT_ENOUGH_MONEY(HttpStatus.BAD_REQUEST, "보증금이 부족합니다." ),
  CHALLENGE_FULL(HttpStatus.BAD_REQUEST, "챌린지의 인원이 가득 찼습니다."),
  NOT_FOUND_PARTICIPATION(HttpStatus.BAD_REQUEST, "참여한 유저가 아닙니다." ),
  ALREADY_STARTED_CHALLENGE(HttpStatus.BAD_REQUEST, "이미 시작한 챌린지입니다. 취소가 불가능합니다."),
  CANNOT_DELETE_HAS_PARTICIPANTS(HttpStatus.BAD_REQUEST, "참여중인 유저가 있으므로 삭제가 불가능합니다." );

;
  private final HttpStatus httpStatus;
  private final String message;
}
