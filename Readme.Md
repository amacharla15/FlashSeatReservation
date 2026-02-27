# Flash Seat Reservation (Seat Reservation Lite)

Backend project that models a **flash-sale seat reservation flow** with:
- **seat holds (TTL)**
- **booking confirmation**
- **correctness under concurrency**
- **CI/CD to Azure Container Apps**
- **Postgres + Flyway migrations**

This is the same core problem real ticketing systems solve: *many users racing for the same seat*.

---

#ENTIRE WORKFLOW OR SUCCESS OF CONCURRENCY, DATABASE, DEPLOYMENT AND CLOUD IN DOCS -> SCREENSHOTS.

---

## Tech Stack

- **Java 21**, **Spring Boot 4.x**
- **Spring WebMVC**, **Spring Validation**
- **Spring Data JPA**, **Hibernate**
- **PostgreSQL**
- **Flyway** (schema migrations)
- **Docker** (container image)
- **GitHub Actions** (CI/CD)
- **GHCR** (GitHub Container Registry)
- **Azure Container Apps** (deployment)
- **Azure PostgreSQL Flexible Server** (managed DB)

---

## Features

### 1) Seat Hold (TTL)
- Create a temporary hold for a seat for N seconds (default: 120)
- Reject hold if seat is already held (and not expired)
- Reject hold if seat is already booked

### 2) Booking Confirmation
- Convert a valid hold into a confirmed booking
- Prevent confirming expired holds
- Prevent double-confirm on same hold

### 3) Release Hold
- Explicitly release a valid hold before it expires

### 4) Seat Status
- View seat availability: `AVAILABLE`, `HELD`, `BOOKED`

### 5) Concurrency Safety (important)
When many requests hit the same seat at the same time:
- **only one HOLD succeeds**
- losers get **409 Conflict**
- database remains consistent (no double-holds / double-bookings)

---

## API Endpoints

Base URL:
- Local: `http://localhost:8080`
- Prod (Azure): `https://<your-app-fqdn>`

### Health
`GET /actuator/health`

### Create Hold
`POST /holds`

Request:
```json
{
  "eventId": "event_123",
  "seatNo": "A1",
  "ttlSeconds": 120
}

Response (201):

{
  "holdToken": "uuid",
  "eventId": "event_123",
  "seatNo": "A1",
  "status": "HELD",
  "expiresAt": "2026-02-27T12:23:53Z"
}
Confirm Booking

POST /bookings/confirm

Request:

{
  "holdToken": "uuid",
  "customerEmail": "user@example.com"
}

Response (201):

{
  "bookingId": "2",
  "eventId": "event_123",
  "seatNo": "A1",
  "status": "BOOKED",
  "bookedAt": "2026-02-27T12:24:30Z"
}
Release Hold

DELETE /holds/{holdToken}

Response:

204 No Content on success

Seat Status List

GET /events/{eventId}/seats

Response:

{
  "eventId": "event_123",
  "seats": [
    {"seatNo": "A1", "status": "BOOKED", "expiresAt": null},
    {"seatNo": "A2", "status": "AVAILABLE", "expiresAt": null}
  ]
}
Database + Migrations (Flyway)

Migrations live in:

src/main/resources/db/migration/

When the app starts, Flyway creates:

flyway_schema_history
and applies:

V1__init.sql (tables + indexes)

Core tables:

events

seats

reservations (holds + bookings)

Concurrency: How it’s enforced

This project enforces correctness using the database as the source of truth:

1) Unique constraint at the DB layer

reservations is protected so only one active reservation row can exist for the same (event_id, seat_id).

When two requests race:

one insert wins

the rest fail with a uniqueness violation like:
Key (event_id, seat_id)=(event_123, 1) already exists.

2) Transactional service logic

Hold creation is wrapped in a transaction.
If the insert loses the race, the service catches DataIntegrityViolationException and returns 409 Conflict.

This is a common production pattern because it’s simple and safe.

Run Locally (Docker Postgres + App)
1) Start Postgres
docker compose up -d
docker ps
2) Run the app
./mvnw spring-boot:run
3) Quick test (PowerShell)
# health
curl.exe -i http://localhost:8080/actuator/health

# create hold
$holdBody = @{ eventId="event_123"; seatNo="A1"; ttlSeconds=120 } | ConvertTo-Json
$hold = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/holds" -ContentType "application/json" -Body $holdBody
$hold.holdToken

# confirm booking
$confirmBody = @{ holdToken=$hold.holdToken; customerEmail="user@example.com" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/bookings/confirm" -ContentType "application/json" -Body $confirmBody

# seat status list
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/events/event_123/seats"
Tests
Unit/Integration
./mvnw test
Concurrency test (proof)
./mvnw -Dtest=HoldConcurrencyIT test

Expected:

test passes

output includes at least one uniqueness collision line (showing the race was real)

CI/CD (GitHub Actions → GHCR → Azure Container Apps)

On every push to main, GitHub Actions:

builds Docker image

pushes to GHCR

updates Azure Container App to the new image

Deployment target:

Azure Container Apps (Consumption plan)

Managed Postgres (Azure PostgreSQL Flexible Server)

Production Verification (Azure)
Health check
curl -i https://<your-app-fqdn>/actuator/health

Expected:

200 OK

{"status":"UP"}

Project Structure (high level)

src/main/java/.../web → controllers + DTOs

src/main/java/.../service → business logic (transaction boundaries)

src/main/java/.../repo → JPA repositories

src/main/java/.../entity → JPA entities

src/main/resources/db/migration → Flyway SQL migrations

.github/workflows/ → CI/CD pipeline

Dockerfile → container image build
