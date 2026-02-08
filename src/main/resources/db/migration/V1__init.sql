CREATE TABLE users (
    id         UUID PRIMARY KEY,
    balance    NUMERIC(12,2) NOT NULL
);

CREATE TABLE bets (
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users(id),
    event_id   BIGINT NOT NULL,
    driver_id  INTEGER NOT NULL,
    amount     NUMERIC(12,2) NOT NULL,
    odds       INTEGER NOT NULL,
    status     VARCHAR(10) NOT NULL
);

CREATE INDEX idx_bets_event_id ON bets(event_id);

CREATE TABLE event_outcomes (
    event_id          BIGINT PRIMARY KEY,
    winner_driver_id  INTEGER NOT NULL
);

INSERT INTO users (id, balance) VALUES
    ('00000000-0000-0000-0000-000000000001', 100.00),
    ('00000000-0000-0000-0000-000000000002', 100.00),
    ('00000000-0000-0000-0000-000000000003', 100.00);
