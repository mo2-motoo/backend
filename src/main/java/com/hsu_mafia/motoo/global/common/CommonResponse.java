package com.hsu_mafia.motoo.global.common;

import lombok.Getter;

@Getter
public final class CommonResponse<T> {

    // 성공 여부를 나타내는 boolean 변수
    private final boolean success;
    // 클라이언트가 요청한 데이터. 요청 실패했을 경우 null
    private final T data;
    // 실패했을 경우 어떤 에러인지 클라이언트에게 전달. 요청 성공했을 경우 null
    private final Error error;

    public CommonResponse(boolean success, T data, Error error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // API 요청 성공 시 컨트롤러에서 success 함수에 적절한 data 를 넣어 리턴
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null);
    }


    // API 요청 실패 시 fail 함수에 ErrorCode 대신 코드와 메세지를 직접 넣어 리턴
    public static <T> CommonResponse<T> fail(String code, String message) {
        return new CommonResponse<>(false, null, new Error(code, message));
    }

    // Response 에 넣기 위한 Error 클래스
    @Getter
    static class Error {

        // 에러 타입
        private final String code;
        // 에러 메시지
        private final String message;

        public Error(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}