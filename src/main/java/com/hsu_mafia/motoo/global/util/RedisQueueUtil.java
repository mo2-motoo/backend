package com.hsu_mafia.motoo.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisQueueUtil {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 주문 대기 큐 키
    private static final String ORDER_QUEUE_KEY = "order:queue:pending";
    
    // 주문 처리 중 큐 키
    private static final String ORDER_PROCESSING_KEY = "order:queue:processing";
    
    /**
     * 주문을 대기 큐에 추가합니다.
     */
    public void addToOrderQueue(Object orderMessage) {
        try {
            redisTemplate.opsForList().rightPush(ORDER_QUEUE_KEY, orderMessage);
            log.info("주문이 대기 큐에 추가되었습니다: {}", orderMessage);
        } catch (Exception e) {
            log.error("주문 큐 추가 중 오류 발생: {}", e.getMessage(), e);
            // Redis 연결 실패 시에도 예외를 던지지 않고 로그만 남김
            log.warn("Redis 연결 실패로 인해 주문 큐에 추가되지 않았습니다. 주문은 생성되었지만 체결 대기 큐에 추가되지 않았습니다.");
        }
    }
    
    /**
     * 대기 큐에서 주문을 가져옵니다.
     */
    public Object getFromOrderQueue() {
        try {
            return redisTemplate.opsForList().leftPop(ORDER_QUEUE_KEY, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("주문 큐에서 가져오기 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 대기 큐의 모든 주문을 가져옵니다.
     */
    public List<Object> getAllFromOrderQueue() {
        try {
            return redisTemplate.opsForList().range(ORDER_QUEUE_KEY, 0, -1);
        } catch (Exception e) {
            log.error("주문 큐 전체 조회 중 오류 발생: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * 대기 큐의 크기를 반환합니다.
     */
    public Long getOrderQueueSize() {
        try {
            return redisTemplate.opsForList().size(ORDER_QUEUE_KEY);
        } catch (Exception e) {
            log.error("주문 큐 크기 조회 중 오류 발생: {}", e.getMessage(), e);
            return 0L;
        }
    }
    
    /**
     * 주문을 처리 중 큐에 추가합니다.
     */
    public void addToProcessingQueue(Object orderMessage) {
        try {
            redisTemplate.opsForList().rightPush(ORDER_PROCESSING_KEY, orderMessage);
            log.info("주문이 처리 중 큐에 추가되었습니다: {}", orderMessage);
        } catch (Exception e) {
            log.error("처리 중 큐 추가 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 처리 중 큐에서 주문을 제거합니다.
     */
    public void removeFromProcessingQueue(Object orderMessage) {
        try {
            redisTemplate.opsForList().remove(ORDER_PROCESSING_KEY, 1, orderMessage);
            log.info("주문이 처리 중 큐에서 제거되었습니다: {}", orderMessage);
        } catch (Exception e) {
            log.error("처리 중 큐 제거 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 큐 상태를 로깅합니다.
     */
    public void logQueueStatus() {
        try {
            Long pendingSize = getOrderQueueSize();
            Long processingSize = redisTemplate.opsForList().size(ORDER_PROCESSING_KEY);
            log.info("큐 상태 - 대기 중: {}, 처리 중: {}", pendingSize, processingSize);
        } catch (Exception e) {
            log.error("큐 상태 로깅 중 오류 발생: {}", e.getMessage(), e);
        }
    }
} 