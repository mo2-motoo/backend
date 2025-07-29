# 주식 시세 수집 및 저장 시스템

한국투자증권 API를 활용한 주식 시세 수집 및 저장 시스템입니다.

## 📊 시스템 개요

- **대상 종목**: KOSPI 200 상위 32개 + NASDAQ 상위 30개 종목
- **수집 주기**: 1분봉, 1시간봉, 1일봉 데이터 수집 및 저장
- **자동화**: 스케줄링을 통한 자동 데이터 수집 및 종목 갱신

## 🏗️ 시스템 아키텍처

### Entity 구조

- `Stock`: 종목 정보 (종목코드, 종목명, 시장구분, 활성화여부)
- `StockPriceMinute`: 1분봉 시세 (복합키: 종목코드+시간)
- `StockPriceHour`: 1시간봉 시세 (복합키: 종목코드+시간)
- `StockPriceDaily`: 1일봉 시세 (복합키: 종목코드+날짜)

### Service 구조

- `StockManagementService`: 종목 관리 (등록/갱신/활성화)
- `StockDataCollectionService`: 시세 수집 및 저장
- `StockSchedulerService`: 스케줄링 관리
- `StockApiService`: 한국투자증권 API 호출

### AOP 및 매핑

- `LoggingAspect`: Spring AOP를 활용한 로깅 처리
- `StockMapper`: MapStruct를 활용한 Entity-DTO 매핑
- `PortfolioMapper`: 포트폴리오 관련 매핑

### 상수 관리

- `ApiConstants`: API URL, 헤더, 거래시간 등 상수 관리

## 📅 스케줄링

### 자동 실행 스케줄

- **1분봉 수집**: 평일 09:00-15:30, 매 1분마다
- **1시간봉 집계**: 매 시간 정각
- **1일봉 집계**: 매일 자정
- **종목 갱신**: 매월 1일 자정

### 수동 실행 API

모든 스케줄링 작업은 수동으로도 실행 가능합니다.

### E2E 테스트 API

전체 시스템의 End-to-End 테스트를 위한 API가 제공됩니다.

## 🔧 API 사용법

### 1. 종목 관리 API

#### KOSPI 200 종목 갱신

```http
POST /api/stock-management/stocks/kospi200
```

#### NASDAQ 상위 종목 갱신

```http
POST /api/stock-management/stocks/nasdaq
```

#### 활성화된 종목 조회

```http
GET /api/stock-management/stocks/active
```

#### 시장별 활성화된 종목 조회

```http
GET /api/stock-management/stocks/active/{marketType}
```

#### 종목 활성화 상태 변경

```http
PUT /api/stock-management/stocks/{stockCode}/active?isActive=true
```

### 2. 데이터 수집 API

#### 1분봉 데이터 수집 (수동)

```http
POST /api/stock-management/data/collect/minute
```

#### 1시간봉 데이터 집계 (수동)

```http
POST /api/stock-management/data/aggregate/hour
```

#### 1일봉 데이터 집계 (수동)

```http
POST /api/stock-management/data/aggregate/daily
```

#### E2E 테스트

```http
POST /api/stock-management/test/e2e
```

### 3. 기존 주식 API

#### 주식 목록 조회

```http
GET /api/stocks?page=0&size=20
```

#### 주식 상세 조회

```http
GET /api/stocks/{stockCode}
```

#### 주식 검색

```http
POST /api/stocks/search
Content-Type: application/json

{
  "keyword": "삼성전자",
  "marketType": "KOSPI",
  "industryName": "전자제품"
}
```

## 🚀 시작하기

### 1. 환경 설정

- 한국투자증권 API 키 설정 (`application.yml`)
- 데이터베이스 연결 설정

### 2. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 3. 초기 데이터 로딩

애플리케이션 시작 시 자동으로 테스트용 종목 데이터가 생성됩니다.

### 4. Swagger UI 접속

```
http://localhost:8080/swagger-ui.html
```

## 📊 데이터베이스 스키마

### Stock 테이블

```sql
CREATE TABLE stocks (
    stock_code VARCHAR(10) PRIMARY KEY,
    stock_name VARCHAR(50) NOT NULL,
    outline VARCHAR(255),
    market_type VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    industry_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Stock Price 테이블들

```sql
-- 1분봉
CREATE TABLE stock_price_minute (
    stock_code VARCHAR(10),
    timestamp TIMESTAMP,
    open_price BIGINT NOT NULL,
    high_price BIGINT NOT NULL,
    low_price BIGINT NOT NULL,
    close_price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    amount BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (stock_code, timestamp)
);

-- 1시간봉
CREATE TABLE stock_price_hour (
    stock_code VARCHAR(10),
    timestamp TIMESTAMP,
    open_price BIGINT NOT NULL,
    high_price BIGINT NOT NULL,
    low_price BIGINT NOT NULL,
    close_price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    amount BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (stock_code, timestamp)
);

-- 1일봉
CREATE TABLE stock_price_daily (
    stock_code VARCHAR(10),
    date DATE,
    open_price BIGINT NOT NULL,
    high_price BIGINT NOT NULL,
    low_price BIGINT NOT NULL,
    close_price BIGINT NOT NULL,
    volume BIGINT NOT NULL,
    amount BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (stock_code, date)
);
```

## 🔍 모니터링

### 로그 확인

- 데이터 수집 성공/실패 로그
- 스케줄러 실행 로그
- API 호출 오류 로그

### 주요 로그 패턴

```
INFO  - 스케줄러: 1분봉 데이터 수집 시작
INFO  - 1분봉 데이터 수집이 완료되었습니다. 총 10개 종목
ERROR - 종목 005930의 1분봉 데이터 수집 중 오류
```

## ⚠️ 주의사항

1. **API 호출 제한**: 한국투자증권 API 호출 제한을 준수해야 합니다.
2. **거래시간**: 1분봉 수집은 평일 09:00-15:30에만 실행됩니다.
3. **토큰 관리**: API 토큰은 자동으로 갱신되지만, 초기 설정이 필요합니다.
4. **데이터 중복**: 동일 시간대 데이터 중복 저장을 방지합니다.

## 🧪 테스트

### 단위 테스트

```bash
./gradlew test
```

### API 테스트

Swagger UI를 통해 각 API를 테스트할 수 있습니다.

### 수동 테스트 시나리오

1. 종목 갱신 API 호출
2. 활성화된 종목 조회
3. 1분봉 데이터 수집 실행
4. 데이터 집계 실행

## 📝 개발 가이드

### 새로운 종목 추가

1. `StockManagementService.updateKospi200Stocks()` 또는 `updateNasdaqStocks()` 호출
2. 또는 직접 `Stock` Entity 생성

### 새로운 시세 데이터 추가

1. 새로운 Entity 클래스 생성
2. Repository 인터페이스 생성
3. Service 클래스에 수집 로직 추가
4. 스케줄러에 등록

### API 확장

1. Controller에 새로운 엔드포인트 추가
2. Swagger 문서화 추가
3. 예외 처리 추가
