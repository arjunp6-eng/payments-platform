# Production-Grade Payments Platform

This repository is the source code for a complete, production-grade payments platform built using a modern microservices architecture. The goal of this project is to serve as a practical, hands-on guide to building resilient, scalable, and secure financial systems.

## Technology Stack

The platform is a polyglot microservices system, leveraging the best tool for each specific job.

* **Architecture**: Microservices, Event-Driven, RESTful APIs
* **Languages**: Kotlin, Go
* **Frameworks**: Spring Boot 3, Spring Cloud
* **Data**: PostgreSQL, Redis (to be added)
* **Messaging**: Apache Kafka (to be added)
* **Infrastructure**: Docker Compose
* **Service Discovery**: Netflix Eureka
* **API Gateway**: Spring Cloud Gateway

## Running the Project

The entire local development environment is containerized using Docker.

1.  **Prerequisites**:
    * Docker Desktop installed and running.
    * Go installed locally (for module management).
    * JDK 21 installed locally.

2.  **Launch the Stack**:
    Navigate to the `infra/` directory and run:
    ```bash
    docker compose up --build -d
    ```
    This will build and start all the services defined in the `docker-compose.yml` file.

## Services

### 1. Discovery Service (`discovery-service`)

* **Language**: Kotlin (Spring Boot)
* **Port**: `8761`
* **Description**: A Netflix Eureka server that acts as the "phonebook" for all other microservices, allowing them to find each other dynamically.

### 2. API Gateway (`api-gateway`)

* **Language**: Kotlin (Spring Boot)
* **Port**: `8080`
* **Description**: The single entry point for all external traffic. It routes requests to the appropriate downstream service using the discovery service.

### 3. Identity Service (`identity-service`)

* **Language**: Kotlin (Spring Boot)
* **Port**: Random
* **Description**: Manages user identity, registration, and authentication.
* **API Endpoints**:
    * `POST /api/v1/users/register`: Registers a new user.

### 4. Ledger Service (`ledger-service`)

* **Language**: Go
* **Port**: `8082`
* **Description**: The core of the financial system. It manages accounts, transactions, and entries using double-entry accounting principles. It is the immutable source of truth for all financial data.
* **API Endpoints**:
    * `POST /v1/accounts`: Creates a new account for a user.
    * `GET /v1/accounts/{id}`: Retrieves details for a single account.
    * `GET /v1/accounts/{id}/balance`: Calculates and returns the real-time balance for an account.
    * `POST /v1/transfers`: Executes an atomic, double-entry transfer between two accounts.
    * `GET /v1/transactions/{id}`: Retrieves a transaction and its associated debit/credit entries.