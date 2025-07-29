#!/bin/bash

echo "=== Redis Queue 비우기 ==="
echo ""

# 대기 중인 주문 큐 비우기
echo "대기 중인 주문 큐를 비우는 중..."
PENDING_SIZE=$(docker exec redis redis-cli LLEN order:queue:pending)
if [ "$PENDING_SIZE" -gt 0 ]; then
    docker exec redis redis-cli DEL order:queue:pending
    echo "대기 중인 주문 큐를 비웠습니다. (제거된 주문: $PENDING_SIZE개)"
else
    echo "대기 중인 주문 큐가 이미 비어있습니다."
fi

# 처리 중인 주문 큐 비우기
echo "처리 중인 주문 큐를 비우는 중..."
PROCESSING_SIZE=$(docker exec redis redis-cli LLEN order:queue:processing)
if [ "$PROCESSING_SIZE" -gt 0 ]; then
    docker exec redis redis-cli DEL order:queue:processing
    echo "처리 중인 주문 큐를 비웠습니다. (제거된 주문: $PROCESSING_SIZE개)"
else
    echo "처리 중인 주문 큐가 이미 비어있습니다."
fi

echo ""
echo "=== Queue 상태 확인 ==="
docker exec redis redis-cli LLEN order:queue:pending
docker exec redis redis-cli LLEN order:queue:processing 