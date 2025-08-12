package com.hsu_mafia.motoo.global.exception;

public class TransactionDateRangeException extends BaseException {
    public TransactionDateRangeException() {
        super(ErrorCode.TRANSACTION_DATE_RANGE_TOO_LONG);
    }
    
    public TransactionDateRangeException(String message) {
        super(ErrorCode.TRANSACTION_DATE_RANGE_TOO_LONG, message);
    }
} 