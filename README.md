# REST Calls - BE Home Assignment

## Overview
This application simulates a backend service that tracks the status of sports events. It provides a REST endpoint to update event statuses (live or not live). A scheduled task periodically checks for "live" events, calls a (mocked) external API to get details for these live events, and then simulates publishing this information to a message broker.

## Core Requirements Met
-   **REST Endpoint for Event Status Updates:** Allows clients to set events as live or not live.
-   **Scheduled External API Calls:** Periodically calls a mock external API for events marked as "live".
-   **Message Transformation & Simulated Publishing:** Transforms the API response and logs a simulation of publishing it to a message broker topic.
-   **Basic Error Handling & Logging:** Includes basic error handling for API calls and uses SLF4J for logging application activity.
-   **Unit & Integration Tests:** Provides good test coverage for services, controllers, and schedulers.

## Tech Stack
-   **Java:** 17
-   **Spring Boot:** 3.4.5
-   **Maven:** Project build and dependency management
-   **SLF4J:** Logging facade (with Logback as the default binding from Spring Boot)
-   **JUnit 5 & Mockito:** For unit and integration testing

## Setup & Run Instructions

### Prerequisites
-   JDK 17 or later
-   Apache Maven installed and configured

### Steps
1.  **Clone the repository:**
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

3.  **Run the application:**
    You can run the application using the JAR file (the artifactId is `calls` as per `pom.xml`):
    ```bash
    java -jar target/calls-0.0.1-SNAPSHOT.jar
    ```
    Alternatively, you can use the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run
    ```

The application will start and run on `http://localhost:8080`.

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

## Design Decisions

-   **Framework:** Spring Boot was chosen for its rapid development capabilities, embedded server (Tomcat by default), simplified dependency management via starters, and robust support for building RESTful APIs. This aligns well with typical microservice development practices.

-   **Event State Management:** An in-memory `java.util.concurrent.ConcurrentHashMap` is used to store event statuses (`eventId` -> `isLive`). This approach was selected for its simplicity and to meet the assignment's scope, which did not explicitly require persistence. For a production system, a persistent datastore (like a relational or NoSQL database) or a distributed cache (like Redis or Hazelcast) would be more appropriate to ensure data durability and scalability.

-   **External API Simulation:** The external REST API (which the scheduler calls) is mocked within the same application (`EventController`). This simplifies development and testing by removing the dependency on an actual external service, making the application self-contained for this exercise.

-   **Message Broker Simulation:** Publishing messages to a broker (like Kafka or RocketMQ) is simulated. The `MessageProducerService` serializes the payload to JSON and logs it along with the target topic name. This demonstrates the intent and logic of message publishing without the overhead of setting up and configuring a real message broker. The `spring-kafka` dependency is included in `pom.xml` to show readiness and facilitate easier integration with a real Kafka instance if the project were to evolve.

-   **Scheduling:** Spring Framework's built-in `@Scheduled` annotation is used for the periodic task of fetching live event data. This is a straightforward and effective way to implement scheduled tasks within the Spring ecosystem, requiring minimal configuration.

-   **Error Handling:** Basic error handling is implemented (e.g., `RestClientException` for API calls, validation for request bodies). Errors are logged using SLF4J to provide observability into issues. More sophisticated error handling (e.g., global exception handlers, custom error DTOs) could be added for a production environment.

-   **Logging:** SLF4J is used as the logging facade, with Logback (provided by `spring-boot-starter-logging`) as the underlying implementation. Logging is implemented across controllers, services, and the scheduler to trace requests and internal operations.

## AI Assistance Documentation
This project was developed with the assistance of an AI agent (Jules from Google). The AI assistant was used for the following:
-   Generating the initial Spring Boot project structure and Maven `pom.xml`.
-   Scaffolding code for DTOs, REST controllers, services, and scheduled tasks based on provided requirements.
-   Writing unit and integration test templates and some test logic.
-   Generating this `README.md` file structure and initial content.

All AI-generated code and documentation were reviewed, validated, and modified by the developer to ensure correctness, completeness, and alignment with the project goals and best practices.
