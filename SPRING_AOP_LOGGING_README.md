# Spring AOP와 LoggingAspect 활용 가이드

## 📖 개요

이 문서는 Motoo 프로젝트에서 Spring AOP(관점 지향 프로그래밍)를 활용한 로깅 시스템의 구현과 활용 방법을 설명합니다. 중앙화된 로깅 처리로 코드의 재사용성과 유지보수성을 크게 향상시킨 사례를 다룹니다.

---

## 🎯 기: Spring AOP의 기본 개념

### AOP란 무엇인가?

**관점 지향 프로그래밍(Aspect-Oriented Programming, AOP)**은 횡단 관심사(cross-cutting concerns)를 분리하여 모듈화하는 프로그래밍 패러다임입니다.

#### 횡단 관심사의 예시

- 로깅 (Logging)
- 트랜잭션 관리 (Transaction Management)
- 보안 (Security)
- 성능 모니터링 (Performance Monitoring)
- 예외 처리 (Exception Handling)

### Spring AOP의 핵심 개념

#### 1. Aspect (관점)

- 횡단 관심사를 모듈화한 클래스
- `@Aspect` 어노테이션으로 정의

#### 2. Join Point (조인 포인트)

- 프로그램 실행 중 특정 지점
- 메서드 실행, 예외 처리 등

#### 3. Pointcut (포인트컷)

- 조인 포인트를 선별하는 표현식
- 어떤 메서드에 Advice를 적용할지 결정

#### 4. Advice (어드바이스)

- 특정 조인 포인트에서 실행되는 코드
- `@Before`, `@After`, `@Around` 등

---

## 🚀 승: Spring AOP의 Proxy 메커니즘

### Proxy 패턴의 이해

Spring AOP는 **Proxy 패턴**을 기반으로 동작합니다. 실제 객체를 감싸는 프록시 객체가 생성되어, 메서드 호출을 가로채고 추가 기능을 수행합니다.

#### Proxy의 동작 과정

```java
// 실제 호출되는 코드
stockService.getStock(userId, stockCode);

// 프록시가 가로채서 실행하는 과정
1. 프록시 객체가 메서드 호출을 가로챔
2. Before Advice 실행 (로깅 시작)
3. 실제 메서드 실행
4. After Advice 실행 (로깅 완료)
5. 결과 반환
```

### Spring AOP의 Proxy 종류

#### 1. JDK Dynamic Proxy (기본)

- 인터페이스 기반 프록시
- `Proxy.newProxyInstance()` 사용
- 인터페이스가 있는 경우 자동 선택

#### 2. CGLIB Proxy

- 클래스 기반 프록시
- 바이트코드 조작으로 프록시 생성
- 인터페이스가 없는 경우 사용

### Proxy의 특징과 제약사항

#### 장점

- **투명성**: 기존 코드 수정 없이 기능 추가
- **재사용성**: 여러 클래스에 동일한 기능 적용
- **유지보수성**: 중앙화된 관리

#### 제약사항

- **Self-Invocation 문제**: 같은 클래스 내 메서드 호출 시 AOP 적용 안됨
- **Private 메서드 제외**: public 메서드만 대상
- **Final 클래스 제한**: CGLIB로 프록시 생성 불가

---

## 🔧 전: LoggingAspect 구현 분석

### 현재 구현된 LoggingAspect 구조

```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Service 계층 로깅
    @Around("execution(public * com.hsu_mafia.motoo.api.domain.*.service.*Service.*(..))")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 로깅 로직
    }

    // API 호출 로깅
    @Around("execution(public * com.hsu_mafia.motoo.api.domain.stock.StockApiService.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        // 로깅 로직
    }

    // Repository 계층 로깅
    @Around("execution(public * com.hsu_mafia.motoo.api.domain.*.repository.*Repository.*(..))")
    public Object logRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 로깅 로직
    }
}
```

### Pointcut 표현식 분석

#### 1. Service 계층

```java
"execution(public * com.hsu_mafia.motoo.api.domain.*.service.*Service.*(..))"
```

- **public**: public 메서드만 대상
- **com.hsu_mafia.motoo.api.domain.\*.service**: 모든 service 패키지
- **.\*Service**: Service로 끝나는 클래스
- **.\*(..)**: 모든 메서드 (모든 파라미터)

#### 2. API 호출

```java
"execution(public * com.hsu_mafia.motoo.api.domain.stock.StockApiService.*(..))"
```

- **StockApiService**: 특정 API 서비스 클래스만 대상

#### 3. Repository 계층

```java
"execution(public * com.hsu_mafia.motoo.api.domain.*.repository.*Repository.*(..))"
```

- **repository**: 모든 repository 패키지
- **.\*Repository**: Repository로 끝나는 클래스

### @Around Advice의 동작

```java
@Around("pointcut")
public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
    // Before: 메서드 실행 전 로직
    long startTime = System.currentTimeMillis();

    try {
        // 실제 메서드 실행
        Object result = joinPoint.proceed();

        // After: 메서드 실행 후 로직 (성공)
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("실행 완료 - 실행시간: {}ms", executionTime);

        return result;
    } catch (Exception e) {
        // After Throwing: 예외 발생 시 로직
        long executionTime = System.currentTimeMillis() - startTime;
        log.error("실행 실패 - 실행시간: {}ms, 오류: {}", executionTime, e.getMessage());
        throw e;
    }
}
```

---

## 🎯 결: 실제 활용 사례와 효과

### 현재 프로젝트에서의 활용

#### 1. 적용된 클래스들

**Service 계층**

- `StockService`: 주식 정보 조회 서비스
- `StockManagementService`: 주식 관리 서비스
- `StockDataCollectionService`: 데이터 수집 서비스
- `StockSchedulerService`: 스케줄링 서비스
- `OrderService`: 주문 서비스
- `PortfolioService`: 포트폴리오 서비스
- `UserService`: 사용자 서비스

**API 계층**

- `StockApiService`: 한국투자증권 API 호출 서비스

**Repository 계층**

- `StockRepository`: 주식 데이터 접근
- `OrderRepository`: 주문 데이터 접근
- `UserRepository`: 사용자 데이터 접근

#### 2. 로깅 레벨별 분리

```java
// Service 계층: INFO 레벨
log.info("[{}] {}.{}() 실행 완료 - 실행시간: {}ms",
        className, className, methodName, executionTime);

// API 호출: INFO 레벨
log.info("[API] {}.{}() 호출 성공 - 실행시간: {}ms",
        className, methodName, executionTime);

// Repository 계층: DEBUG 레벨
log.debug("[{}] {}.{}() 실행 완료 - 실행시간: {}ms",
        className, className, methodName, executionTime);
```

### 성능 모니터링 효과

#### 1. 실행 시간 추적

- 모든 Service 메서드의 실행 시간 자동 측정
- 성능 병목 지점 식별 가능
- API 호출 시간 모니터링

#### 2. 에러 추적

- 예외 발생 시점과 원인 자동 로깅
- 스택 트레이스와 함께 실행 시간 기록
- 디버깅 효율성 대폭 향상

### 코드 품질 향상

#### 1. 중복 코드 제거

**Before (AOP 적용 전)**

```java
@Service
public class StockService {
    public Stock getStock(String stockCode) {
        long startTime = System.currentTimeMillis();
        try {
            // 비즈니스 로직
            Stock stock = stockRepository.findByStockCode(stockCode);
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("getStock 실행 완료 - 실행시간: {}ms", executionTime);
            return stock;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("getStock 실행 실패 - 실행시간: {}ms, 오류: {}", executionTime, e.getMessage());
            throw e;
        }
    }
}
```

**After (AOP 적용 후)**

```java
@Service
public class StockService {
    public Stock getStock(String stockCode) {
        // 비즈니스 로직만 집중
        return stockRepository.findByStockCode(stockCode);
    }
}
```

#### 2. 관심사 분리

- **비즈니스 로직**: Service 클래스에 집중
- **로깅 로직**: LoggingAspect에서 중앙 관리
- **유지보수성**: 로깅 정책 변경 시 한 곳만 수정

### 확장 가능성

#### 1. 새로운 Advice 추가

```java
// 메서드 호출 횟수 측정
@Around("servicePointcut()")
public Object countMethodCalls(ProceedingJoinPoint joinPoint) throws Throwable {
    methodCallCounter.increment();
    return joinPoint.proceed();
}

// 메모리 사용량 측정
@Around("servicePointcut()")
public Object measureMemoryUsage(ProceedingJoinPoint joinPoint) throws Throwable {
    long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    Object result = joinPoint.proceed();
    long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    log.debug("메모리 사용량: {} bytes", afterMemory - beforeMemory);
    return result;
}
```

#### 2. 조건부 로깅

```java
@Around("servicePointcut() && args(userId, ..)")
public Object logUserSpecificOperations(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {
    if (userId != null && userId > 1000) {
        // VIP 사용자에 대한 상세 로깅
        log.info("VIP 사용자 {}의 작업 시작", userId);
    }
    return joinPoint.proceed();
}
```

---

## 📊 결론: AOP의 가치와 미래

### 달성한 효과

1. **코드 품질 향상**: 중복 코드 제거, 관심사 분리
2. **개발 생산성 증대**: 로깅 코드 작성 시간 절약
3. **운영 효율성 향상**: 중앙화된 로깅으로 문제 추적 용이
4. **성능 모니터링**: 자동화된 성능 측정

### Spring AOP의 한계와 대안

#### 한계점

- **Self-Invocation 문제**: 같은 클래스 내 메서드 호출 시 AOP 미적용
- **Proxy 기반 제약**: Final 클래스, Private 메서드 제외
- **런타임 오버헤드**: Proxy 생성과 메서드 호출 오버헤드

#### 대안 기술

- **AspectJ**: 컴파일 타임 위빙, 더 강력한 기능
- **ByteBuddy**: 바이트코드 조작 라이브러리
- **Micrometer**: 메트릭 수집 전용 라이브러리

### 향후 발전 방향

1. **실시간 모니터링**: 로그를 실시간으로 분석하여 알림 시스템 구축
2. **성능 분석**: 실행 시간 통계를 활용한 성능 최적화
3. **보안 강화**: 민감한 데이터 접근 로깅 및 감사
4. **마이크로서비스 연계**: 분산 추적 시스템과 연동

---

## 🔗 참고 자료

- [Spring AOP 공식 문서](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#aop)
- [AspectJ 프로그래밍 가이드](https://www.eclipse.org/aspectj/doc/released/progguide/index.html)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

_이 문서는 Motoo 프로젝트의 Spring AOP 활용 사례를 바탕으로 작성되었습니다._
