package org.example.betty.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "해당하는 Game이 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 0000, "잘못된 요청입니다."),
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, 1002, "유효하지 않은 ID Token입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0001, "예상치 못한 서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}