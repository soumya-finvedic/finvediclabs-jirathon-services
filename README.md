# Jirathon

Jirathon is a microservices-based coding contest platform with secure code execution, AI-assisted evaluation, real-time leaderboards, payment processing, and notifications.

## Business Purpose

Jirathon is designed to become a skills-to-opportunity platform, not only a coding contest site.

High-level business outcomes:

- Help learners and professionals prove real skills through measurable challenge performance.
- Enable colleges, startups, and enterprises to run branded hiring and upskilling events quickly.
- Create monetizable contest operations via paid registrations, premium tracks, sponsorships, and B2B organization plans.
- Improve learner retention with real-time engagement loops (leaderboards, notifications, progression milestones).
- Build data-driven talent insights using execution, evaluation, and behavioral analytics.

## Latest Trending Ideas (2026) For This Codebase

These ideas are aligned with existing services so they can be implemented incrementally.

1. AI Career Copilot Journeys
- Use ai-evaluation-service to generate personalized learning plans after each contest.
- Add post-contest skill-gap reports and next-step challenge recommendations.

2. Skill Passport and Verifiable Badges
- Convert contest outcomes into role-based skill badges.
- Expose shareable public profiles and badge metadata for recruiters.

3. Team Hiring Tournaments for Companies
- Extend organization-service + contest-service for private company hiring events.
- Add recruiter dashboards for shortlisting based on live contest signals.

4. Creator Economy for Problem Setters
- Allow vetted creators to publish problem packs and earn revenue shares.
- Track usage, ratings, and earnings with payment-service and analytics events.

5. Live AI Interview Simulation
- Add timed voice/text interview rounds with rubric scoring.
- Reuse notification + websocket channels for live interviewer-like interactions.

6. Integrity and Trust Layer
- Add anti-cheat risk scoring using execution telemetry and behavior patterns.
- Introduce trust scores per submission/session for enterprise-grade assessments.

7. Campus-to-Corporate Pipeline
- Build college league seasons and inter-campus leaderboards.
- Offer employer-sponsored finals and direct recruitment funnels.

8. Outcome Analytics for B2B Customers
- Provide organization-level insights: participation, completion, skill distribution, hiring conversion.
- Package as subscription tiers for institutions and recruiting teams.

## Architecture

### Service Map

- frontend (Vue 3): user/admin web UI
- auth-service: authentication, authorization, JWT/OAuth flows
- user-service: user profile and user domain operations
- organization-service: organization management
- contest-service: contest lifecycle, registrations, and problem orchestration
- execution-service: sandboxed code execution
- ai-evaluation-service: descriptive answer/transcript/file evaluation
- leaderboard-service: ranking and realtime leaderboard updates
- payment-service: paid registration, coupons, refunds, webhooks
- notification-service: Kafka-driven notifications, email, websocket pushes

### Infrastructure Components

- reverse-proxy (Nginx): edge routing for frontend/API/WebSocket
- mongodb: primary document datastore
- redis: cache and ranking support
- zookeeper + kafka: event streaming backbone
- kafka-ui: Kafka observability UI

### Request Routing

- `/` -> frontend
- `/api/*` -> backend microservices (path-based)
- `/ws/*` -> websocket endpoints

Detailed proxy and route definitions are in:
- deployment/nginx/nginx.conf

### Event-Driven Flows

Examples:

1. Payment success event -> notification-service email + websocket push
2. Score update event -> leaderboard-service recompute + websocket broadcast
3. Contest lifecycle events -> participant notifications

Kafka topics are initialized by:
- deployment/scripts/db-init.sh

## Repository Layout

- backend/
  - auth-service/
  - user-service/
  - organization-service/
  - contest-service/
  - execution-service/
  - leaderboard-service/
  - payment-service/
  - notification-service/
  - ai-evaluation-service/
- frontend/
- deployment/
  - nginx/nginx.conf
  - scripts/
  - mongo-init/
  - PLESK_DEPLOYMENT.md
- docker-compose.yml
- build.sh
- start.sh
- stop.sh
- db-init.sh

## Setup

### Prerequisites

- Docker 24+
- Docker Compose v2+
- Java 17 + Maven (for local jar builds)
- Node 20+ (for local frontend work)
- Linux host for production deployment (Plesk server)

### Environment Variables

Copy and customize values:

```bash
cp deployment/.env.example .env
```

Important variables:

- MONGO_ROOT_USER
- MONGO_ROOT_PASSWORD
- MAIL_HOST
- MAIL_PORT
- MAIL_USERNAME
- MAIL_PASSWORD
- AIEVAL_OPENAI_API_KEY
- AIEVAL_ADMIN_TOKEN

Required (must be non-empty):

- MONGO_ROOT_PASSWORD
- AIEVAL_ADMIN_TOKEN

## Run Commands

### Fast Start (build + up)

```bash
./start.sh --build
```

### Build Images Only

```bash
./build.sh
```

### Start Existing Images

```bash
./start.sh
```

### Initialize Topics/Indexes

```bash
./db-init.sh
```

### Stop Stack

```bash
./stop.sh
```

### Stop and Purge Volumes

```bash
./stop.sh --purge
```

### Inspect Running Services

```bash
docker compose ps
docker compose logs -f reverse-proxy
```

### Endpoints (default)

- App entrypoint (via Plesk reverse proxy): https://your-domain
- Local Docker Nginx bind for Plesk forwarding: http://127.0.0.1:8080
- Kafka UI (optional ops profile): http://127.0.0.1:8089

Start ops-only tooling:

```bash
docker compose --profile ops up -d kafka-ui
```

## Deployment via Plesk

For complete step-by-step instructions, see:
- deployment/PLESK_DEPLOYMENT.md
- deployment/GITHUB_PLESK_CICD_SETUP.md

### Quick Summary

1. Upload project to server, for example:
   - /var/www/vhosts/<domain>/jirathon
2. Configure environment variables in Plesk Docker settings.
3. Deploy compose stack from docker-compose.yml.
4. Configure Plesk domain and DNS.
5. Issue SSL with Let's Encrypt.
6. Add Plesk reverse proxy directives to forward traffic to 127.0.0.1:8080.

### Plesk Reverse Proxy Pattern

Plesk Nginx/Apache remains public edge and forwards to Docker reverse-proxy container:

```nginx
location / {
  proxy_pass http://127.0.0.1:8080;
  proxy_set_header Host $host;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto $scheme;
}
```

## Scaling Guide

### Horizontal Scaling Candidates

Best stateless scaling targets:

- frontend
- contest-service
- execution-service
- leaderboard-service
- notification-service
- ai-evaluation-service

Scale with compose:

```bash
docker compose up -d \
  --scale frontend=2 \
  --scale contest-service=2 \
  --scale execution-service=2 \
  --scale leaderboard-service=2 \
  --scale notification-service=2
```

### Stateful Components

- MongoDB: start with single node; move to replica set for HA
- Redis: add replica/sentinel for failover
- Kafka: move from single broker to 3+ brokers for production resilience

### WebSocket Scale Considerations

- Use sticky sessions at proxy level, or
- Use shared pub/sub backplane (Redis pub/sub) for fan-out across replicas

### Recommended Production Progression

1. Vertical scale single host (CPU/RAM tuning)
2. Horizontal scale stateless services
3. Introduce HA data services (Mongo replica set, Redis sentinel, multi-broker Kafka)
4. Add centralized observability and alerting

## Logging and Observability

### Current Logging

- Docker json-file logging with rotation (`20m`, 5 files)
- Nginx logs persisted to:
  - logs/nginx/

### Recommended Enhancements

- Structured JSON logs in Java/Python services
- Central log shipping with Fluent Bit/Filebeat to ELK/OpenSearch/Loki
- Metrics stack (Prometheus + Grafana)
- Alerting for service health, queue lag, and latency

## Testing and QA

Testing assets are available in:

- testing/TESTING_STRATEGY.md
- testing/postman/Jirathon.postman_collection.json
- testing/postman/Jirathon.local.postman_environment.json
- testing/newman/run-newman.sh

## Troubleshooting

### Compose startup issues

```bash
docker compose config
docker compose ps
docker compose logs -f
```

### Kafka topic bootstrap rerun

```bash
./db-init.sh
```

### Rebuild specific service

```bash
docker compose build payment-service
docker compose up -d payment-service
```

## License

Internal project. Add your organization license policy as needed.
