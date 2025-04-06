package org.example.betty.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 0000, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0001, "예상치 못한 서버 오류가 발생했습니다."),
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, 1002, "유효하지 않은 ID Token입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 1003, "유효하지 않은 Access Token입니다."),
    NOT_FOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 1004, "Access Token이 존재하지 않습니다."),
    INVALID_SESSION(HttpStatus.UNAUTHORIZED, 1005, "유효하지 않은 Session입니다."),
    NOT_FOUND_WALLET(HttpStatus.NOT_FOUND, 1006, "등록되지 않은 지갑입니다."),
    NOT_FOUND_WALLET_BALANCE(HttpStatus.NOT_FOUND, 1006, "존재하지 않는 토큰 보유 정보입니다."),
    ALREADY_EXIST_WALLET(HttpStatus.NOT_FOUND, 1007, "이미 등록된 지갑입니다."),
    INVALID_WEBSOCKET_SESSION(HttpStatus.UNAUTHORIZED, 1008, "유효하지 않은 Websocket Session입니다."),
    NOT_FOUND_TEAM(HttpStatus.UNAUTHORIZED, 1009, "존재하지 않는 팀 입니다."),
    NOT_FOUND_PROPOSAL(HttpStatus.NOT_FOUND, 1010, "존재하지 않는 안건입니다."),
    NOT_ENOUGH_TOKEN(HttpStatus.NOT_ACCEPTABLE, 1011, "토큰이 부족합니다."),
    ALREADY_EXISTS_WALLET_PROPOSAL(HttpStatus.NOT_FOUND, 1012, "이미 투표에 참여한 안건입니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "해당하는 Game이 없습니다."),
    DISPLAY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3002, "전광판 저장에 실패했습니다."),
    NOT_FOUND_DISPLAY_ACCESS(HttpStatus.INTERNAL_SERVER_ERROR, 3003, "전광판 접근 권한이 없습니다."),
    ALREADY_HAS_DISPLAY_ACCESS(HttpStatus.INTERNAL_SERVER_ERROR, 3003, "이미 전광판 접근 권한이 있습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "해당하는 팬토큰이 없습니다."),
    TRANSACTION_PENDING_EXISTS(HttpStatus.CONFLICT, 4002, "현재 처리 중인 트랜잭션이 존재합니다."),
    TRANSACTION_NOT_CONFIRMED(HttpStatus.NOT_FOUND, 4003, "트랜잭션이 아직 블럭에 포함되지 않았습니다."),
    NOT_FOUND_TRANSACTION(HttpStatus.NOT_FOUND, 4004, "존재하지 않는 트랜잭션입니다."),
    FAILED_TRANSACTION(HttpStatus.NOT_FOUND, 4005, "실패한 트랜잭션입니다."),
    INVALID_TOKEN_TRANSFER(HttpStatus.NOT_FOUND, 4006, "잘못된 토큰 전송 시도입니다."),
    INVALID_TRANSACTION(HttpStatus.NOT_FOUND, 4007, "유효하지 않은 트랜잭션입니다."),
    INSUFFICIENT_TOKEN_AMOUNT(HttpStatus.NOT_FOUND, 4008, "전송된 토큰 금액이 부족합니다."),
    FAILED_CONNECT_WEB3J(HttpStatus.NOT_FOUND, 4005, "WEB3J 연결에 실패했습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}