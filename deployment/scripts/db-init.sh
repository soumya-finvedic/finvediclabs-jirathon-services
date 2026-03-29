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

MONGO_USER="${MONGO_ROOT_USER:-jirathon}"
MONGO_PASS="${MONGO_ROOT_PASSWORD:-}"

if [[ -z "$MONGO_PASS" ]]; then
  echo "[db-init] MONGO_ROOT_PASSWORD is required. Set it in .env or environment."
  exit 1
fi

if ! docker compose ps mongodb | grep -q "Up"; then
  echo "[db-init] mongodb is not running. Start stack first."
  exit 1
fi

echo "[db-init] Creating Kafka topics (idempotent)..."
docker compose exec -T kafka kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic payment.success --partitions 3 --replication-factor 1 || true
docker compose exec -T kafka kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic registration.confirmed --partitions 3 --replication-factor 1 || true
docker compose exec -T kafka kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic contest.start --partitions 3 --replication-factor 1 || true
docker compose exec -T kafka kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic contest.end --partitions 3 --replication-factor 1 || true
docker compose exec -T kafka kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic leaderboard-score-events --partitions 6 --replication-factor 1 || true

echo "[db-init] Preparing Mongo indexes (idempotent)..."
docker compose exec -T mongodb mongosh --username "$MONGO_USER" --password "$MONGO_PASS" --authenticationDatabase admin <<'EOF'
use jirathon_payment;
db.payment_transactions.createIndex({ registrationId: 1 }, { unique: true });
db.payment_transactions.createIndex({ payuTxnId: 1 }, { unique: true });

use notification_db;
db.notifications.createIndex({ userId: 1, createdAt: -1 });
db.notifications.createIndex({ expiresAt: 1 }, { expireAfterSeconds: 0 });

use jirathon_ai_evaluation;
db.evaluations.createIndex({ status: 1, created_at: -1 });
EOF

echo "[db-init] Done."
