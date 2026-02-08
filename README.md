# F1 Betting Service

A Formula 1 betting backend built with Kotlin, Spring Boot, and PostgreSQL.

## Prerequisites

- Docker and Docker Compose

## Running

```bash
docker-compose up --build
```

The application starts at `http://localhost:8080`. Swagger UI is available at:

**http://localhost:8080/swagger-ui.html**

Three users are pre-seeded with 100 EUR each:
- `00000000-0000-0000-0000-000000000001`
- `00000000-0000-0000-0000-000000000002`
- `00000000-0000-0000-0000-000000000003`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/events?sessionType=&year=&country=` | List F1 events with driver market and odds |
| POST | `/api/bets` | Place a bet on a driver |
| POST | `/api/event-outcomes` | Settle an event with the winning driver |

## Testing Walkthrough

**1. List events** — find a race and pick a driver:

```bash
curl http://localhost:8080/api/events?year=2024&sessionType=Race&country=Monaco
```

**2. Place a bet** — bet 25 EUR on Charles Leclerc (driver 16) at odds 3:

```bash
curl -X POST http://localhost:8080/api/bets \
  -H "Content-Type: application/json" \
  -d '{"userId":"00000000-0000-0000-0000-000000000001","eventId":9523,"driverId":16,"amount":25,"odds":3}'
```

**3. Settle the event** — Leclerc wins:

```bash
curl -X POST http://localhost:8080/api/event-outcomes \
  -H "Content-Type: application/json" \
  -d '{"eventId":9523,"winnerDriverId":16}'
```

The user's balance is now `75 (remaining) + 75 (25 * 3 odds) = 150 EUR`.

## Running Tests

```bash
./gradlew test
```

## Architecture

The project follows **hexagonal architecture** (ports and adapters) with strict layer separation:

```
com.sgchallenge.f1
├── domain          # Entities, value objects, ports — zero framework dependencies
│   ├── model       # User, Bet, F1Event, Driver, etc.
│   ├── port
│   │   ├── inbound   # Use case interfaces (ListEvents, PlaceBet, SettleEvent)
│   │   └── outbound  # Repository and provider interfaces
│   └── exception
├── application     # Use case implementations — depends only on domain
│   └── service
└── infrastructure  # Spring, JPA, HTTP — implements the ports
    └── adapter
        ├── inbound/rest        # Controllers, DTOs, error handling
        └── outbound
            ├── persistence     # JPA entities, Spring Data repos, adapters
            └── openf1          # OpenF1 API client
```

The domain layer is pure Kotlin with no Spring imports. Business rules live in the entities themselves (`User.debit()`, `Bet.settle()`), not in services.

## Key Decisions

- **Immutable domain entities** — `User` and `Bet` are data classes; mutations return new instances, making state transitions explicit and testable
- **Decoupled F1 provider** — the domain defines `F1EventProvider` as an outbound port; the OpenF1 adapter is just one implementation, swappable without touching domain or application code
- **Odds snapshot** — odds are captured on the `Bet` at placement time, so prize calculation is deterministic regardless of future odds changes
- **Sealed exception hierarchy** — `DomainException` subtypes map cleanly to HTTP status codes in a single `@RestControllerAdvice`
- **Flyway migrations** — schema is version-controlled and reproducible; seed data provides ready-to-use test users

## Production Improvements

- **Concurrency** — add optimistic locking (`@Version`) on `User` to handle concurrent balance updates
- **Caching** — cache OpenF1 API responses (sessions rarely change mid-season) to reduce latency and external API load
- **Pagination** — paginate the events endpoint for large result sets
- **Authentication** — add JWT or session-based auth instead of passing user IDs in requests
- **Async settlement** — process bet settlement asynchronously via events/message queue for better throughput
- **Integration tests** — use Testcontainers for PostgreSQL to test the full persistence layer
- **Observability** — structured logging, distributed tracing, metrics export
