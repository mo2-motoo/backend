package com.hsu_mafia.motoo.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Service 계층의 메서드 실행 시간과 결과를 로깅합니다.
     * 단, private 메서드는 제외하여 중복 로깅을 방지합니다.
     */
    @Around("execution(public * com.hsu_mafia.motoo.api.domain.*.service.*Service.*(..))")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("[{}] {}.{}() 실행 완료 - 실행시간: {}ms", 
                    className, className, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[{}] {}.{}() 실행 실패 - 실행시간: {}ms, 오류: {}", 
                    className, className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    /**
     * API 호출 메서드의 실행을 로깅합니다.
     * 단, private 메서드는 제외하여 중복 로깅을 방지합니다.
     */
    @Around("execution(public * com.hsu_mafia.motoo.api.domain.stock.StockApiService.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("[API] {}.{}() 호출 성공 - 실행시간: {}ms", 
                    className, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[API] {}.{}() 호출 실패 - 실행시간: {}ms, 오류: {}", 
                    className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    /**
     * Repository 계층의 메서드 실행을 로깅합니다.
     */
    @Around("execution(public * com.hsu_mafia.motoo.api.domain.*.repository.*Repository.*(..))")
    public Object logRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.debug("[{}] {}.{}() 실행 완료 - 실행시간: {}ms", 
                    className, className, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[{}] {}.{}() 실행 실패 - 실행시간: {}ms, 오류: {}", 
                    className, className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }
} 