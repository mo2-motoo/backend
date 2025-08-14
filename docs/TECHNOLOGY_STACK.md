# Technology Stack & Versions

This document outlines the technology stack, including programming languages, frameworks, and key libraries, used in the Motoo backend project. Version information is based on the `build.gradle` file.

## Core Technologies

- **Java**: `21`
- **Spring Boot**: `3.5.0`

## Key Libraries and Dependencies

### Spring Boot Starters

- `spring-boot-starter-web`: Provides support for building web applications, including RESTful applications, using Spring MVC.
- `spring-boot-starter-validation`: Provides support for bean validation with Hibernate Validator.
- `spring-boot-starter-aop`: Provides support for aspect-oriented programming (AOP) with Spring AOP and AspectJ.
- `spring-boot-starter-data-jpa`: Provides support for using Java Persistence API (JPA) with Spring Data and Hibernate.
- `spring-boot-starter-data-redis`: Provides support for Redis key-value data store with Spring Data Redis.
- `spring-boot-starter-actuator`: Provides production-ready features to help you monitor and manage your application.
- `spring-boot-starter-test`: Provides support for testing Spring Boot applications with libraries including JUnit, Hamcrest, and Mockito.

### Database

- `postgresql`: PostgreSQL JDBC driver.

### Development Tools

- `lombok`: Reduces boilerplate code for model/data objects (e.g., getters, setters, constructors).
- `mapstruct`: A code generator that simplifies the implementation of mappings between Java bean types.

### API and Documentation

- `jackson-module-parameter-names`, `jackson-datatype-jsr310`, `jackson-databind`: Provides support for JSON serialization and deserialization, including support for Java 8 date/time types and
  snake_case to camelCase mapping.
- `springdoc-openapi-starter-webmvc-ui`: Automatically generates API documentation for Spring Boot projects.

### Monitoring

- `micrometer-registry-prometheus`: Micrometer registry for Prometheus.

