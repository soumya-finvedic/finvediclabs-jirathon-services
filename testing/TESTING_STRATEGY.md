# Jirathon Testing Strategy

## Objectives

- Catch regressions early across microservices.
- Validate service contracts and edge cases before deployment.
- Verify end-to-end API behavior and WebSocket/event flows.
- Support CI with fast feedback and promotion gates.

## Testing Pyramid for Jirathon

1. Unit Tests (fast, isolated): JUnit + Mockito.
2. Integration Tests (service wiring and persistence): Spring Boot Test + MockMvc + real containers where needed.
3. API Contract and Workflow Tests: Postman/Newman collections.
4. End-to-end smoke in deployed environment: script-driven health and key user journeys.

## 1) JUnit Test Strategy

### Scope

- Service-layer business rules:
  - payment discount and coupon validation
  - leaderboard ranking logic
  - auth token lifecycle and OTP workflows
  - notification preference enforcement
- Utility and mapper logic.

### Guidelines

- Use JUnit 5 and Mockito.
- Follow Arrange-Act-Assert.
- Keep tests deterministic (no system clock randomness without control).
- Prefer explicit fixtures/builders over shared mutable state.

### Minimum Targets

- 80%+ line coverage for core business classes.
- 90%+ branch coverage for payment/coupon/refund rules.

### Implemented Sample

- Payment coupon logic unit tests:
  - [payment-service/src/test/java/com/jirathon/payment/service/CouponServiceTest.java](payment-service/src/test/java/com/jirathon/payment/service/CouponServiceTest.java)

## 2) Integration Test Strategy

### Scope

- Controller + validation + serialization + exception handling.
- Service integration with Mongo/Redis/Kafka in dedicated profile (CI stage).
- Webhook endpoints and request signing behavior.

### Layers

- API integration (MockMvc): verifies endpoint behavior and schema.
- Data integration (Mongo/Redis): repository/index behavior.
- Messaging integration (Kafka): topic publish/consume and side effects.

### Environment

- `test` Spring profile.
- In CI: Docker-based dependencies via compose or Testcontainers.
- Run integration tests in separate Maven phase to keep local loop fast.

### Implemented Sample

- Payment API integration test:
  - [payment-service/src/test/java/com/jirathon/payment/integration/PaymentControllerIntegrationTest.java](payment-service/src/test/java/com/jirathon/payment/integration/PaymentControllerIntegrationTest.java)

## 3) API Test Strategy

### Scope

- Public REST endpoints for auth, contests, execution, leaderboard, payment, notification.
- Positive, negative, and auth boundary cases.
- Webhook and idempotency checks.

### API Test Categories

1. Happy paths:
   - login, fetch contests, initiate payment, leaderboard fetch.
2. Validation failures:
   - malformed payloads and missing required fields.
3. Security/auth:
   - unauthorized and forbidden access.
4. Stateful workflows:
   - payment initiate -> webhook callback -> payment status read.

### Tooling

- Postman collection for local and staging runs.
- Newman in CI/CD pipeline.

## 4) Postman and Newman

### Files

- Collection:
  - [testing/postman/Jirathon.postman_collection.json](testing/postman/Jirathon.postman_collection.json)
- Local environment:
  - [testing/postman/Jirathon.local.postman_environment.json](testing/postman/Jirathon.local.postman_environment.json)
- Newman runner script:
  - [testing/newman/run-newman.sh](testing/newman/run-newman.sh)

### Run

```bash
cd testing/newman
./run-newman.sh
```

## 5) CI Pipeline Recommendation

1. Stage `lint`:
   - Java checkstyle/spotbugs (if enabled), frontend lint.
2. Stage `unit`:
   - `mvn test` on each service.
3. Stage `integration`:
   - Start infra (Mongo, Redis, Kafka), run integration suites.
4. Stage `api`:
   - Deploy ephemeral stack, run Newman collection.
5. Stage `quality-gate`:
   - Coverage threshold + test pass required for merge.

## 6) Test Data and Isolation

- Use deterministic fixture data with clear IDs.
- Isolate tests by resetting DB state between integration runs.
- For Kafka, use unique consumer groups in test profile.
- Avoid external payment gateway calls in tests; mock PayU edge and use webhook simulation.

## 7) Suggested Expansion Backlog

1. Add Testcontainers for Mongo/Redis/Kafka integration suites.
2. Add consumer integration tests for leaderboard and notification Kafka handlers.
3. Add WebSocket integration tests for `/ws-leaderboard` and `/ws-notifications`.
4. Add contract tests between frontend API client and backend schema.
5. Add load test profiles for execution and leaderboard hot paths.
