#!/usr/bin/env bash
set -euo pipefail

# Push Docker images for all deployable services.
# Required envs:
#   REGISTRY, IMAGE_NAMESPACE, IMAGE_TAG

: "${REGISTRY:?REGISTRY is required}"
: "${IMAGE_NAMESPACE:?IMAGE_NAMESPACE is required}"
: "${IMAGE_TAG:?IMAGE_TAG is required}"

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
  echo "[docker-push] Pushing ${image_base}:${IMAGE_TAG}"
  docker push "${image_base}:${IMAGE_TAG}"

  echo "[docker-push] Pushing ${image_base}:latest"
  docker push "${image_base}:latest"
done

echo "[docker-push] Completed pushing all images."
