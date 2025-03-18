package org.example.betty.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME_NOT_FOUND_001", "해당하는 Game이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}