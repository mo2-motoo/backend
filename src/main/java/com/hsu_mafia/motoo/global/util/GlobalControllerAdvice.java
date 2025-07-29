package com.hsu_mafia.motoo.global.util;

import com.hsu_mafia.motoo.global.common.CommonResponse;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.exception.TransactionDateRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import jakarta.validation.ConstraintViolationException;

/**
 * Global Exception Handler
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<?>> onBaseException(BaseException e) {
        log.error("BaseException: codeName = {}, message = {}", e.getErrorCode().name(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(CommonResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(TransactionDateRangeException.class)
    public ResponseEntity<CommonResponse<?>> onTransactionDateRangeException(TransactionDateRangeException e) {
        log.warn("TransactionDateRangeException: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(CommonResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<CommonResponse<?>> onHttpClientErrorException(HttpClientErrorException e) {
        log.error("HttpClientErrorException: {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.HTTP_CLIENT_ERROR.getStatus())
                .body(CommonResponse.fail(ErrorCode.HTTP_CLIENT_ERROR));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<CommonResponse<?>> onHttpServerErrorException(HttpServerErrorException e) {
        log.error("HttpServerErrorException: {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.HTTP_SERVER_ERROR.getStatus())
                .body(CommonResponse.fail(ErrorCode.HTTP_SERVER_ERROR));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<?>> onConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException: {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(CommonResponse.fail(ErrorCode.VALIDATION_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> onException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(ErrorCode.UNEXPECTED_ERROR.getStatus())
                .body(CommonResponse.fail(ErrorCode.UNEXPECTED_ERROR));
    }
}
