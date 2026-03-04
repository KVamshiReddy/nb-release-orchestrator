# NB Release Orchestrator

> **Structured releases. Full visibility. Every deployment, orchestrated.**

NB Release Orchestrator is a project which can be us as a Internal custom release management portal at Enterprise level. It integrates with Jira as the single source of truth for all production release requests, giving every stakeholder — from Dev Teams to Senior Leadership — a consistent, real-time view of the release flow.

---

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Jira Integration](#jira-integration)
- [API Reference](#api-reference)
- [User Roles](#user-roles)
- [Release Workflow](#release-workflow)
- [Testing](#testing)
- [Deployment](#deployment)
- [Roadmap](#roadmap)

---

## Overview

Production releases today are uncoordinated — Jira tickets disappear into inboxes, DevOps waits for email approvals, and stakeholders have no visibility until something breaks.

**NB Release Orchestrator** solves this by providing:

- **Single pane of glass** — all release requests in one dashboard, synced live from Jira
- **Structured approval gates** — Release Manager and CAB approval with full audit trail
- **Real-time status** — WebSocket push means every stakeholder sees deployment status instantly
- **Role-based access** — 7 distinct roles with enforced permissions at API and UI level
- **Immutable audit log** — every action recorded with actor, timestamp, and before/after state

---

## Architecture

The POC uses a **Monolithic Layered Architecture** built on Spring Boot 3.x. Clean package boundaries (Controller → Service → Repository → Domain) are designed to map directly to future microservice extraction.

```
┌─────────────────────────────────────────────────────────┐
│                    React.js Portal                       │
│         Ant Design · Zustand · STOMP WebSocket           │
└────────────────────────┬────────────────────────────────┘
                         │ REST + WebSocket
┌────────────────────────▼────────────────────────────────┐
│               Spring Boot 3.x Backend                    │
│                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │
│  │ Controllers │→ │  Services   │→ │  Repositories   │  │
│  │  REST + WS  │  │ Biz Logic   │  │  Spring Data    │  │
│  └─────────────┘  └──────┬──────┘  └────────┬────────┘  │
│                          │                  │           │
│              ┌───────────▼──────┐           │           │
│              │  Spring Events   │           │           │
│              │ Audit · Notify   │           │           │
│              └──────────────────┘           │           │
└──────────────────────────────────────────┬──┘           │
                                           │              │
┌──────────────────────────────────────────▼──────────────┐
│                   PostgreSQL 15                          │
│         releases · users · approvals · audit_logs        │
└─────────────────────────────────────────────────────────┘
         ↕ Webhook (HMAC-SHA256)
┌─────────────────────────────────────────────────────────┐
│                      Jira Cloud                          │
│            Release Change Request Issue Type             │
└─────────────────────────────────────────────────────────┘
```

---

## Tech Stack

### Backend
| Layer | Technology | Version |
|---|---|---|
| Runtime | Java (OpenJDK) | 17 LTS |
| Framework | Spring Boot | 3.2.x |
| Web API | Spring Web MVC | Included |
| Security | Spring Security | 6.x |
| Data Access | Spring Data JPA + Hibernate | Included |
| Database | PostgreSQL | 15.x |
| HTTP Client | Spring WebClient | Included |
| WebSocket | Spring WebSocket + STOMP | Included |
| Email | Spring Mail + JavaMail | Included |
| Testing | JUnit 5 + Mockito + Testcontainers | Included |
| Build | Apache Maven | 3.9.x |
| Containerisation | Docker + Docker Compose | Latest |

### Frontend
| Layer | Technology | Version |
|---|---|---|
| Framework | React.js + TypeScript | 18.x |
| UI Library | Ant Design | 5.x |
| State | Zustand | 4.x |
| HTTP | Axios | 1.x |
| Real-time | STOMP.js + SockJS | Latest |
| Charts | Recharts | 2.x |
| Build | Vite | 5.x |

---

## Project Structure

```
nb-release-orchestrator/
│
├── nb-release-backend/                  # Spring Boot application
│   ├── src/main/java/com/nb/nbro/
│   │   ├── NbReleaseOrchestratorApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java      # JWT filter chain, CORS, STATELESS session
│   │   │   ├── WebSocketConfig.java     # STOMP WebSocket broker
│   │   │   └── JiraClientConfig.java    # Spring WebClient bean for Jira REST API
│   │   ├── controller/
│   │   │   ├── AuthController.java      # POST /api/v1/auth/login, GET /auth/me
│   │   │   ├── ReleaseController.java   # GET/PATCH /api/v1/releases/**
│   │   │   ├── ApprovalController.java  # POST /api/v1/approvals/{id}
│   │   │   ├── DashboardController.java # GET /api/v1/dashboard/stats + calendar
│   │   │   └── WebhookController.java   # POST /api/v1/webhooks/jira
│   │   ├── service/
│   │   │   ├── ReleaseService.java      # Core CRUD, state transitions, event pub
│   │   │   ├── ApprovalService.java     # Approval routing — direct vs CAB
│   │   │   ├── JiraService.java         # Outbound Jira REST calls via WebClient
│   │   │   ├── NotificationService.java # @EventListener — email + WebSocket push
│   │   │   └── AuditService.java        # @EventListener — INSERT-only audit log
│   │   ├── repository/
│   │   │   ├── ReleaseRepository.java
│   │   │   ├── UserRepository.java
│   │   │   ├── ApprovalRepository.java
│   │   │   └── AuditLogRepository.java
│   │   ├── domain/
│   │   │   ├── entity/                  # JPA entities: Release, User, Approval, AuditLog
│   │   │   └── dto/                     # Request/response DTOs
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java    # Generate + validate JWT tokens
│   │   │   └── JwtAuthFilter.java       # OncePerRequestFilter
│   │   └── event/
│   │       └── ReleaseStatusChangedEvent.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── nb-release-portal/                   # React.js frontend
│   ├── src/
│   │   ├── pages/
│   │   │   ├── Login.tsx
│   │   │   ├── Dashboard.tsx
│   │   │   ├── ReleaseList.tsx
│   │   │   ├── ReleaseDetail.tsx
│   │   │   ├── DeployQueue.tsx
│   │   │   └── AuditLog.tsx
│   │   ├── store/                       # Zustand stores
│   │   ├── api/                         # Axios API client
│   │   └── websocket/                   # STOMP connection
│   ├── package.json
│   └── vite.config.ts
│
├── db/
│   └── init.sql                         # PostgreSQL DDL — run once at setup
│
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Docker + Docker Compose
- A Jira Cloud account with admin access
- [ngrok](https://ngrok.com) (for exposing local backend to Jira webhook)

### Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/nb-release-orchestrator.git
cd nb-release-orchestrator

# 2. Copy and configure environment variables
cp .env.example .env
# Edit .env with your Jira credentials, JWT secret, and SMTP config

# 3. Start PostgreSQL
docker-compose up -d postgres
# Wait ~5 seconds for readiness

# 4. Initialise the database schema
docker cp db/init.sql postgres:/tmp/init.sql
docker exec -it postgres psql -U nbro_user -d nb_release_orchestrator -f /tmp/init.sql

# 5. Start the backend
docker-compose up -d backend
# Spring Boot validates the schema on startup — check logs for "Started NbReleaseOrchestratorApplication"

# 6. Start the frontend
docker-compose up -d frontend
# Portal available at http://localhost:3000

# 7. Expose backend to Jira (POC only)
ngrok http 8080
# Copy the HTTPS URL → paste into Jira Webhook config (see Jira Integration below)
```

### Default Test Users (seeded by init.sql)

| Email | Password | Role |
|---|---|---|
| dev@nb.com | test1234 | Dev Team |
| devops@nb.com | test1234 | DevOps |
| relmanager@nb.com | test1234 | Release Manager |
| scrummaster@nb.com | test1234 | Scrum Master |
| po@nb.com | test1234 | Product Owner |
| cab@nb.com | test1234 | CAB Member |
| stakeholder@nb.com | test1234 | Stakeholder |

> ⚠️ Change all passwords before any non-local deployment.

---

## Configuration

### Environment Variables (`.env`)

```env
# Database
DB_USER=nbro_user
DB_PASS=your-strong-password

# Jira
JIRA_BASE_URL=https://yourorg.atlassian.net
JIRA_API_TOKEN=your-jira-api-token
JIRA_EMAIL=devops@nb.com
JIRA_WEBHOOK_SECRET=your-32-char-hex-secret

# JWT
JWT_SECRET=your-256-bit-random-secret-at-least-32-chars

# SMTP (use Mailhog values for local POC)
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USER=noreply@nb.com
SMTP_PASS=
```

### `application.yml` Key Properties

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/nb_release_orchestrator
    username: ${DB_USER}
    password: ${DB_PASS}
  jpa:
    hibernate:
      ddl-auto: validate          # Validates schema — never creates or modifies
    show-sql: false               # Set true for local debugging only

jira:
  base-url: ${JIRA_BASE_URL}
  api-token: ${JIRA_API_TOKEN}
  user-email: ${JIRA_EMAIL}
  webhook-secret: ${JIRA_WEBHOOK_SECRET}

security:
  jwt:
    secret: ${JWT_SECRET}
    expiry-ms: 86400000           # 24 hours for POC; reduce for production
```

---

## Jira Integration

### One-Time Setup (Jira Admin)

1. **Create Issue Type**: Jira Settings → Issue Types → Add Issue Type
   - Name: `Release Change Request` | Type: Standard

2. **Add Custom Fields** (Jira Settings → Custom Fields):

   | Field | Type | Field ID |
   |---|---|---|
   | Release Type | Single Select | customfield_10201 |
   | Target Environment | Single Select | customfield_10202 |
   | Scheduled Release Date | Date Time Picker | customfield_10203 |
   | Services Impacted | Multi-line Text | customfield_10204 |
   | Deployment Steps | Multi-line Text | customfield_10205 |
   | Rollback Plan | Multi-line Text | customfield_10206 |
   | DevOps Assignee | User Picker | customfield_10207 |
   | Release Manager | User Picker | customfield_10208 |
   | CAB Approval Status | Single Select | customfield_10209 |
   | Post Release Notes | Multi-line Text | customfield_10210 |

3. **Create Webhook**: Jira Settings → System → WebHooks → Create a WebHook
   - URL: `https://<your-backend>/api/v1/webhooks/jira`
   - Events: `jira:issue_created`, `jira:issue_updated`, `jira:issue_deleted`
   - JQL Filter: `issuetype = "Release Change Request"`
   - Secret: your `JIRA_WEBHOOK_SECRET` value

### HMAC Signature Validation

Every Jira webhook POST includes an `X-Hub-Signature` header with an HMAC-SHA256 hash of the payload. The `WebhookController` validates this signature before processing. Requests that fail validation return `HTTP 401`.

---

## API Reference

All endpoints are prefixed `/api/v1`. All routes except `/auth/login` require a JWT Bearer token.

| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/auth/login` | Public | Authenticate, receive JWT |
| GET | `/auth/me` | Authenticated | Current user profile |
| GET | `/releases` | All | List releases (paginated + filtered) |
| GET | `/releases/{id}` | All | Full release detail |
| PATCH | `/releases/{id}/status` | REL_MGR, DEVOPS | Transition workflow state |
| POST | `/approvals/{id}` | REL_MGR, CAB | Submit approval/rejection |
| POST | `/releases/{id}/initiate` | DEVOPS | Start deployment |
| POST | `/releases/{id}/rollback` | DEVOPS, REL_MGR | Trigger rollback |
| GET | `/releases/{id}/audit` | All | Immutable audit trail |
| GET | `/dashboard/stats` | All | KPI summary counts |
| GET | `/dashboard/calendar` | All | Scheduled releases by date range |
| GET | `/releases/export` | SCR_MASTER, REL_MGR | CSV export |
| POST | `/webhooks/jira` | Webhook (HMAC) | Receive Jira events |
| GET | `/users` | REL_MGR | List portal users |
| PUT | `/users/{id}/role` | REL_MGR | Change user role |

Full Swagger UI available at: `http://localhost:8080/swagger-ui.html`

---

## User Roles

| Role | Spring Authority | Access |
|---|---|---|
| Dev Team | `ROLE_DEV` | Create + view own team releases, post-release verification |
| Cross-Dev | `ROLE_CROSS_DEV` | Read-only all releases, comment only |
| DevOps | `ROLE_DEVOPS` | Initiate and rollback deployments, update status |
| Scrum Master | `ROLE_SCRUM_MASTER` | Read all releases, CSV export |
| Product Owner | `ROLE_PRODUCT_OWNER` | Read all releases, add comments |
| Release Manager | `ROLE_RELEASE_MANAGER` | Full access — approve, reject, CAB escalate, user admin |
| Stakeholder | `ROLE_STAKEHOLDER` | Dashboard summary only — executive read-only view |

---

## Release Workflow

```
[DRAFT] ──► [PENDING_REVIEW] ──► [APPROVED] ──► [IN_PROGRESS] ──► [DEPLOYED] ──► [VERIFIED]
                    │                  ▲                │
                    │                  │                └──► [ROLLED_BACK] ──► [PENDING_REVIEW]
                    └──► [CAB_REVIEW] ─┘
                    │
                    └──► [REJECTED]
```

| Transition | From | To | Actor |
|---|---|---|---|
| T1 | DRAFT | PENDING_REVIEW | Dev Team (via Jira) |
| T2 | PENDING_REVIEW | CAB_REVIEW | Release Manager |
| T3 | PENDING_REVIEW | APPROVED | Release Manager (minor/hotfix) |
| T4 | PENDING_REVIEW | REJECTED | Release Manager |
| T5 | CAB_REVIEW | APPROVED | CAB Member |
| T6 | CAB_REVIEW | REJECTED | CAB Member |
| T7 | APPROVED | IN_PROGRESS | DevOps |
| T8 | IN_PROGRESS | DEPLOYED | DevOps |
| T9 | DEPLOYED | VERIFIED | Dev/QA |
| T10 | IN_PROGRESS | ROLLED_BACK | DevOps |
| T11 | ROLLED_BACK | PENDING_REVIEW | Dev Team |

---

## Testing

```bash
# Unit tests (service layer with Mockito)
cd nb-release-backend
mvn test

# Integration tests with Testcontainers (real PostgreSQL)
mvn verify -P integration-tests

# Coverage report (target: 80% branch coverage)
mvn jacoco:report
open target/site/jacoco/index.html

# Load test webhook endpoint (requires k6)
k6 run tests/load/webhook-load-test.js
```

### UAT Sign-Off Scenarios

| ID | Actor | Pass Criteria |
|---|---|---|
| S1 | Dev Team | Jira ticket appears in portal within 5 seconds |
| S2 | Release Mgr | Approval changes status; DevOps receives email |
| S3 | DevOps | Initiate → IN_PROGRESS visible to all stakeholders |
| S4 | All Roles | Dashboard updates without page refresh within 2 seconds |
| S5 | DevOps | Rollback logged with reason; all stakeholders notified |
| S6 | Dev Team | Approve action returns HTTP 403 for unauthorised role |
| S7 | Release Mgr | Audit trail shows all transitions with actor + timestamp |
| S8 | Scrum Master | CSV export contains all releases with correct status values |

---

## Deployment

### Docker Compose (POC)

```bash
docker-compose up -d
```

Services: `postgres` (5432) · `backend` (8080) · `frontend` (3000) · `mailhog` (1025/8025)

### Production Roadmap

| Phase | Timeline | Key Changes |
|---|---|---|
| POC | Weeks 1–8 | Spring Boot monolith + Docker Compose |
| Prod v1 | Month 3–4 | OAuth 2.0 SSO, Redis cache, Kubernetes, HA Postgres |
| Prod v2 | Month 5–6 | Slack bot, CI/CD triggers, advanced analytics |
| Hybrid | Month 7–12 | Extract Notification + Audit microservices, Kafka events |
| Microservices | Year 2 | Full service mesh, API gateway, distributed tracing |

---

## Roadmap

- [ ] OAuth 2.0 / Active Directory SSO
- [ ] Slack notifications integration
- [ ] CI/CD pipeline trigger hooks (Jenkins / GitHub Actions)
- [ ] Release calendar conflict detection
- [ ] Kubernetes deployment manifests
- [ ] Redis caching layer for dashboard queries
- [ ] Advanced release analytics and KPI trending
- [ ] Mobile-responsive PWA
- [ ] Microservice extraction — Notification Service
- [ ] Microservice extraction — Audit Service

---

## Contributing

This is an internal NB project. Raise a PR against the `develop` branch. All PRs require:
- Passing CI (unit + integration tests)
- Code review from a senior engineer
- No secrets committed (enforced via `.gitignore` and pre-commit hooks)

---

## License

Internal use only — NB Engineering. Not for distribution.

---