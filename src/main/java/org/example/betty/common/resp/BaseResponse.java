package org.example.betty.common.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private int code;           // 0: 성공, 1: 실패
    private String message;     // 메시지
    private T data;             // 실제 데이터

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(0, "요청이 성공적으로 처리되었습니다.", null);
    }

    public static <T> BaseResponse<T> fail(String message) {
        return new BaseResponse<>(1, message, null);
    }
}
