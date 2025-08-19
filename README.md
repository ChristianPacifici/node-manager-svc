## Kotlin Microservice with Spring Boot  

## Introduction


## Technology Stack

| Technology      | Version | Purpose                          |
|-----------------|---------|----------------------------------|
| **Spring Boot** | 3.2.0   | Application framework            |
| **Kotlin**      | 2.1.21  | Programming language             |
| **Java**        | 21      | Runtime platform                 |
| **Gradle**      | 8.13    | Build tool                       |
| **JOOQ**        | 3.20.1  | database-mapping class Generator |


## Quick Start

### Prerequisites

- Java 21 or higher
- Gradle 8.31 (or use the wrapper inside)
- Docker for containerization
- Postman for testing


## Running the Application

1. Clone the repository

```bash
git clone https://github.com/ChristianPacifici/node-manager-svc.git
cd node-manager-svc
```

2. build with gradle wrapper/docker

```bash
./gradlew build
```
this will also generate the JOOQ classes starting from the SQL file

or alternatively 
```bash
./build-docker.sh
```
this will create the docker-container for the service
or you can do this via command line 

```bash
docker build -t node-manager-svc .
```

3. Run the service
if you want to immediately start testing the service and you have docker 
 installed, you can directly lunch

```bash
./run-docker.sh
```
or using the following on CLI

```bash
docker-compose up
```

4. Access the application

- Application: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus

## API Documentation

### Person Management Endpoints

| Method | Endpoint                               | Description                                                    |
|--------|----------------------------------------|----------------------------------------------------------------|
| POST   | /v1/node-manager/edges                 | Create and Edge                                                |
| GET    | /v1/node-manager/edges/tree/{Id}       | Get a tree of the edges, passing the desired root id using BFS |
| DELETE | /v1/node-manager/edges/{fromId}/{toId} | Delete edge by id from and id to                               |

## Testing
A postman collection in available in /src/test/postman

## Monitoring & Observability

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```


##  Development Setup

### IntelliJ IDEA

1. Import as Gradle project
2. Install Kotlin plugin
3. Set Project SDK to Java 21
4. Enable annotation processing


## Project Structure

```text
node-manager-svc/
├── gradle/
├── src/
│   ├── main/kotlin/com/prewave/nodemager/configuration
│   ├── main/kotlin/com/prewave/nodemager/controller
│   ├── main/kotlin/com/prewave/nodemager/dto
│   ├── main/kotlin/com/prewave/nodemager/exception
│   ├── main/kotlin/com/prewave/nodemager/interceptor
│   ├── main/kotlin/com/prewave/nodemager/services
│   ├── main/resources/db/changelog
│   ├── main/resources/db/migration
│   ├── main/resources/db/application.yaml
│   ├── test/kotlin/com/prewave/nodemager/controller
│   ├── main/kotlin/postman
├── build.gradle.kts
├── build-docker.sh
├── docker-compose.yml
├── Dockerfile
├── gradle.properties
├── gradlew
├── gradlew
├── gradlew.bat
├── README.md
├── run-docker.sh
├── settings.gradle.kts
```
