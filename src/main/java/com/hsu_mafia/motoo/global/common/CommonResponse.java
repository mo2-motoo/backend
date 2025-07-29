package com.hsu_mafia.motoo.global.common;

import com.hsu_mafia.motoo.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;

    public static <T> CommonResponse<T> success() {
        return CommonResponse.<T>builder()
                .success(true)
                .message("성공")
                .status(HttpStatus.OK.value())
                .build();
    }

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message("성공")
                .data(data)
                .status(HttpStatus.OK.value())
                .build();
    }

    public static <T> CommonResponse<T> success(String message, T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(HttpStatus.OK.value())
                .build();
    }

    public static <T> CommonResponse<T> fail(String code, String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    /**
     * @deprecated ErrorCode를 사용하는 fail(ErrorCode) 메서드를 사용하세요.
     */
    @Deprecated
    public static <T> CommonResponse<T> fail(HttpStatus status, String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status.value())
                .build();
    }

    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .build();
    }
}