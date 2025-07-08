package com.hsu_mafia.motoo.global.exception;

public enum ErrorCode {
    // 사용자 관련
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    
    // 주식 관련
    STOCK_NOT_FOUND("주식을 찾을 수 없습니다."),
    
    // 주문 관련
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다."),
    ORDER_ACCESS_DENIED("주문에 대한 접근 권한이 없습니다."),
    ORDER_CANNOT_CANCEL("취소할 수 없는 주문입니다."),
    
    // 자금 관련
    INSUFFICIENT_CASH("보유 현금이 부족합니다."),
    INSUFFICIENT_STOCK("보유 주식이 부족합니다."),
    
    // 일반
    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),
    MAPPING_PROBLEM("매핑 과정에서 문제가 발생했습니다.");
    
    private final String message;
    
    ErrorCode(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
