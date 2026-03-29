#!/usr/bin/env bash
set -euo pipefail

# Deploy script for Plesk host using Docker Compose and pre-built images.
# It performs a rolling restart strategy service-by-service to reduce downtime.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

: "${DEPLOY_ENV:?DEPLOY_ENV is required (dev|staging|prod)}"
: "${REGISTRY:?REGISTRY is required}"
: "${IMAGE_NAMESPACE:?IMAGE_NAMESPACE is required}"
: "${IMAGE_TAG:?IMAGE_TAG is required}"
: "${REGISTRY_USERNAME:?REGISTRY_USERNAME is required}"
: "${REGISTRY_PASSWORD:?REGISTRY_PASSWORD is required}"
: "${MONGO_ROOT_PASSWORD:?MONGO_ROOT_PASSWORD is required}"
: "${AIEVAL_ADMIN_TOKEN:?AIEVAL_ADMIN_TOKEN is required}"

# Optional but recommended
MAIL_HOST="${MAIL_HOST:-smtp.mailtrap.io}"
MAIL_PORT="${MAIL_PORT:-587}"
MAIL_USERNAME="${MAIL_USERNAME:-}"
MAIL_PASSWORD="${MAIL_PASSWORD:-}"
AIEVAL_OPENAI_API_KEY="${AIEVAL_OPENAI_API_KEY:-}"
MONGO_ROOT_USER="${MONGO_ROOT_USER:-jirathon}"

# Persist environment for compose/script consistency
cat > "$ROOT_DIR/.env" <<EOF
DEPLOY_ENV=${DEPLOY_ENV}
REGISTRY=${REGISTRY}
IMAGE_NAMESPACE=${IMAGE_NAMESPACE}
IMAGE_TAG=${IMAGE_TAG}
MONGO_ROOT_USER=${MONGO_ROOT_USER}
MONGO_ROOT_PASSWORD=${MONGO_ROOT_PASSWORD}
AIEVAL_ADMIN_TOKEN=${AIEVAL_ADMIN_TOKEN}
AIEVAL_OPENAI_API_KEY=${AIEVAL_OPENAI_API_KEY}
MAIL_HOST=${MAIL_HOST}
MAIL_PORT=${MAIL_PORT}
MAIL_USERNAME=${MAIL_USERNAME}
MAIL_PASSWORD=${MAIL_PASSWORD}
EOF

echo "[deploy] Logging in to container registry ${REGISTRY}..."
echo "${REGISTRY_PASSWORD}" | docker login "${REGISTRY}" -u "${REGISTRY_USERNAME}" --password-stdin

COMPOSE_FILES=(
  -f docker-compose.yml
  -f deployment/docker-compose.ci.yml
)

SERVICES=(
  frontend
  auth-service
  user-service
  organization-service
  contest-service
  execution-service
  leaderboard-service
  payment-service
  notification-service
  ai-evaluation-service
)

echo "[deploy] Pulling image set for tag ${IMAGE_TAG}"
docker compose "${COMPOSE_FILES[@]}" pull "${SERVICES[@]}"

echo "[deploy] Ensuring infra is up (mongodb/redis/kafka stack)..."
docker compose "${COMPOSE_FILES[@]}" up -d mongodb redis zookeeper kafka reverse-proxy

# Rolling update strategy (container restart strategy)
echo "[deploy] Starting rolling app updates..."
for svc in "${SERVICES[@]}"; do
  echo "[deploy] -> updating ${svc}"
  docker compose "${COMPOSE_FILES[@]}" up -d --no-deps "${svc}"

done

echo "[deploy] Running bootstrap tasks (topics/indexes)..."
"$ROOT_DIR/deployment/scripts/db-init.sh" || true

echo "[deploy] Final service status"
docker compose "${COMPOSE_FILES[@]}" ps

echo "[deploy] Completed deployment."
