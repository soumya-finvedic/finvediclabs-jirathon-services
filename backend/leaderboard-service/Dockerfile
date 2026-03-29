FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="jirathon-team"
LABEL service="leaderboard-service"

RUN addgroup -S jirathon && adduser -S jirathon -G jirathon

WORKDIR /app

COPY target/leaderboard-service-1.0.0.jar app.jar

RUN chown -R jirathon:jirathon /app

USER jirathon

EXPOSE 8087

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8087/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
