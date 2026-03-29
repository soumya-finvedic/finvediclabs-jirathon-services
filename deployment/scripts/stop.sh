#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

MODE="${1:-keep-volumes}"

if [[ "$MODE" == "--purge" ]]; then
  echo "[stop] Stopping and removing containers, networks, and volumes..."
  docker compose down -v --remove-orphans
else
  echo "[stop] Stopping and removing containers and networks (keeping volumes)..."
  docker compose down --remove-orphans
fi

echo "[stop] Done."
