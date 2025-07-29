package com.hsu_mafia.motoo.global.exception;

public class StockApiException extends BaseException {
    
    public StockApiException(String message) {
        super(ErrorCode.STOCK_API_ERROR, message);
    }
    
    public StockApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
} 