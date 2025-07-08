package com.hsu_mafia.motoo.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> CommonResponse<T> success() {
        return CommonResponse.<T>builder()
                .success(true)
                .message("성공")
                .build();
    }

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message("성공")
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> fail(String code, String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}