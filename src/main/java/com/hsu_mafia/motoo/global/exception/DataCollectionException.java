package com.hsu_mafia.motoo.global.exception;

public class DataCollectionException extends BaseException {
    
    public DataCollectionException(String message) {
        super(ErrorCode.DATA_COLLECTION_ERROR, message);
    }
    
    public DataCollectionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
} 