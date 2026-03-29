#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COLLECTION="$SCRIPT_DIR/../postman/Jirathon.postman_collection.json"
ENV_FILE="$SCRIPT_DIR/../postman/Jirathon.local.postman_environment.json"

echo "[newman] Running Jirathon collection..."

if ! command -v newman >/dev/null 2>&1; then
  echo "[newman] newman not found. Install with: npm i -g newman"
  exit 1
fi

newman run "$COLLECTION" \
  -e "$ENV_FILE" \
  --reporters cli,json \
  --reporter-json-export "$SCRIPT_DIR/newman-report.json"

echo "[newman] Completed. Report: $SCRIPT_DIR/newman-report.json"
