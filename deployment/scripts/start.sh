#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

if [[ -f "$ROOT_DIR/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "$ROOT_DIR/.env"
  set +a
fi

if [[ -z "${MONGO_ROOT_PASSWORD:-}" ]]; then
  echo "[start] MONGO_ROOT_PASSWORD is required. Set it in .env or environment."
  exit 1
fi

if [[ -z "${AIEVAL_ADMIN_TOKEN:-}" ]]; then
  echo "[start] AIEVAL_ADMIN_TOKEN is required. Set it in .env or environment."
  exit 1
fi

if [[ "${1:-}" == "--build" ]]; then
  "$ROOT_DIR/deployment/scripts/build.sh"
fi

echo "[start] Starting platform..."
docker compose up -d

echo "[start] Waiting for core infra..."
sleep 8

echo "[start] Running database/bootstrap initialization..."
"$ROOT_DIR/deployment/scripts/db-init.sh" || true

echo "[start] Done."
docker compose ps
