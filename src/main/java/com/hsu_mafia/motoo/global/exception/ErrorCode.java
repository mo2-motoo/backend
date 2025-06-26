package com.hsu_mafia.motoo.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    /**
     * 비즈니스 에러 (확인 가능한 예외 상황)
     */
    /* 400 BAD_REQUEST : 잘못된 요청 */
    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재 */

    UNEXPECTED_ERROR(HttpStatus.BAD_REQUEST, "0"),
    KIS_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "881"),
    KIS_API_CALL_DENIED(HttpStatus.NOT_FOUND, "882"),
    KIS_API_CALL_TOO_MANY(HttpStatus.INTERNAL_SERVER_ERROR, "883"),
    MAPPING_PROBLEM(HttpStatus.CONFLICT, "993"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
