# k6 Load Testing Guide

## 📋 목차

- [개요](#개요)
- [기본 사용법](#기본-사용법)
- [고급 사용법](#고급-사용법)
- [스크립트 수정](#스크립트-수정)
- [모니터링](#모니터링)
- [문제 해결](#문제-해결)

## 🎯 개요

k6는 성능 테스트를 위한 현대적인 로드 테스트 도구입니다. 이 프로젝트에서는 Docker 컨테이너로 실행되며, Prometheus와 Grafana를 통해 실시간 모니터링이 가능합니다.

## 🚀 기본 사용법

### 1. k6 컨테이너에 접속

```bash
docker exec -it k6 bash
```

### 2. 사용 가능한 스크립트 확인

```bash
ls -la /scripts/
```

### 3. 기본 로드 테스트 실행

```bash
k6 run /scripts/load-test.js
```

### 4. Prometheus로 메트릭 전송하며 실행

```bash
k6 run --out experimental-prometheus-rw=remoteWriteUrl=http://prometheus:9090/api/v1/write /scripts/load-test.js
```

### 5. 컨테이너에서 나가기

```bash
exit
```

### 실제 테스트 화면
<img width="2278" height="1350" alt="image" src="https://github.com/user-attachments/assets/ed6d68f3-f84c-4a8d-9d8b-84b07d324ca4" />

## ⚡ 고급 사용법

### 다양한 출력 옵션

```bash
# JSON 출력
k6 run --out json=results.json /scripts/load-test.js

# InfluxDB 출력
k6 run --out influxdb=http://influxdb:8086/k6 /scripts/load-test.js

# StatsD 출력
k6 run --out statsd=localhost:8125 /scripts/load-test.js
```

### 환경 변수 사용

```bash
# 환경 변수로 URL 설정
K6_BASE_URL=http://backend:8080 k6 run /scripts/load-test.js
```

### 커스텀 설정으로 실행

```bash
# VU 수와 지속 시간 직접 지정
k6 run --vus 5 --duration 30s /scripts/load-test.js
```

## 📝 스크립트 수정

### 현재 스크립트 설정

```javascript
export const options = {
  stages: [
    { duration: "2m", target: 10 }, // 2분: 0 → 10 VU로 증가
    { duration: "5m", target: 10 }, // 5분: 10 VU 유지
    { duration: "2m", target: 0 }, // 2분: 10 → 0 VU로 감소
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"], // 95% 요청이 500ms 이내
    http_req_failed: ["rate<0.1"], // 오류율 10% 미만
  },
};
```

### 빠른 테스트를 위한 설정

```javascript
export const options = {
  stages: [
    { duration: "30s", target: 5 }, // 30초: 0 → 5 VU
    { duration: "1m", target: 5 }, // 1분: 5 VU 유지
    { duration: "30s", target: 0 }, // 30초: 5 → 0 VU
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"],
    http_req_failed: ["rate<0.1"],
  },
};
```

### 대기 시간 단축

```javascript
// sleep(1); // 1초 대기
sleep(0.1); // 0.1초로 단축
```

## 📊 모니터링

### Grafana 대시보드 접속

- URL: http://localhost:3000
- 기본 계정: admin / admin

### Prometheus 메트릭 확인

```bash
# k6 메트릭 조회
curl "http://localhost:9090/api/v1/query?query=k6_http_reqs_total"

# 모든 k6 메트릭 조회
curl "http://localhost:9090/api/v1/query?query=k6"
```

### 주요 k6 메트릭

- `k6_http_reqs_total`: 총 HTTP 요청 수
- `k6_http_req_duration_seconds`: 응답 시간
- `k6_http_req_failed`: 실패한 요청 수
- `k6_vus`: 활성 가상 사용자 수
- `k6_iterations_total`: 총 반복 횟수

## 🔧 문제 해결

### 1. k6 컨테이너가 실행되지 않는 경우

```bash
# 컨테이너 상태 확인
docker ps -a | grep k6

# 컨테이너 재시작
docker compose -f docker-compose.monitoring.yml restart k6
```

### 2. Prometheus 연결 오류

```bash
# Prometheus 상태 확인
curl http://localhost:9090/api/v1/status/targets

# 네트워크 연결 확인
docker exec k6 ping prometheus
```

### 3. 메트릭이 Grafana에 표시되지 않는 경우

1. Grafana에서 데이터소스 확인
2. Prometheus 연결 상태 확인
3. k6 메트릭 전송 확인

### 4. 테스트가 너무 오래 걸리는 경우

- 스크립트의 `stages` 설정 단축
- `sleep()` 시간 단축
- VU 수 줄이기

## 📚 유용한 명령어 모음

### 컨테이너 관리

```bash
# k6 컨테이너만 시작
docker compose -f docker-compose.monitoring.yml up -d k6

# k6 컨테이너만 중지
docker compose -f docker-compose.monitoring.yml stop k6

# k6 로그 확인
docker logs k6
```

### 테스트 실행

```bash
# 빠른 테스트 (30초)
docker exec -it k6 k6 run --vus 3 --duration 30s /scripts/load-test.js

# 무한 반복 테스트
docker exec -it k6 k6 run --vus 5 --duration 0 /scripts/load-test.js

# 특정 스크립트 실행
docker exec -it k6 k6 run /scripts/your-custom-test.js
```

### 모니터링

```bash
# 실시간 로그 확인
docker logs -f k6

# Prometheus 타겟 상태 확인
curl http://localhost:9090/api/v1/targets
```

## 🎯 팁

1. **메트릭 전송 사용**: Prometheus 연동으로 Grafana에서 실시간 모니터링 가능
2. **단계적 테스트**: 처음에는 짧은 시간으로 테스트 후 점진적으로 늘리기
3. **모니터링 병행**: 테스트 실행 중 Grafana 대시보드 동시 확인
4. **로그 활용**: 오류 발생 시 `docker logs k6`로 상세 로그 확인

## 📞 도움말

- k6 공식 문서: https://k6.io/docs/
- Grafana 대시보드: http://localhost:3000
- Prometheus UI: http://localhost:9090
