## Phase 1 — Domain Model + API Contract

### What this system is
A minimal seat reservation service with temporary holds and final bookings. The core rule is: a seat cannot be held or booked by more than one user at the same time.

### Seat state machine
- AVAILABLE: no active hold and not booked
- HELD: temporary reservation with an expiry time (expiresAt)
- BOOKED: final reservation (permanent)

Flow:
AVAILABLE -> HELD -> BOOKED
HELD can go back to AVAILABLE via expiry or manual release.

### Domain model (conceptual)
- Event(eventId, name, startsAt)
- Seat(eventId, seatNo)
- Reservation(eventId, seatNo, status, holdToken, expiresAt, createdAt)

Invariants:
- At most one active reservation per (eventId, seatNo).
- HELD has holdToken and expiresAt.
- BOOKED is final and does not expire.

### API contract
OpenAPI: docs/api/openapi.yaml

Endpoints:
- POST /holds
- POST /bookings/confirm
- DELETE /holds/{holdToken}
- GET /events/{eventId}/seats

Error codes:
- 404: event/seat/token not found
- 409: seat not available (already held or booked)
- 410: hold expired

### Example flow
1) POST /holds { "eventId": "event_123", "seatNo": "A1", "ttlSeconds": 120 } -> 201 with holdToken + expiresAt
2) POST /bookings/confirm { "holdToken": "<token>", "customerEmail": "user@example.com" } -> 201 with bookingId
3) GET /events/event_123/seats -> seat A1 shows status BOOKED