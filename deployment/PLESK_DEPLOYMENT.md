# Jirathon Deployment on Plesk (Docker)

This guide deploys the full Jirathon stack on a Plesk server with Docker support.

## 1) Prerequisites

- Plesk with Docker extension enabled
- Domain/subdomain pointed to your server
- Server ports open: `80`, `443`
- Git installed on server (optional but recommended)
- At least 8 GB RAM recommended

## 2) Files Included

- Root compose: `docker-compose.yml`
- Reverse proxy config: `deployment/nginx/nginx.conf`
- Ops scripts:
  - `deployment/scripts/build.sh`
  - `deployment/scripts/start.sh`
  - `deployment/scripts/stop.sh`
  - `deployment/scripts/db-init.sh`
- Mongo bootstrap: `deployment/mongo-init/001-init.js`
- Frontend Dockerfile: `frontend/Dockerfile`
- Existing Dockerfiles used for all backend services.

## 3) Plesk Docker Deployment Steps

### Option A: Plesk GUI + Docker Compose plugin

1. Upload project to server, for example to `/var/www/vhosts/<domain>/jirathon`.
2. In Plesk, open **Docker**.
3. Add/Enable Docker Compose project and point to project folder.
4. Select `docker-compose.yml` from root.
5. Set environment variables in Plesk:
   - `MONGO_ROOT_USER`
   - `MONGO_ROOT_PASSWORD`
   - `MAIL_HOST`
   - `MAIL_PORT`
   - `MAIL_USERNAME`
   - `MAIL_PASSWORD`
   - `AIEVAL_OPENAI_API_KEY`
   - `AIEVAL_ADMIN_TOKEN`
7. Ensure required secrets are non-empty before deploy:
  - `MONGO_ROOT_PASSWORD`
  - `AIEVAL_ADMIN_TOKEN`
6. Deploy stack.

### Option B: SSH on Plesk server

```bash
cd /var/www/vhosts/<domain>/jirathon
chmod +x deployment/scripts/*.sh
./deployment/scripts/start.sh --build
```

## 4) Domain Setup in Plesk

1. Go to **Websites & Domains**.
2. Add domain or subdomain (for example `app.example.com`).
3. Ensure DNS A record points to server IP.
4. Keep Plesk Apache/Nginx service enabled.

## 5) SSL with Let's Encrypt

1. In Plesk domain panel, click **Let's Encrypt**.
2. Issue certificate for domain and `www` (if used).
3. Enable **Secure the domain** and redirect HTTP to HTTPS.
4. Keep auto-renew enabled.

## 6) Reverse Proxy Setup in Plesk

Use one of these patterns:

### Pattern 1 (recommended): Plesk forwards to Docker Nginx

- Keep Docker `reverse-proxy` service published on `127.0.0.1:8080` (change compose port mapping if desired).
- In Plesk domain **Apache & Nginx Settings**, add additional nginx directives:

```nginx
location / {
  proxy_pass http://127.0.0.1:8080;
  proxy_set_header Host $host;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto $scheme;
}
```

### Pattern 2: Expose Docker Nginx on :80/:443 directly

- Use only if this host is dedicated for this stack and does not conflict with Plesk web services.

## 7) URL Routing Behavior

Configured in `deployment/nginx/nginx.conf`:

- `/` -> frontend
- `/api` -> backend services (path-based routing)
- `/ws` -> websocket routes
  - `/ws/leaderboard` -> leaderboard websocket endpoint
  - `/ws/notifications` -> notification websocket endpoint

## 8) Kafka + Redis + DB Setup

Compose includes:

- `mongodb` + persistent volume
- `redis` + persistent volume
- `zookeeper`
- `kafka`
- `kafka-ui` (optional web UI)

Initialize infra after first run:

```bash
./deployment/scripts/db-init.sh
```

Run Kafka UI only when needed (ops profile):

```bash
docker compose --profile ops up -d kafka-ui
```

## 9) Logging Strategy

Current stack logging:

- Docker `json-file` driver with rotation (`20m x 5` files)
- Reverse proxy logs mounted to `./logs/nginx`

Recommended production enhancement:

- Add log shipping (Fluent Bit/Vector/Filebeat) to ELK/OpenSearch/Loki
- Add structured app logs (JSON encoder in Spring/Python)

## 10) Scaling Strategy

### Horizontal scale candidates

- `contest-service`, `execution-service`, `leaderboard-service`, `notification-service`, `ai-evaluation-service`
- Frontend can scale to multiple replicas behind nginx

### Commands

```bash
docker compose up -d --scale contest-service=2 --scale execution-service=2 --scale leaderboard-service=2
```

### Notes

- Keep services stateless; store state in Mongo/Redis/Kafka.
- WebSocket scaling needs sticky sessions or shared pub/sub backplane (Redis pub/sub is recommended).
- For high availability Kafka, move to 3-broker cluster and external volumes.

## 11) Operations

- Build images: `./deployment/scripts/build.sh`
- Start stack: `./deployment/scripts/start.sh`
- Stop stack: `./deployment/scripts/stop.sh`
- Full cleanup: `./deployment/scripts/stop.sh --purge`

## 12) Health Checks

- Public health: `GET /health`
- Service health uses individual container probes where defined.

