#!/bin/bash

echo "=== Redis Queue 실시간 모니터링 ==="
echo "모니터링을 중단하려면 Ctrl+C를 누르세요."
echo ""

# 실시간으로 Queue 상태를 모니터링
while true; do
    clear
    echo "=== Redis Queue 상태 모니터링 ==="
    echo "시간: $(date)"
    echo ""
    
    # 대기 중인 주문 큐 크기
    PENDING_SIZE=$(docker exec redis redis-cli LLEN order:queue:pending)
    echo "대기 중인 주문: $PENDING_SIZE개"
    
    # 처리 중인 주문 큐 크기
    PROCESSING_SIZE=$(docker exec redis redis-cli LLEN order:queue:processing)
    echo "처리 중인 주문: $PROCESSING_SIZE개"
    
    # 총 주문 수
    TOTAL=$((PENDING_SIZE + PROCESSING_SIZE))
    echo "총 주문 수: $TOTAL개"
    
    echo ""
    echo "=== 최근 대기 중인 주문 (최대 5개) ==="
    if [ "$PENDING_SIZE" -gt 0 ]; then
        docker exec redis redis-cli LRANGE order:queue:pending 0 4 | jq '.' 2>/dev/null || docker exec redis redis-cli LRANGE order:queue:pending 0 4
    else
        echo "대기 중인 주문이 없습니다."
    fi
    
    echo ""
    echo "=== Redis 메모리 사용량 ==="
    docker exec redis redis-cli INFO memory | grep -E "(used_memory_human|used_memory_peak_human)"
    
    # 5초 대기
    sleep 5
done 