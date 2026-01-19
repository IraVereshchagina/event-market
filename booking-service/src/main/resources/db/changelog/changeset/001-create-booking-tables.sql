CREATE TABLE event_sessions (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    capacity INT NOT NULL,
    sold_count INT NOT NULL DEFAULT 0
);

CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    event_session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP
);

INSERT INTO event_sessions (event_id, capacity, sold_count)
VALUES (1, 5, 0);