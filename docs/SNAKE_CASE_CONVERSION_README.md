# Snake Case ↔ Camel Case 자동 변환 설정

## 📋 개요

Spring Boot 애플리케이션에서 프론트엔드는 `snake_case`로 API 요청을 보내고, 백엔드 내부에서는 `camelCase`로 처리하도록 자동 변환을 설정합니다.

## 🎯 목표

- **프론트엔드**: `snake_case` 형식으로 JSON 요청 전송
- **백엔드 내부**: `camelCase` 형식으로 처리
- **Redis 저장**: `snake_case` 형식으로 직렬화
- **자동 변환**: 투명한 변환 처리

## ⚙️ 설정 방법

### 1. build.gradle 의존성 추가

```gradle
dependencies {
    // Jackson snake_case 지원
    implementation 'com.fasterxml.jackson.core:jackson-databind'

    // Jackson Java 8 날짜/시간 지원
    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310'
}
```

### 2. application.yml 설정

```yaml
spring:
  jackson:
    property-naming-strategy: SNAKE_CASE
    serialization:
      write-dates-as-timestamps: false
      write-dates-with-zone-id: false
    deserialization:
      fail-on-unknown-properties: false
    default-property-inclusion: non_null
```

### 3. JacksonConfig.java 생성

```java
package com.hsu_mafia.motoo.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
}
```

### 4. RedisConfig.java 수정

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Jackson ObjectMapper 설정 (snake_case 지원)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Key는 String으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value는 JSON으로 직렬화 (snake_case 지원)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        template.afterPropertiesSet();
        return template;
    }
}
```

## 🧪 테스트 방법

### 1. Docker 환경 실행

```bash
# Docker 컨테이너 실행
docker compose up --build -d

# 서비스 상태 확인
curl http://localhost:8080/actuator/health
```

### 2. snake_case로 주문 요청

```bash
# snake_case 형식으로 주문 생성
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "stock_id": "AAPL",
    "order_type": "BUY",
    "quantity": 10,
    "price": 150000,
    "market_order": false
  }'
```

### 3. Redis 큐 확인

```bash
# Redis CLI 접속
docker exec -it redis-cli redis-cli -h redis

# 큐 크기 확인
LLEN order:queue:pending

# 큐 내용 확인
LRANGE order:queue:pending 0 -1
```

## 📊 테스트 결과

### 요청 형식 (snake_case)

```json
{
  "stock_id": "AAPL",
  "order_type": "BUY",
  "quantity": 10,
  "price": 150000,
  "market_order": false
}
```

### Spring 내부 처리 (camelCase)

```java
// OrderRequest.java
public class OrderRequest {
    private String stockId;        // camelCase로 매핑
    private OrderType orderType;   // camelCase로 매핑
    private Long quantity;
    private Long price;
    private Boolean marketOrder;   // camelCase로 매핑
}
```

### Redis 저장 형식 (snake_case)

```json
{
  "order_id": 7,
  "user_id": 1,
  "stock_code": "AAPL",
  "order_type": "BUY",
  "quantity": 10,
  "price": 150000,
  "created_at": "2025-07-29T22:03:54.696028302",
  "market_order": false
}
```

## 🔄 변환 흐름

```
프론트엔드 (snake_case)
        ↓
    HTTP 요청
        ↓
Spring Boot (자동 변환)
        ↓
   내부 처리 (camelCase)
        ↓
   Redis 저장 (snake_case)
```

## ✅ 검증 포인트

1. **요청 처리**: snake_case JSON이 camelCase DTO로 정상 변환
2. **내부 로직**: camelCase 필드명으로 정상 처리
3. **Redis 저장**: snake_case로 직렬화되어 저장
4. **날짜 처리**: LocalDateTime이 ISO 문자열로 변환
5. **에러 없음**: 변환 과정에서 예외 발생하지 않음

## 🚨 주의사항

1. **DTO 필드명**: 내부 DTO는 camelCase로 정의
2. **API 문서**: 프론트엔드 개발자에게 snake_case 사용 안내
3. **테스트**: 양방향 변환 모두 테스트 필요
4. **일관성**: 모든 API에서 동일한 규칙 적용

## 📝 예시 코드

### OrderRequest.java (camelCase)

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String stockId;        // snake_case "stock_id"와 매핑
    private OrderType orderType;   // snake_case "order_type"과 매핑
    private Long quantity;
    private Long price;
    private Boolean marketOrder;   // snake_case "market_order"와 매핑
}
```

### API 요청 (snake_case)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "stock_id": "AAPL",
    "order_type": "BUY",
    "quantity": 10,
    "price": 150000,
    "market_order": false
  }'
```

### Redis 저장 결과 (snake_case)

```json
{
  "order_id": 7,
  "user_id": 1,
  "stock_code": "AAPL",
  "order_type": "BUY",
  "quantity": 10,
  "price": 150000,
  "created_at": "2025-07-29T22:03:54.696028302",
  "market_order": false
}
```

## 🎉 결론

이 설정을 통해 프론트엔드와 백엔드 간의 네이밍 컨벤션 차이를 투명하게 처리할 수 있습니다. 프론트엔드는 snake_case를 사용하고, 백엔드는 camelCase를 유지하면서도 자동으로 변환이 이루어집니다.
