# GitHub Actions -> Plesk CI/CD Setup

This guide connects GitHub Actions to your Plesk server for automated Docker Compose deployments.

## 1) Prerequisites on Plesk Server

1. Docker extension enabled in Plesk.
2. SSH access enabled for deployment user.
3. Repository cloned on server path, for example:
   - `/var/www/vhosts/your-domain/jirathon`
4. Deployment user can run Docker commands.
5. Ensure these scripts are executable:

```bash
cd /var/www/vhosts/your-domain/jirathon
chmod +x deployment/scripts/*.sh
```

## 2) GitHub Repository Secrets

Add in GitHub -> Settings -> Secrets and variables -> Actions -> Secrets:

- `DOCKER_REGISTRY_USERNAME`
- `DOCKER_REGISTRY_PASSWORD`
- `PLESK_SSH_HOST`
- `PLESK_SSH_USER`
- `PLESK_SSH_PRIVATE_KEY`
- `PLESK_SSH_PORT` (optional)
- `MONGO_ROOT_PASSWORD`
- `AIEVAL_ADMIN_TOKEN`
- `AIEVAL_OPENAI_API_KEY` (optional)
- `MAIL_USERNAME` (optional)
- `MAIL_PASSWORD` (optional)

## 3) GitHub Repository Variables

Add in GitHub -> Settings -> Secrets and variables -> Actions -> Variables:

- `REGISTRY` (example: `docker.io`)
- `IMAGE_NAMESPACE` (example: `your-dockerhub-namespace`)
- `PLESK_APP_PATH` (example: `/var/www/vhosts/your-domain/jirathon`)
- `MONGO_ROOT_USER` (default: `jirathon`)
- `MAIL_HOST` (example: `smtp.mailtrap.io`)
- `MAIL_PORT` (default: `587`)

## 4) Environment Protection (recommended)

Create GitHub Environments:

- `dev`
- `staging`
- `prod`

For `prod`, enforce:

- Required reviewers
- Deployment branch rules (main only)

## 5) Workflow Behavior

Defined in:
- `.github/workflows/main.yml`

Branch to environment mapping:

- `develop` -> `dev`
- `staging` -> `staging`
- `main` -> `prod`

Pipeline stages:

1. Build Stage
   - Maven package for Java services
   - Vue build
   - FastAPI import/syntax smoke
2. Test Stage
   - JUnit tests
   - frontend lint as quality gate
3. Docker Stage
   - Build all images and tag with:
     - `latest`
     - `<commit-short-sha>`
4. Push Stage
   - Push all images to registry
5. Deploy Stage
   - SSH to Plesk
   - Pull latest repo
   - Run `deployment/scripts/deploy.sh`

## 6) Zero-Downtime Strategy

`deployment/scripts/deploy.sh` uses a rolling container restart strategy:

- Pull new images first
- Bring infra up
- Update each app service one-by-one with:
  - `docker compose up -d --no-deps <service>`

This reduces full platform downtime versus replacing all services at once.

## 7) Monitoring and Health

- Docker `restart: unless-stopped` is configured in compose.
- Service and edge health checks are included via Dockerfile/compose where available.
- You can verify after deployment:

```bash
docker compose ps
curl -fsS http://127.0.0.1:8080/health
```

## 8) Manual Rollback

If needed, redeploy a previous image tag:

```bash
export IMAGE_TAG=<previous-tag>
deployment/scripts/deploy.sh
```

## 9) Troubleshooting

- Workflow logs: GitHub Actions run details.
- Server logs:

```bash
cd /var/www/vhosts/your-domain/jirathon
docker compose logs -f reverse-proxy
docker compose logs -f auth-service
```
