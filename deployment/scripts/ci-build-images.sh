#!/usr/bin/env bash
set -euo pipefail

# Build Docker images for all deployable services.
# Required envs:
#   REGISTRY, IMAGE_NAMESPACE, IMAGE_TAG

: "${REGISTRY:?REGISTRY is required}"
: "${IMAGE_NAMESPACE:?IMAGE_NAMESPACE is required}"
: "${IMAGE_TAG:?IMAGE_TAG is required}"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

SERVICES=(
  "auth-service"
  "user-service"
  "organization-service"
  "contest-service"
  "execution-service"
  "leaderboard-service"
  "payment-service"
  "notification-service"
  "ai-evaluation-service"
  "frontend"
)

for service in "${SERVICES[@]}"; do
  image_base="${REGISTRY}/${IMAGE_NAMESPACE}/${service}"
  echo "[docker-build] Building ${service} -> ${image_base}:{latest,${IMAGE_TAG}}"

  if [[ "$service" == "frontend" ]]; then
    dockerfile_path="frontend/Dockerfile"
    context_path="frontend"
  else
    dockerfile_path="backend/${service}/Dockerfile"
    context_path="backend/${service}"
  fi

  docker build \
    -f "${dockerfile_path}" \
    -t "${image_base}:latest" \
    -t "${image_base}:${IMAGE_TAG}" \
    "${context_path}"
done

echo "[docker-build] Completed building all images."
