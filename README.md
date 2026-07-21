# InclusiveConnect

> **Connect Beyond Limits** — An accessible professional networking platform connecting differently-abled talent with inclusive employers.

InclusiveConnect is a production-ready full-stack web application that empowers differently-abled professionals with a modern, accessible platform for networking, job discovery, real-time communication, and career growth.

---

## Application Screenshots

| Landing Page | Login | Candidate Dashboard |
|---|---|---|
| ![Landing](Application%20Screenshots/landing-page.png) | ![Login](Application%20Screenshots/login.png) | ![Dashboard](Application%20Screenshots/candidate-dashboard.png) |

| Jobs | Network | Chat | Notifications |
|---|---|---|---|
| ![Jobs](Application%20Screenshots/job-list.png) | ![Network](Application%20Screenshots/network.png) | ![Chat](Application%20Screenshots/chat.png) | ![Notifications](Application%20Screenshots/notifications.png) |

---

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | Angular 17, TypeScript, Angular Material, Tailwind CSS |
| Backend | Java 17, Spring Boot 3, Spring Security, Spring Data JPA |
| Real-Time | Spring WebSocket, STOMP |
| Database | MySQL 8 |
| Auth | JWT (access + refresh tokens) |
| File Storage | Cloudinary |
| API Docs | SpringDoc OpenAPI / Swagger |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Web Server | Nginx (reverse proxy + SPA server) |

---

## Key Features

- JWT authentication with refresh tokens, email verification, password reset
- Candidate profiles with skills, experience, education, resume upload
- Employer portal with company profiles, job posting, and applicant tracking
- Professional networking — connection requests, discovery, search
- Real-time one-to-one messaging with WebSocket/STOMP
- Live notifications with typing indicators
- Accessibility settings (high contrast, large text, keyboard nav, screen reader support)
- Admin dashboard for user and employer management
- Role-based access control (CANDIDATE / EMPLOYER / ADMIN)

---

## Project Structure

```
InclusiveConnect/
├── InclusiveConnectBackend/        # Spring Boot REST API
│   ├── src/main/java/              # Application source
│   ├── src/main/resources/
│   │   ├── application.yaml        # Base config (profile = dev)
│   │   ├── application-dev.yml     # Local development
│   │   └── application-prod.yml    # Production (all secrets via env vars)
│   └── Dockerfile                  # Multi-stage build → JRE runtime
│
├── InclusiveConnectFrontend/       # Angular SPA
│   ├── src/
│   │   ├── app/                    # Features, core, shared, layouts
│   │   └── environments/           # environment.ts / environment.prod.ts
│   ├── nginx.conf                  # SPA routing + API proxy + gzip
│   └── Dockerfile                  # Node build → Nginx runtime
│
├── database/
│   └── init.sql                    # MySQL init: charset + seed roles
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml               # GitHub Actions CI/CD pipeline
│
├── docker-compose.yml              # Full stack: mysql + backend + frontend
├── .env.example                    # Environment variable template
└── README.md
```

---

## Quick Start — Docker Compose (Recommended)

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) ≥ 24
- [Docker Compose](https://docs.docker.com/compose/install/) ≥ 2.20
- A [Cloudinary](https://cloudinary.com) account (free tier works)
- A Gmail account with an [App Password](https://myaccount.google.com/apppasswords) for SMTP

### 1. Clone

```bash
git clone https://github.com/MANIKANDAN-17K/InclusiveConnect.git
cd InclusiveConnect
```

### 2. Configure environment

```bash
cp .env.example .env
```

Open `.env` and fill in every variable:

```env
MYSQL_ROOT_PASSWORD=your_strong_root_password
MYSQL_PASSWORD=your_strong_app_password

# Generate with: openssl rand -base64 64
JWT_SECRET=your_very_long_random_secret

MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=your_gmail_app_password

CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name
```

### 3. Start all services

```bash
docker compose up -d
```

Docker will:
1. Pull MySQL 8 and run `database/init.sql`
2. Build the Spring Boot backend (Maven compile inside Docker)
3. Build the Angular frontend (npm build inside Docker) and serve it via Nginx

Watch startup logs:

```bash
docker compose logs -f
```

### 4. Access the application

| Service | URL |
|---|---|
| Application | http://localhost |
| Backend API | http://localhost/api/v1 |
| API Docs (dev only) | http://localhost:8080/swagger-ui.html |

### 5. Stop / tear down

```bash
# Stop containers (keep volumes)
docker compose down

# Stop and remove all data volumes (full reset)
docker compose down -v
```

---

## Local Development (without Docker)

### Backend

Requirements: Java 17, Maven 3.8+, MySQL 8 running locally.

```bash
cd InclusiveConnectBackend

# Copy your local credentials into .env (already gitignored)
# Or export them manually:
export CLOUDINARY_URL=cloudinary://...

./mvnw spring-boot:run
# Spring profile = dev by default (see application.yaml)
# API available at: http://localhost:8080
```

### Frontend

Requirements: Node 20, npm.

```bash
cd InclusiveConnectFrontend
npm install
npm start
# Dev server with live reload at: http://localhost:4200
```

The dev environment.ts points to `http://localhost:8080/api/v1`, so the backend must be running.

---

## Production Deployment

### Environment Variables Reference

| Variable | Required | Description |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | ✅ | MySQL root password |
| `MYSQL_DATABASE` | ✅ | Database name |
| `MYSQL_USER` / `MYSQL_PASSWORD` | ✅ | App DB credentials |
| `JWT_SECRET` | ✅ | ≥64-char random Base64 secret |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | ✅ | SMTP credentials |
| `CLOUDINARY_URL` | ✅ | Full Cloudinary connection URL |
| `CORS_ALLOWED_ORIGINS` | ✅ | Your domain, e.g. `https://yourapp.com` |
| `JWT_ACCESS_EXPIRY_MS` | optional | Default: 900000 (15 min) |
| `JWT_REFRESH_EXPIRY_MS` | optional | Default: 604800000 (7 days) |

### Deploying to a VPS / Cloud VM

```bash
# On the server
git clone https://github.com/MANIKANDAN-17K/InclusiveConnect.git
cd InclusiveConnect
cp .env.example .env
nano .env   # fill in production values

# Set CORS_ALLOWED_ORIGINS to your domain
# Set CORS_ALLOWED_ORIGINS=https://your-domain.com

docker compose up -d --build
```

To serve on HTTPS, place an Nginx/Certbot reverse proxy in front and change port 80 to your host port.

### Using Pre-built Docker Images (CI/CD)

After pushing to `main`, GitHub Actions builds and pushes images to Docker Hub:
- `your-username/inclusive-connect-backend:latest`
- `your-username/inclusive-connect-frontend:latest`

To pull and run pre-built images instead of building locally, set image tags in `.env`:

```env
BACKEND_IMAGE_TAG=sha-abc1234
FRONTEND_IMAGE_TAG=sha-abc1234
```

Then remove the `build:` sections from `docker-compose.yml` and use the `image:` fields directly.

---

## CI/CD Pipeline (GitHub Actions)

The workflow in `.github/workflows/ci-cd.yml` runs on every push and pull request:

| Job | Trigger | Steps |
|---|---|---|
| `backend-ci` | All branches | Maven build, unit tests against MySQL service container |
| `frontend-ci` | All branches | npm install, Angular production build |
| `docker-build` | `main` branch only | Build + push both images to Docker Hub |

### Required GitHub Secrets

Go to **Settings → Secrets and variables → Actions** and add:

| Secret | Value |
|---|---|
| `DOCKER_HUB_USERNAME` | Your Docker Hub username |
| `DOCKER_HUB_TOKEN` | Docker Hub access token (not password) |

---

## Architecture

```
  Browser
     │
     ▼
 ┌─────────────────────────────────┐
 │  Nginx (port 80)                │
 │  • Serves Angular SPA           │
 │  • /api/*  → proxy → backend    │
 │  • /ws     → proxy → backend    │
 └────────────┬────────────────────┘
              │  Docker internal network
              ▼
 ┌────────────────────────────────────┐
 │  Spring Boot Backend (port 8080)   │
 │  • REST API   /api/v1/**           │
 │  • WebSocket  /ws                  │
 │  • JWT auth, RBAC                  │
 └────────────┬───────────────────────┘
              │
              ▼
 ┌────────────────────────────────────┐
 │  MySQL 8  (port 3306)              │
 │  • Persistent volume               │
 └────────────────────────────────────┘
```

---

## Security Notes

- All secrets are environment variables — **never hardcoded**
- `.env` is in `.gitignore` — never committed
- Backend runs as a **non-root user** inside Docker
- Nginx adds **security headers** (X-Frame-Options, X-Content-Type-Options, etc.)
- Production Swagger/OpenAPI UI is **disabled**
- MySQL port 3306 can be removed from `docker-compose.yml` ports for strict production
- JWT access tokens expire in 15 minutes; refresh tokens in 7 days

---

## Author

**Manikandan K**
Bachelor of Engineering in Computer Science and Engineering

Building software with a focus on accessibility, scalability, and clean architecture.

---

## License

This project is developed for educational, learning, and portfolio purposes.
