package com.hsu_mafia.motoo.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 사용자 관련
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // 주식 관련
    STOCK_NOT_FOUND("주식을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // 주문 관련
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ORDER_ACCESS_DENIED("주문에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    ORDER_CANNOT_CANCEL("취소할 수 없는 주문입니다.", HttpStatus.BAD_REQUEST),
    
    // 자금 관련
    INSUFFICIENT_CASH("보유 현금이 부족합니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("보유 주식이 부족합니다.", HttpStatus.BAD_REQUEST),
    
    // 일반
    INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    MAPPING_PROBLEM("매핑 과정에서 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // API 관련
    API_ERROR("외부 API 호출 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    STOCK_API_ERROR("주식 API 호출 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    DATA_COLLECTION_ERROR("데이터 수집 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    SCHEDULER_ERROR("스케줄러 실행 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    
    // 데이터 관련
    DATA_NOT_FOUND("요청한 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DATA_ALREADY_EXISTS("이미 존재하는 데이터입니다.", HttpStatus.CONFLICT),
    INVALID_DATA_FORMAT("잘못된 데이터 형식입니다.", HttpStatus.BAD_REQUEST),
    
    // HTTP 관련
    HTTP_CLIENT_ERROR("클라이언트 요청 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    HTTP_SERVER_ERROR("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("입력값 검증 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    UNEXPECTED_ERROR("예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String message;
    private final HttpStatus status;
    
    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
}
