# Multi-stage build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="jirathon-team"
LABEL service="auth-service"

RUN addgroup -S jirathon && adduser -S jirathon -G jirathon

WORKDIR /app

COPY --from=builder /build/target/auth-service-*.jar app.jar

RUN chown -R jirathon:jirathon /app

USER jirathon

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
