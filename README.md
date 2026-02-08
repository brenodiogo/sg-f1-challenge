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
| GET | `/api/events?sessionType=&year=&country=&page=&size=` | List F1 events with driver market and odds (paginated) |
| POST | `/api/bets` | Place a bet on a driver |
| GET | `/api/bets?userId=&eventId=&status=` | List bets (all filters optional) |
| POST | `/api/event-outcomes` | Settle an event with the winning driver |
| GET | `/api/event-outcomes/{eventId}` | Get settlement result for an event |
| GET | `/api/users` | List all users with balances |
| GET | `/api/users/{userId}` | Get a single user with balance |

## Testing Walkthrough

Or just use Swagger UI at `http://localhost:8080/swagger-ui.html` — all request bodies have pre-filled examples.

```bash
# 1. Browse events — pick a race and a driver from the market
curl "http://localhost:8080/api/events?year=2024&sessionType=Race&country=Monaco"

# 2. Place a bet (25 EUR on driver 16 at odds 3)
curl -X POST http://localhost:8080/api/bets \
  -H "Content-Type: application/json" \
  -d '{"userId":"00000000-0000-0000-0000-000000000001","eventId":9523,"driverId":16,"amount":25,"odds":3}'

# 3. Verify: balance dropped from 100 to 75
curl http://localhost:8080/api/users/00000000-0000-0000-0000-000000000001

# 4. Settle the event — driver 16 wins
curl -X POST http://localhost:8080/api/event-outcomes \
  -H "Content-Type: application/json" \
  -d '{"eventId":9523,"winnerDriverId":16}'

# 5. Verify: bet status is WON, balance is 150 (75 + 25*3)
curl "http://localhost:8080/api/bets?eventId=9523"
curl http://localhost:8080/api/users/00000000-0000-0000-0000-000000000001
```

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
- **Authentication** — add JWT or session-based auth instead of passing user IDs in requests
- **Async settlement** — process bet settlement asynchronously via events/message queue for better throughput
- **Integration tests** — use Testcontainers for PostgreSQL to test the full persistence layer
- **Observability** — structured logging, distributed tracing, metrics export
