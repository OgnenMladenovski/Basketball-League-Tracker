# Basketball League Tracker

Project for the course **Continuous Integration and Delivery** at the Faculty of Computer Science and Engineering (FINKI), Ss. Cyril and Methodius University, Skopje.

---

## Overview

A web application for tracking basketball leagues, teams, players and matches. Every match result that gets entered updates the league standings automatically (wins, losses, points scored and conceded, league points and the form over the last five games). The application itself is built after the template from the **Web Programming** course, and the focus of this project is the infrastructure around it: Containerization with Docker, Orchestration with Docker Compose, a CI/CD pipeline that publishes the image to Docker Hub and deploys it to Render, and a full Kubernetes deployment.

**Render hosted link:** https://basketball-league-tracker.onrender.com

---

## Features

- **Leagues** — Multiple leagues per season, each with its own points system (points per win / loss) and colors.
- **Teams & Players** — Teams with city, arena and founding year; players with position, jersey number and height.
- **Matches** — Schedule matches between two teams of the same league, then enter the final score.
- **Standings** — Computed on the fly from played matches: wins, losses, points for / against, league points and last-5 form.
- **Seed Data** — Demo leagues, teams, players and matches are loaded on first start (`SEED_DATA=true`).
- **Health Endpoints** — Spring Boot Actuator readiness and liveness probes used by Docker and Kubernetes.

---

## Tech Stack

| Layer          | Technology                                   |
|----------------|----------------------------------------------|
| Language       | Java 21                                      |
| Framework      | Spring Boot 3.5                              |
| Templating     | Thymeleaf                                    |
| Database       | PostgreSQL 18 (H2 for local dev and tests)   |
| Build Tool     | Maven                                        |
| Utilities      | Lombok, Spring Boot Actuator                 |
| Containers     | Docker, Docker Compose                       |
| CI/CD          | GitHub Actions → Docker Hub → Render         |
| Orchestration  | Kubernetes (Docker Desktop), ingress-nginx   |
| DB Admin UI    | Adminer                                      |

---

## Architecture

```
mk.ukim.finki.basketball
│ 
├── config/
│   │ 
│   └── DataSeeder.java
│ 
├── model/
│   │ 
│   ├── domain/        League, Team, Player, Match, BaseEntity
│   │ 
│   ├── dto/           StandingRow
│   │ 
│   ├── enums/         MatchStatus, Position
│   │ 
│   └── exception/     LeagueRuleViolationException, ResourceNotFoundException
│ 
├── repository/        Spring Data JPA repositories
│ 
├── service/           League, Team, Player, Match, Standings services (+ impl/)
│ 
└── web/               Home, League, Team, Player, Match controllers + form/
```

Infrastructure in the repository root:

```
Basketball-League-Tracker
│ 
├── Dockerfile                  multi-stage build (Maven + JRE), non-root user, healthcheck
│ 
├── docker-compose.yml          app + PostgreSQL + Adminer
│ 
├── .github/workflows/
│   │ 
│   └── build-and-deploy.yml    test → build image → push to Docker Hub → deploy to Render
│ 
└── k8s/                        Kubernetes manifests 
```

---

## Running Locally (no Docker)

Uses the in-memory H2 database, no setup needed:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Open http://localhost:8080

---

## Running with Docker Compose

**1. Create a `.env` file in the project root**
```properties
DB_HOST=db
DB_PORT=5432
DB_NAME=basketball
DB_USER=basketball
DB_PASSWORD=your_password

SEED_DATA=true

APP_PORT=8080
ADMINER_PORT=8081
```

**2. Start the stack**
```bash
docker compose up --build -d
```

**3. Open**

| Service     | URL                                                                                  |
|-------------|--------------------------------------------------------------------------------------|
| Application | http://localhost:8080                                                                |
| Adminer     | http://localhost:8081 (System `PostgreSQL`, Server `db` and credentials from `.env`) |

---

## CI/CD Pipeline

On every push to `main`, GitHub Actions runs three jobs in sequence:

1. **Build and Test** — runs `mvn verify` (tests use H2, no external database needed).
2. **Build Image and Push** — builds the Docker image and pushes it to Docker Hub with two tags: `latest` and the short git SHA.
3. **Deploy to Render** — triggers a re-deploy of the Render web service, which pulls the new image.

Pull requests only run the tests.

Image: **https://hub.docker.com/r/ognenmladenovski767/basketball-league-tracker**

```bash
docker pull ognenmladenovski767/basketball-league-tracker:latest
```

---

## Kubernetes

Manifests in `k8s/`, all in the `basketball-league-tracker` namespace:

| File                                               | Kind                | Purpose                                                                          |
|----------------------------------------------------|---------------------|----------------------------------------------------------------------------------|
| `namespace.yaml`                                   | Namespace           | Isolates all resources                                                           |
| `configmap.yaml`                                   | ConfigMap           | Non-sensitive config (DB host / port / name, seed flag)                          |
| `secret.yaml`                                      | Secret              | DB username and password — **not committed**, see below                          |
| `db-service.yaml`                                  | Service (headless)  | Stable DNS name `db` for the database                                            |
| `statefulset.yaml`                                 | StatefulSet         | PostgreSQL with a persistent volume (`data-db-0`)                                |
| `app-deployment.yaml`                              | Deployment          | Spring Boot app with init container and startup / readiness / liveness probes    |
| `app-service.yaml`                                 | Service             | ClusterIP in front of the app pods                                               |
| `ingress.yaml`                                     | Ingress             | `basketball-league-tracker.localhost` and `adminer.basketball-league-tracker.localhost` |
| `adminer-deployment.yaml`, `adminer-service.yaml`  | Deployment, Service | Adminer as the third service                                                     |

**Prerequisites:** a local cluster (Docker Desktop → Enable Kubernetes) and an ingress-nginx controller:
```bash
helm upgrade --install ingress-nginx ingress-nginx --repo https://kubernetes.github.io/ingress-nginx --namespace ingress-nginx --create-namespace
```

**1. Create the Secret** (not in the repository — create `k8s/secret.yaml` with your own values)
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: basketball-league-tracker-secret
  namespace: basketball-league-tracker
type: Opaque
stringData:
  username: basketball
  password: your_password
```

**2. Apply everything**
```bash
kubectl apply -f k8s/
```

**3. Check**
```bash
kubectl -n basketball-league-tracker get pods,svc,pvc,ingress
```

**4. Open**

| Service     | URL                                                |
|-------------|----------------------------------------------------|
| Application | http://basketball-league-tracker.localhost         |
| Adminer     | http://adminer.basketball-league-tracker.localhost |

Deleting the database pod (`kubectl -n basketball-league-tracker delete pod db-0`) recreates it with the same persistent volume and the data stays.

---

## Pages

| Page    | URL                          | Description                                  |
|---------|------------------------------|----------------------------------------------|
| Home    | `/`                          | Overview with leagues and standings          |
| Leagues | `/leagues`, `/leagues/{id}`  | League list and detail with standings table  |
| Teams   | `/teams`                     | Team list, create / edit / delete            |
| Players | `/players`                   | Player list, create / edit / delete          |
| Matches | `/matches`                   | Match list, schedule new match               |
| Score   | `/matches/{id}/score`        | Enter the final score of a match             |