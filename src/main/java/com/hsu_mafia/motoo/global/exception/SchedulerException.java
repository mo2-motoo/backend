package com.hsu_mafia.motoo.global.exception;

public class SchedulerException extends BaseException {
    
    public SchedulerException(String message) {
        super(ErrorCode.SCHEDULER_ERROR, message);
    }
    
    public SchedulerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
} 