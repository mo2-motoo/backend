FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

COPY src/main/resources/application.yml /app/config/application.yml

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "build/libs/motoo-0.0.1-SNAPSHOT.jar", "--spring.config.location=file:/app/config/application.yml"]
