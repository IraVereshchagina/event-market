# Event Market

A highly scalable, distributed microservices platform for event ticketing, designed to handle high-concurrency booking scenarios and real-time updates.

## Architecture Overview

The system is built using an event-driven architecture with **Apache Kafka**. It implements the **Saga Pattern** for distributed transactions and uses **WebSockets** for real-time frontend updates.

### Microservices

1. **Auth Service:** Manages user registration and JWT-based authentication.
2. **Catalog Service:** Handles event creation and search. Implements Polyglot Persistence (PostgreSQL for transactions, MongoDB for rich text descriptions) and Redis Caching.
3. **Booking Service:** The core service. Handles ticket reservations, prevents double-booking using **Redis Distributed Locks (Redisson)**, and orchestrates the Saga.
4. **Payment Service:** Processes financial transactions asynchronously. Integrates with a Mock Bank (Stripe) and calculates platform commissions (Split payments).
5. **Notification Service:** Listens to Kafka events and pushes real-time updates to the frontend via **WebSockets (STOMP)**.

## Key Features

* **Distributed Transactions (Saga Pattern):** Ensures data consistency across Booking and Payment services without tight coupling.
* **Concurrency Control:** Utilizes Redis distributed locks to prevent ticket over-selling under high load.
* **Real-time Seat Tracking:** Users see available seats updating in real-time without page reloads (Kafka -> WebSockets).
* **Polyglot Persistence:** Optimized data storage using PostgreSQL (relational data), MongoDB (NoSQL for text), and Redis (in-memory caching).

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.2.1, Spring Data (JPA, Mongo, Redis), Spring Kafka, Spring WebSocket
* **Databases:** PostgreSQL 15, MongoDB 6.0, Redis 7
* **Message Broker:** Apache Kafka
* **Testing:** JUnit 5, Mockito, **Testcontainers**, RestAssured
* **Infra:** Docker & Docker Compose, Liquibase

## How to Run

The entire infrastructure and all microservices are containerized. You only need Docker installed.

1. Clone the repository:
   ```bash
   git clone [https://github.com/YOUR_USERNAME/event-market.git](https://github.com/YOUR_USERNAME/event-market.git)
   cd event-market
2. Build and start all containers:
    ```bash
    docker-compose up -d --build
3. Access the Real-time Demo: Open http://localhost:8085/index.html in your browser.
   
    The page includes an interactive control panel where you can:
   - Step 1: Create an Event (Postgres + MongoDB).
   - Step 2: Open Sales Session (Booking DB).
   - Step 3: Buy a Ticket. This triggers the Distributed Saga (Booking -> Payment -> MockBank -> Notification) and updates the seat counter in Real-Time via WebSockets directly on the screen.