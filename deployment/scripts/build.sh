#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

SERVICES=(
  backend/auth-service
  backend/user-service
  backend/organization-service
  backend/contest-service
  backend/execution-service
  backend/leaderboard-service
  backend/payment-service
  backend/notification-service
)

echo "[build] Packaging Java services with Maven..."
for service in "${SERVICES[@]}"; do
  echo "[build] -> $service"
  (cd "$service" && mvn -q -DskipTests clean package)
done

echo "[build] Building Docker images..."
docker compose build

echo "[build] Completed successfully."
