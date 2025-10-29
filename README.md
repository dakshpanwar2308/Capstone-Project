# I-Cinema Platform

I-Cinema is a microservice-based e-commerce experience for discovering movies, browsing showtimes, reserving seats, and completing ticket purchases online. The solution includes six Spring Boot services, a Spring Cloud Gateway entry point, and a React single page application built with Vite.

## Architecture at a Glance

```
                           ┌──────────────────┐
                           │ React Front-end  │
                           │  (Vite, Axios)   │
                           └────────┬─────────┘
                                    │ HTTP (REST)
                           ┌────────▼─────────┐
                           │ Spring Cloud     │
                           │ Gateway          │
                           └────────┬─────────┘
               ┌───────────┬────────┴────────┬───────────┐
               │           │                 │           │
        ┌──────▼─────┐┌────▼─────┐     ┌─────▼────┐┌─────▼─────┐
        │Movies Svc  ││Theatre   │     │Seating   ││Booking    │
        │Catalog +   ││Shows &   │     │Seat state││Orchestration
        │Discovery   ││Theatres  │     │& holds   ││+ payments │
        └─────┬──────┘└────┬──────┘     └────┬────┘└─────┬──────┘
              │             │                │          │
              │             │                │          │
         ┌────▼────┐    ┌───▼───┐        ┌───▼───┐  ┌───▼────┐
         │MySQL DB │... │MySQL  │  ...   │MySQL   │  │Payment │
         │movies   │    │theatre│        │seating │  │Service │
         └─────────┘    └───────┘        └───────┘  └────────┘
```

- **Movies Service** – manages movie catalog and search.
- **Theatre Service** – manages theatres, showtimes, and links movies to venues.
- **Seating Service** – maintains per-show seat availability and hold lifecycle.
- **Booking Service** – orchestrates seat hold, payment, and booking confirmation.
- **Payment Service** – simulates payment gateway interactions.
- **Gateway Service** – single entry point, CORS handling, and route management.
- **React SPA** – movie discovery, filtering, seat selection, and booking flow.

All services register with Consul in `mysql` profile and use Flyway migrations for schema management.

## Repository Layout

```
backend/
 ├── common-lib/             Shared DTOs, security helpers, exception handling
 ├── movies-service/         Movie catalog microservice
 ├── theatre-service/        Theatre & showtime microservice
 ├── seating-service/        Seat availability microservice
 ├── booking-service/        Booking orchestration microservice
 ├── payment-service/        Payment microservice (Stripe-like simulation)
 └── gateway-service/        Spring Cloud Gateway entry point
frontend/                    React + Vite single page application
ops/mysql/init/              MySQL bootstrap scripts (create databases)
database/ddl/icinema_schema.sql  Consolidated relational schema
Dockerfile (per service)     Container runtime definition
```

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Data JPA, Spring Cloud (Gateway, Consul, OpenFeign), Flyway, MySQL
- **Front-end:** React 18 (TypeScript), Vite, Axios, React Router
- **DevOps:** Maven, Docker, Docker Compose, Consul

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+ (ships with npm 9/10) for the front-end
- Docker & Docker Compose (optional, for containerised deployment)

## Quick Start

1. **Build all services**
   ```bash
   mvn -f backend/pom.xml clean package -DskipTests
   ```
   This installs `common-lib` and resolves the dependency chain so single services can be launched without missing artefact errors.

2. **Run the API Gateway (brings up dependencies automatically)**
   ```bash
   mvn -f backend/pom.xml -pl gateway-service -am spring-boot:run
   ```
   The `-am` flag ensures the gateway builds its required modules the first time. Make sure ports `8080` (gateway) and `8081-8085` are free; stop any stray Java process bound to these ports before retrying.

3. **Front-end**
   ```bash
   cd frontend
   npm install
   npm run dev      # http://localhost:5173 (uses VITE_API_BASE_URL from .env)
   ```
   A production-ready bundle is already checked into `frontend/dist`. To preview it locally without rebuilding, run `npm run preview`.

## Building the Back-end

```bash
# from repo root
mvn -f backend/pom.xml clean package -DskipTests
```

The command builds all microservices and produces runnable jars in each module's `target/` directory.

Run a single service locally (example):

```bash
mvn -f backend/pom.xml -pl movies-service spring-boot:run
```

## Front-end SPA

```bash
cd frontend
npm install
npm run dev                        # dev server at http://localhost:5173
npm run build                      # production build (dist/)
```

The SPA assumes the Spring Cloud Gateway is reachable at `http://localhost:8080` in dev mode. Update `frontend/.env` if the gateway runs elsewhere.

## Docker Compose Workflow

1. Build all services first (`mvn clean package -DskipTests`).
2. Start the stack:

   ```bash
   docker-compose up --build
   ```

   This brings up Consul, a shared MySQL instance, all five microservices, and the API gateway. Each service runs with the `mysql` profile and registers with Consul.

3. Optional: serve the React artefacts from a static host (e.g., Netlify) or run `npm run dev` locally pointing at the gateway container.

_Public ports_

- Gateway: `8080`
- Movies / Theatre / Seating / Booking / Payment: `8081-8085`
- MySQL: `3306`
- Consul UI: `8500`

## Database & Schema

Each microservice owns its schema; Flyway migrations live under `src/main/resources/db/migration` in each module. A consolidated DDL script is available at `database/ddl/icinema_schema.sql` for review or manual provisioning.

Seed data is inserted via Flyway (`V2__seed_*.sql`) for movies, theatres, shows, and initial seat maps to support instant evaluation.

## Key REST Endpoints

| Service           | Sample Endpoint                             | Description                              |
| ----------------- | ------------------------------------------- | ---------------------------------------- |
| Movies            | `GET /api/movies`, `/api/movies/search`      | List, filter, and search movies          |
| Movies            | `GET /api/movies/{id}`                       | Movie detail                             |
| Theatre           | `GET /api/theatres/{id}/shows`               | Shows in a theatre                       |
| Theatre           | `GET /api/theatres/movies/{movieId}/shows`   | Shows for a given movie                  |
| Seating           | `GET /api/seats/show/{showId}`               | Seat map per show                        |
| Seating           | `POST /api/seats/hold`                       | Hold seats temporarily                   |
| Booking           | `POST /api/bookings`                         | Complete booking (hold + payment)        |
| Booking           | `GET /api/bookings/{id}`                     | Booking summary                          |
| Payment           | `POST /api/payments/charge`                  | Simulated card charge                    |

All endpoint paths are accessible via the gateway at `http://localhost:8080`.

## React Screens (placeholders)

`docs/screenshots/` contains placeholders describing key UI views. Capture actual screens after running the app locally.

## Testing & Quality Gates

- **Backend:** `mvn -f backend/pom.xml clean verify` (currently builds without dedicated unit tests; extension points provided via service layer methods).
- **Front-end:** `npm run build` (TypeScript type-check + Vite production build).

## Extensibility Roadmap

- Implement authentication & JWT propagation between services.
- Replace simulated payments with a real PSP integration (Stripe, Razorpay, etc.).
- Add caching (e.g., Redis) for frequently accessed catalog data.
- Provide analytics dashboards for occupancy and revenue.
- Expand automated tests (unit + integration + Cypress end-to-end).

## Troubleshooting

- Update `.env` for the front-end if the gateway runs behind a different host/port.
- If Docker services crash on startup, ensure previous containers/volumes are removed (`docker-compose down -v`).
- Each service exposes actuator health checks at `/actuator/health`.

Happy coding & enjoy the show! 🍿
