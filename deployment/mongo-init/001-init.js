// Executed on first Mongo container bootstrap only.
// Creates logical databases and service users (idempotent for first run).

const rootUser = process.env.MONGO_INITDB_ROOT_USERNAME || "jirathon";

print("[mongo-init] Root user available:", rootUser);

function ensureDb(dbName) {
  const dbRef = db.getSiblingDB(dbName);
  dbRef.createCollection("_bootstrap_marker");
  print("[mongo-init] Initialized DB:", dbName);
}

[
  "jirathon_auth",
  "jirathon_user",
  "jirathon_organization",
  "jirathon_contest",
  "jirathon_execution",
  "jirathon_payment",
  "jirathon_ai_evaluation",
  "notification_db"
].forEach(ensureDb);

print("[mongo-init] Database bootstrap complete.");
