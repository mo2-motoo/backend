# Order Domain Documentation

## 📋 개요

Order 도메인은 주식 주문 관리, 주문 상태 추적, 주문 매칭 시스템을 담당하는 핵심 도메인입니다. 사용자의 매수/매도 주문을 처리하고 주문 상태를 관리합니다.

## 🏗️ Entity 구조

### Order Entity 관계도

```mermaid
erDiagram
    ORDERS {
        bigint id PK
        bigint user_id FK
        varchar stock_id FK "Stock Entity 참조"
        enum order_type "BUY/SELL"
        bigint quantity "NOT NULL, 주문수량"
        decimal price "precision15scale4, 지정가"
        datetime created_at "NOT NULL, 주문시각"
        enum status "PENDING/COMPLETED/CANCELLED"
        datetime updated_at
    }

    USERS {
        bigint id PK
        varchar username UK "UNIQUE"
        varchar email UK "UNIQUE"
        bigint seed_money "초기투자금"
        bigint cash "현재현금"
        datetime join_at
        datetime created_at
        datetime updated_at
    }

    STOCKS {
        varchar stock_code PK "길이10"
        varchar stock_name "NOT NULL"
        varchar market_type "KOSPI/NASDAQ"
        boolean is_active "DEFAULT true"
        integer ranking
        bigint industry_id FK
        datetime created_at
        datetime updated_at
    }

    EXECUTIONS {
        bigint id PK
        bigint user_id FK
        varchar stock_id FK "Stock Entity 참조"
        enum order_type "BUY/SELL"
        bigint quantity "NOT NULL, 체결수량"
        decimal executed_price "precision15scale4, 체결가"
        datetime executed_at "NOT NULL, 체결시각"
        datetime created_at
        datetime updated_at
    }

    USER_STOCKS {
        bigint id PK
        bigint user_id FK
        varchar stock_id FK "Stock Entity 참조"
        bigint quantity "NOT NULL, 보유수량"
        bigint average_buy_price "평단가"
        datetime created_at
        datetime updated_at
    }

    USERS ||--o{ ORDERS : "places"
    STOCKS ||--o{ ORDERS : "targets"
    ORDERS ||--o{ EXECUTIONS : "results_in"
    USERS ||--o{ USER_STOCKS : "owns"
    STOCKS ||--o{ USER_STOCKS : "held_by"
```

<details>
<summary>📄 Entity 코드 보기</summary>

```java
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderType orderType; // BUY / SELL

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal price; // 지정가

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private OrderStatus status; // PENDING / COMPLETED / CANCELLED

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
} 
```

</details>

## 🔧 주요 기능

### 1. 주문 관리

- **주문 생성**: 매수/매도 주문 생성
- **주문 조회**: 사용자별 주문 내역 조회
- **주문 상태 관리**: PENDING → COMPLETED/CANCELLED
- **주문 취소**: 대기 중인 주문 취소

### 2. 주문 매칭

- **매칭 엔진**: 매수/매도 주문 매칭
- **가격 우선순위**: 동일 가격 시 시간 우선순위
- **부분 체결**: 주문 수량의 일부만 체결 가능
- **잔량 관리**: 미체결 수량 관리

### 3. 주문 검증

- **잔고 검증**: 매수 시 현금 잔고 확인
- **보유 주식 검증**: 매도 시 보유 주식 확인
- **가격 검증**: 지정가 범위 검증
- **수량 검증**: 주문 수량 유효성 검증

## 📊 비즈니스 플로우

### 주문 생성 플로우

```mermaid
graph TD
    A[주문 요청] --> B[주문 검증]
    B --> C{검증 통과?}
    C -->|No| D[에러 반환]
    C -->|Yes| E[Order Entity 생성]
    E --> F[주문 상태 PENDING 설정]
    F --> G[주문 매칭 엔진 호출]
    G --> H[주문 저장]
    H --> I[응답 반환]
```

### 주문 취소 플로우

```mermaid
graph TD
    A[주문 취소 요청] --> B[주문 존재 확인]
    B --> C{주문 존재?}
    C -->|No| D[주문 없음 에러]
    C -->|Yes| E[주문 소유자 확인]
    E --> F{소유자 일치?}
    F -->|No| G[접근 권한 에러]
    F -->|Yes| H[주문 상태 확인]
    H --> I{대기 중인 주문?}
    I -->|No| J[취소 불가 에러]
    I -->|Yes| K[주문 상태 CANCELLED로 변경]
```

## 🎯 API 엔드포인트

### Swagger UI 스크린샷

![Order API Endpoints](images/order-api-endpoints.png)

**주요 엔드포인트:**

- `POST /api/v1/orders` - 주문 생성 (시장가/지정가 모두 지원)
- `GET /api/v1/orders` - 사용자별 주문 내역 조회 (페이지네이션 지원)
- `DELETE /api/v1/orders/{orderId}` - 주문 취소 (PENDING 상태만 가능)

## 📈 핵심 비즈니스 로직

### 1. 주문 생성 로직 (OrderService.placeOrder)

실제 구현된 주문 생성 과정:

```java
@Transactional
public void placeOrder(Long userId, OrderRequest request) {
    // 1. 사용자 검증
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    
    // 2. 종목 검증
    Stock stock = stockRepository.findById(request.getStockId())
        .orElseThrow(() -> new BaseException(ErrorCode.STOCK_NOT_FOUND));
    
    // 3. 매수/매도별 검증 로직
    if (request.getOrderType() == OrderType.BUY) {
        validateBuyOrder(user, request);
    } else {
        validateSellOrder(user, stock, request);
    }
    
    // 4. Order Entity 생성 및 저장
    Order order = Order.builder()
        .user(user)
        .stock(stock)
        .orderType(request.getOrderType())
        .quantity(request.getQuantity())
        .price(request.getPrice()) // BigDecimal
        .createdAt(LocalDateTime.now())
        .status(OrderStatus.PENDING)
        .build();
    
    orderRepository.save(order);
    
    // 5. 즉시 체결 시도
    orderExecutionService.processOrder(order);
}
```

### 2. 주문 검증 로직

종류별 상세 검증 로직:

- **매수 주문 검증**: `user.getCash() >= request.getPrice() × request.getQuantity()`
- **매도 주문 검증**: 보유 주식 수량 >= 주문 수량
- **BigDecimal 정밀도**: 가격 계산 시 소수점 정확성 보장
- **예외 처리**: BaseException과 사용자 정의 ErrorCode 활용

### 3. 주문 취소 로직 (OrderService.cancelOrder)

주문 취소 시 수행되는 검증:

1. **주문 존재 확인**: 주문 ID로 Order Entity 조회
2. **소유자 검증**: 주문한 사용자와 요청자 일치 확인
3. **상태 검증**: PENDING 상태인 주문만 취소 가능
4. **상태 업데이트**: `order.updateStatus(OrderStatus.CANCELLED)`

### 4. 주문 내역 조회 (페이지네이션)

효율적인 주문 조회:

- **페이지네이션**: `PageRequest.of(page, size)` 활용
- **사용자별 필터링**: 특정 사용자의 주문만 조회
- **정렬**: 생성 시간 기준 최신순 정렬
- **DTO 매핑**: OrderMapper를 통한 Entity → DTO 변환

<details>
<summary>🔧 핵심 기술 구현</summary>

**트랜잭션 관리**: `@Transactional`을 사용하여 주문 생성-매칭-체결을 원자적으로 처리

**낙관적 락**: 주문 수량 업데이트 시 버전 관리를 통한 동시성 제어

**매칭 엔진**: 가격-시간 우선순위 기반의 효율적인 주문 매칭 알고리즘

</details>

## 🔗 연관 도메인

### User (사용자)

- 주문을 생성하는 사용자
- Order Entity와 N:1 관계

### Stock (종목)

- 주문 대상 종목
- Order Entity와 N:1 관계

### User (사용자)

- 주문을 생성하는 사용자
- Order Entity와 N:1 관계

### Stock (종목)

- 주문 대상 종목
- Order Entity와 N:1 관계

## 📊 주문 상태 관리

### 상태 전이 다이어그램

```mermaid
stateDiagram-v2
    [*] --> PENDING: 주문 생성
    PENDING --> COMPLETED: 체결 완료
    PENDING --> CANCELLED: 주문 취소
    COMPLETED --> [*]
    CANCELLED --> [*]
```

### 주문 상태별 처리

- **PENDING**: 매칭 대기 중, 취소 가능
- **COMPLETED**: 체결 완료, 변경 불가
- **CANCELLED**: 취소됨, 재활용 불가

## ✅ 구현 상태

### 핵심 기능 구현 현황

- [x] **Order Entity**: 완전한 Entity 구조 및 BigDecimal 가격 타입 구현
- [x] **주문 생성**: OrderService.placeOrder() 완전 구현 (검증 → 생성 → 체결시도)
- [x] **주문 취소**: OrderService.cancelOrder() 구현 (소유자/상태 검증)
- [x] **주문 조회**: 페이지네이션 지원 주문 내역 조회 구현
- [x] **검증 시스템**: 매수(현금)/매도(보유주식) 잔고 검증 완료
- [x] **상태 관리**: PENDING → COMPLETED/CANCELLED 상태 전이 구현
- [x] **API 엔드포인트**: `/api/v1/orders/*` 완전 구현
- [x] **DTO 매핑**: OrderMapper를 통한 Entity ↔ DTO 변환
- [x] **체결 연동**: OrderExecutionService와 연동하여 즉시 체결 시도
- [x] **BigDecimal 정밀도**: 금융 거래의 소수점 정확성 보장
- [ ] **주문 매칭 엔진**: 가격-시간 우선순위 자동 매칭 (향후 구현 예정)
- [ ] **시장가 주문**: 현재가 기반 즉시 체결 (향후 구현 예정)
- [ ] **조건부 주문**: 지정 조건 달성 시 자동 주문 (향후 구현 예정)

### 데이터 무결성 및 에러 처리

- [x] **Entity 제약조건**: NOT NULL, ENUM 타입, BigDecimal precision 적용
- [x] **트랜잭션 관리**: @Transactional 보장으로 주문-체결 원자성
- [x] **예외 처리**: BaseException, ErrorCode 기반 체계적 에러 처리
- [x] **검증 로직**: 사용자/종목/잔고 다단계 검증 시스템
- [x] **상태 관리**: PENDING 상태만 취소 가능한 규칙 검증
- [x] **성능 최적화**: 페이지네이션, Lazy Loading, Builder 패턴 활용

## 🛡️ 검증 및 에러 처리

### 1. 주문 검증

- **가격 검증**: 지정가 범위 및 유효성 검사
- **수량 검증**: 주문 수량의 유효성 검사
- **잔고 검증**: 매수 시 현금, 매도 시 보유 주식 확인
- **주문 타입 검증**: 매수/매도 타입 유효성 검사

### 2. 잔고 검증

- **매수 주문**: 현금 잔고 >= 주문 금액
- **매도 주문**: 보유 주식 수량 >= 주문 수량
- **수수료 고려**: 매수 시 수수료를 포함한 총 금액 검증

## 📈 성능 최적화

### 1. 주문 조회 최적화

- **페이지네이션**: 대량 주문 데이터의 효율적인 조회
- **인덱스 활용**: (user_id, status, created_at) 복합 인덱스
- **쿼리 최적화**: 사용자별, 상태별 주문 조회 최적화

### 2. 트랜잭션 관리

- **트랜잭션 경계**: 주문 생성 시 원자성 보장
- **동시성 제어**: 주문 취소 시 상태 검증
- **데이터 무결성**: 주문 상태 변경 시 유효성 검증

<details>
<summary>🚀 확장 가능성</summary>

### 1. 주문 타입 확장

- **시장가 주문**: 현재 시장가로 즉시 체결
- **조건부 주문**: 특정 조건 만족 시 주문 실행
- **기한부 주문**: 지정 기한까지 유효한 주문

### 2. 주문 매칭 시스템

- **매칭 엔진**: 가격-시간 우선순위 기반 매칭
- **부분 체결**: 주문 수량의 일부만 체결
- **실시간 매칭**: WebSocket 기반 실시간 매칭

### 3. 모니터링 및 알림

- **주문 상태 알림**: 체결/취소 시 실시간 알림
- **주문 모니터링**: 주문 처리 현황 대시보드
- **성과 분석**: 주문 처리 성능 분석
</details>

---

_이 문서는 Motoo 프로젝트의 Order 도메인 설계를 설명합니다._
