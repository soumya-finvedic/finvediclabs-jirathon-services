FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="jirathon-team"
LABEL service="organization-service"

RUN addgroup -S jirathon && adduser -S jirathon -G jirathon

WORKDIR /app

COPY target/organization-service-1.0.0.jar app.jar

RUN chown -R jirathon:jirathon /app

USER jirathon

EXPOSE 8084

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8084/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
