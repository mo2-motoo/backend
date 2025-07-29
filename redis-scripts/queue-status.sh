#!/bin/bash

echo "=== Redis Queue 상태 확인 ==="
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
echo "=== 대기 중인 주문 상세 정보 ==="
if [ "$PENDING_SIZE" -gt 0 ]; then
    docker exec redis redis-cli LRANGE order:queue:pending 0 -1 | jq '.' 2>/dev/null || docker exec redis redis-cli LRANGE order:queue:pending 0 -1
else
    echo "대기 중인 주문이 없습니다."
fi

echo ""
echo "=== 처리 중인 주문 상세 정보 ==="
if [ "$PROCESSING_SIZE" -gt 0 ]; then
    docker exec redis redis-cli LRANGE order:queue:processing 0 -1 | jq '.' 2>/dev/null || docker exec redis redis-cli LRANGE order:queue:processing 0 -1
else
    echo "처리 중인 주문이 없습니다."
fi

echo ""
echo "=== Redis 서버 정보 ==="
docker exec redis redis-cli INFO server | grep -E "(redis_version|uptime_in_seconds|connected_clients)" 