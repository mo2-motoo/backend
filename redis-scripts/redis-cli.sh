#!/bin/bash

echo "=== Redis CLI 접근 ==="
echo "Redis CLI에 접근합니다. 종료하려면 'exit'를 입력하세요."
echo ""

# Redis CLI에 접근
docker exec -it redis redis-cli 