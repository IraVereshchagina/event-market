CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    platform_fee DECIMAL(19, 2) NOT NULL,
    organizer_payout DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE payment_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    event_type VARCHAR(100),
    payload TEXT,
    created_at TIMESTAMP NOT NULL
);