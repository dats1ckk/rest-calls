# REST Calls - BE Home Assignment

## Overview
This application simulates a backend service that tracks the status of sports events. It provides a REST endpoint to update event statuses (live or not live). A scheduled task periodically checks for "live" events, calls a (mocked) external API to get details for these live events, and then publishes this information to a Kafka topic.

## Core Requirements Met
-   **REST Endpoint for Event Status Updates:** Allows clients to set events as live or not live.
-   **Scheduled External API Calls:** Periodically calls a mock external API for events marked as "live".
-   **Message Transformation & Kafka Publishing:** Transforms the API response and publishes it to a Kafka topic ("live_event_updates").
-   **Basic Error Handling & Logging:** Includes basic error handling for API calls and uses SLF4J for logging application activity.
-   **Unit & Integration Tests:** Provides good test coverage for services, controllers, and schedulers.

## Tech Stack
-   **Java:** 17
-   **Spring Boot:** 3.4.5
-   **Apache Kafka:** Message broker for event streaming.
-   **Maven:** Project build and dependency management
-   **SLF4J:** Logging facade (with Logback as the default binding from Spring Boot)
-   **Lombok:** To reduce boilerplate code.
-   **JUnit 5 & Mockito:** For unit and integration testing

### Setting up Kafka (Local Development)

For local development and testing, you can run Kafka using Docker Compose. Create a `docker-compose.yml` file with the following content:

```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
```
Run `docker-compose up -d` in the directory where you saved the file. This will start Kafka on `localhost:9092` (or `localhost:29092` from outside Docker).
The application is configured to connect to Kafka on `localhost:9092` by default and publishes messages to the `live_event_updates` topic.

## Setup & Run Instructions

### Prerequisites
-   JDK 17 or later
-   Apache Maven installed and configured
-   Docker and Docker Compose (if running Kafka locally as described above)

### Steps
1.  **Start Kafka:**
    If you are using the Docker Compose setup described above, ensure Kafka is running:
    ```bash
    docker-compose up -d 
    ```
    (Run this in the directory where you saved the `docker-compose.yml` file).

2.  **Clone the repository:**
    ```bash
    git clone <repository-url> 
    cd <repository-directory>
    ```
    (Replace `<repository-url>` and `<repository-directory>` with the actual values)

2.  **Build the project:**
    This command will compile the code, run tests, and package the application into a JAR file.
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    You can run the application using the JAR file (the artifactId is `calls` as per `pom.xml`):
    ```bash
    java -jar target/calls-0.0.1-SNAPSHOT.jar
    ```
    Alternatively, you can use the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run
    ```

The application will start and run on `http://localhost:8080`. It will attempt to connect to Kafka on `localhost:9092`.

## API Endpoints

### 1. Update Event Status
-   **Endpoint:** `POST /events/status`
-   **Description:** Updates the status of an event (e.g., whether it is currently live or not).
-   **Request Body Example:**
    ```json
    {
      "eventId": "event123",
      "live": true
    }
    ```
    Setting `live` to `false` will mark the event as not live.
-   **Success Response:**
    -   Code: `200 OK`
-   **Error Response:**
    -   Code: `400 Bad Request`
    -   Reason: If `eventId` is missing or the request body is malformed.

### 2. Mock External API (for testing scheduler)
-   **Endpoint:** `GET /events/mockapi/event/{eventId}`
-   **Description:** This is a mock endpoint that simulates an external API from which the scheduler fetches data for live events.
-   **Path Variable:** `{eventId}` - The ID of the event.
-   **Example Response (`200 OK`):**
    ```json
    {
      "eventId": "event123",
      "currentScore": "1:0" 
    }
    ```
    The `currentScore` is randomly generated for demonstration.
-   **Error Response:**
    -   Code: `400 Bad Request`
    -   Reason: If `eventId` is null or empty.

## How to Run Tests
To execute the unit and integration tests for the application, run the following Maven command from the project root directory:
```bash
mvn test
```

### Testing Kafka Integration Manually

After [setting up Kafka](#setting-up-kafka-local-development) and starting the main application:

1.  **Mark an event as "live":**
    Send a POST request to `/events/status`:
    ```bash
    curl -X POST -H "Content-Type: application/json" -d '{"eventId":"testEvent001", "live":true}' http://localhost:8080/events/status
    ```

2.  **Listen for messages on the Kafka topic:**
    You can use the Kafka console consumer tool to see the messages being published to the `live_event_updates` topic. If you are using the Docker setup provided, you can run the consumer inside a new container or execute it within the running Kafka container.

    To run the console consumer from your host machine (if Kafka is exposed on `localhost:9092` as per application config):
    If you have Kafka binaries downloaded, you can run:
    ```bash
    # Navigate to your Kafka binaries directory
    # bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic live_event_updates --from-beginning
    ```

    Alternatively, if using the provided Docker setup, you can execute the command within the Kafka container:
    ```bash
    docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic live_event_updates --from-beginning
    ```
    *(Note: `localhost:9092` inside the `kafka` container refers to itself).*


3.  **Observe Output:**
    Within 10 seconds (the scheduler interval), you should see JSON messages appearing in the console consumer output, corresponding to the `MockApiResponse` for `testEvent001`. Each message will look something like:
    ```json
    {"eventId":"testEvent001","currentScore":"1:0"} 
    ```
    (The score might vary if it was made random).

## Design Decisions

-   **Framework:** Spring Boot was chosen for its rapid development capabilities, embedded server (Tomcat by default), simplified dependency management via starters, and robust support for building RESTful APIs. This aligns well with typical microservice development practices.

-   **Event State Management:** An in-memory `java.util.concurrent.ConcurrentHashMap` is used to store event statuses (`eventId` -> `isLive`). This approach was selected for its simplicity and to meet the assignment's scope, which did not explicitly require persistence. For a production system, a persistent datastore (like a relational or NoSQL database) or a distributed cache (like Redis or Hazelcast) would be more appropriate to ensure data durability and scalability.

-   **External API Simulation:** The external REST API (which the scheduler calls) is mocked within the same application (`EventController`). This simplifies development and testing by removing the dependency on an actual external service, making the application self-contained for this exercise.

-   **Message Broker Integration:** The application integrates with Apache Kafka for message publishing. The `MessageProducerService` uses Spring's `KafkaTemplate` to send serialized event data (as JSON strings) to the `live_event_updates` topic. The `spring-kafka` dependency facilitates this integration.

-   **Scheduling:** Spring Framework's built-in `@Scheduled` annotation is used for the periodic task of fetching live event data. This is a straightforward and effective way to implement scheduled tasks within the Spring ecosystem, requiring minimal configuration.

-   **Error Handling:** Basic error handling is implemented (e.g., `RestClientException` for API calls, validation for request bodies, Kafka send callbacks). Errors are logged using SLF4J to provide observability into issues. More sophisticated error handling (e.g., global exception handlers, custom error DTOs) could be added for a production environment.

-   **Logging:** SLF4J is used as the logging facade, with Logback (provided by `spring-boot-starter-logging`) as the underlying implementation. Logging is implemented across controllers, services, and the scheduler to trace requests and internal operations, including Kafka message production.

-   **Lombok:** Project Lombok is used to reduce boilerplate code such as getters, setters, and constructors in DTOs and other classes (e.g., using `@Data`, `@RequiredArgsConstructor`).

## AI Assistance Documentation
This project was developed with the assistance of an AI agent (Jules from Google). The AI assistant was used for the following:
-   Generating the initial Spring Boot project structure and Maven `pom.xml`.
-   Scaffolding code for DTOs, REST controllers, services, and scheduled tasks based on provided requirements.
-   Writing unit and integration test templates and some test logic.
-   Generating this `README.md` file structure and initial content.

All AI-generated code and documentation were reviewed, validated, and modified by the developer to ensure correctness, completeness, and alignment with the project goals and best practices.
