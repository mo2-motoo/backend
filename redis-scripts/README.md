# Redis Queue 관리 스크립트

이 디렉토리에는 Redis Queue를 관리하기 위한 유틸리티 스크립트들이 포함되어 있습니다.

## 스크립트 목록

### 1. `queue-status.sh`

Redis Queue의 현재 상태를 확인합니다.

```bash
./redis-scripts/queue-status.sh
```

**기능:**

- 대기 중인 주문 개수
- 처리 중인 주문 개수
- 총 주문 개수
- 대기 중인 주문 상세 정보
- 처리 중인 주문 상세 정보
- Redis 서버 정보

### 2. `clear-queues.sh`

Redis Queue를 모두 비웁니다.

```bash
./redis-scripts/clear-queues.sh
```

**기능:**

- 대기 중인 주문 큐 비우기
- 처리 중인 주문 큐 비우기
- 비우기 후 상태 확인

### 3. `redis-cli.sh`

Redis CLI에 직접 접근합니다.

```bash
./redis-scripts/redis-cli.sh
```

**기능:**

- Redis CLI 인터랙티브 모드
- 직접 명령어 실행 가능

### 4. `monitor-queues.sh`

Redis Queue를 실시간으로 모니터링합니다.

```bash
./redis-scripts/monitor-queues.sh
```

**기능:**

- 5초마다 Queue 상태 업데이트
- 실시간 주문 개수 모니터링
- 최근 주문 정보 표시
- Redis 메모리 사용량 표시

## 사용법

### 스크립트 실행 권한 부여

```bash
chmod +x redis-scripts/*.sh
```

### Docker Compose 실행

```bash
docker compose up -d
```

### Queue 상태 확인

```bash
./redis-scripts/queue-status.sh
```

### 실시간 모니터링

```bash
./redis-scripts/monitor-queues.sh
```

## Redis CLI 직접 명령어

### Queue 상태 확인

```bash
# 대기 중인 주문 개수
docker exec redis redis-cli LLEN order:queue:pending

# 처리 중인 주문 개수
docker exec redis redis-cli LLEN order:queue:processing

# 대기 중인 주문 목록
docker exec redis redis-cli LRANGE order:queue:pending 0 -1

# 처리 중인 주문 목록
docker exec redis redis-cli LRANGE order:queue:processing 0 -1
```

### Queue 관리

```bash
# Queue 비우기
docker exec redis redis-cli DEL order:queue:pending
docker exec redis redis-cli DEL order:queue:processing

# 특정 주문 제거
docker exec redis redis-cli LREM order:queue:pending 1 "주문데이터"

# Queue에 주문 추가 (테스트용)
docker exec redis redis-cli RPUSH order:queue:pending '{"orderId": 1, "userId": 1, "stockCode": "005930"}'
```

### Redis 서버 정보

```bash
# 서버 정보
docker exec redis redis-cli INFO server

# 메모리 정보
docker exec redis redis-cli INFO memory

# 클라이언트 정보
docker exec redis redis-cli INFO clients

# 통계 정보
docker exec redis redis-cli INFO stats
```

## 주의사항

1. **Queue 비우기**: `clear-queues.sh`는 모든 대기 중인 주문을 삭제합니다. 신중하게 사용하세요.
2. **실시간 모니터링**: `monitor-queues.sh`는 Ctrl+C로 중단할 수 있습니다.
3. **JSON 파싱**: `jq`가 설치되어 있으면 JSON 데이터가 보기 좋게 표시됩니다.

## 문제 해결

### Redis 연결 오류

```bash
# Redis 컨테이너 상태 확인
docker ps | grep redis

# Redis 로그 확인
docker logs redis
```

### 스크립트 실행 오류

```bash
# 실행 권한 확인
ls -la redis-scripts/

# 실행 권한 부여
chmod +x redis-scripts/*.sh
```
