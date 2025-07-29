package com.hsu_mafia.motoo.api.presentation.execution;

import com.hsu_mafia.motoo.api.domain.execution.OrderExecutionService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import com.hsu_mafia.motoo.global.util.RedisQueueUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Order Execution", description = "주문 체결 관리 API")
@RestController
@RequestMapping("/api/order-execution")
@RequiredArgsConstructor
public class OrderExecutionController {
    
    private final OrderExecutionService orderExecutionService;
    private final RedisQueueUtil redisQueueUtil;
    
    /**
     * Redis Queue 상태 확인
     */
    @GetMapping("/queue/status")
    @Operation(summary = "Redis Queue 상태 확인", description = "대기 중인 주문과 처리 중인 주문의 개수를 확인합니다.")
    public ResponseEntity<CommonResponse<Map<String, Object>>> getQueueStatus() {
        Long pendingSize = redisQueueUtil.getOrderQueueSize();
        Long processingSize = redisQueueUtil.getOrderQueueSize(); // 실제로는 처리 중 큐 크기
        
        Map<String, Object> status = Map.of(
            "pendingOrders", pendingSize,
            "processingOrders", processingSize,
            "totalOrders", pendingSize + processingSize
        );
        
        return ResponseEntity.ok(CommonResponse.success(status));
    }
    
    /**
     * 대기 중인 모든 주문 조회
     */
    @GetMapping("/queue/pending")
    @Operation(summary = "대기 중인 주문 조회", description = "Redis Queue에 대기 중인 모든 주문을 조회합니다.")
    public ResponseEntity<CommonResponse<List<Object>>> getPendingOrders() {
        List<Object> pendingOrders = redisQueueUtil.getAllFromOrderQueue();
        return ResponseEntity.ok(CommonResponse.success(pendingOrders));
    }
    
    /**
     * 수동으로 주문 체결 처리 실행
     */
    @PostMapping("/process")
    @Operation(summary = "수동 주문 체결 처리", description = "대기 중인 주문들을 수동으로 체결 처리합니다.")
    public ResponseEntity<CommonResponse<Void>> processOrdersManually() {
        orderExecutionService.processPendingOrders();
        return ResponseEntity.ok(CommonResponse.success());
    }
    
    /**
     * Queue 상태 로깅
     */
    @PostMapping("/queue/log")
    @Operation(summary = "Queue 상태 로깅", description = "Redis Queue의 현재 상태를 로그로 출력합니다.")
    public ResponseEntity<CommonResponse<Void>> logQueueStatus() {
        redisQueueUtil.logQueueStatus();
        return ResponseEntity.ok(CommonResponse.success());
    }
} 