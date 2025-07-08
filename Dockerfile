FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

COPY newrelic/newrelic.jar newrelic.jar
COPY newrelic/newrelic.yml newrelic.yml
COPY src/main/resources/application.yml /app/config/application.yml

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:newrelic.jar", "-jar", "build/libs/motoo-0.0.1-SNAPSHOT.jar", "--spring.config.location=file:/app/config/application.yml"]
