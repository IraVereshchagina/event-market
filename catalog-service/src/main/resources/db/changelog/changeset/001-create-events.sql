CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    date TIMESTAMP NOT NULL,
    city VARCHAR(100),
    price DECIMAL(19, 2)
);

CREATE INDEX idx_events_city ON events(city);
CREATE INDEX idx_events_date ON events(date);