# Social Media Service

---

##  Project Overview

This is a **Spring Boot 3.5.6** microservice built with Java 25 that provides:
- **Social Media Features** including blogging, messaging, and interactions
- **User Authentication** with JWT tokens
- **gRPC Communication** for inter-service communication
- **Real-time Monitoring** with Prometheus metrics
- **Health Checks** optimized for AWS API Gateway

---

##  Technology Stack

### Core Framework
- **Spring Boot 3.5.6** - Latest Spring Boot framework
- **Java 25** - Latest Java version
- **Maven 3.9** - Build and dependency management

### Data & Storage
- **MongoDB** - NoSQL database for storing user data, posts, and interactions
  - Atlas Cloud Database Connection
  - Auto-index creation enabled



### Authentication & Security
  - Spring Security - Authentication and authorization framework
  - Authorization and authentication by API gateway and Cognito
  - Refresh Token Expiration: 7 days

### API & Documentation
- **SpringDoc OpenAPI 2.1.0** - Swagger UI and OpenAPI integration
- **Spring Validation** - Bean validation support

### Monitoring & Observability
- **Spring Boot Actuator** - Health checks and metrics endpoints
- **Prometheus Micrometer (1.71.0)** - Metrics collection and export
  - JVM metrics
  - Process metrics
  - System metrics
  - Tomcat metrics
  - HTTP request metrics with histograms

### Inter-Service Communication
- **gRPC 1.71.0** - High-performance RPC framework
  - grpc-protobuf
  - grpc-stub
  - grpc-netty-shaded
  - Protocol Buffers for data serialization

### Utilities
- **Lombok** - Reduce boilerplate code with annotations
- **Commons FileUpload (1.4)** - File upload handling
- **Commons IO (2.15.1)** - File I/O operations
- **Netty (4.2.7.Final)** - Async network communication

---

## 🚀 Getting Started

### Prerequisites
- Java 25+
- Maven 3.9+
- MongoDB Atlas account or local MongoDB instance


### Configuration

#### 1. Database Configuration
Edit `src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/
spring.data.mongodb.database=awsrek_db
```



#### 2. CORS Configuration
```properties
cors.allowed-origins=*
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
cors.allowed-headers=*
```


### Build & Run

**Local Development:**
```bash
mvn clean install
mvn spring-boot:run
```

**Docker Build:**
```bash
docker build -t awsrek:latest .
docker run -p 8080:8080 awsrek:latest
```

**Port Configuration:**
```properties
server.port=8080
```

---

##  API Endpoints

### Health Check Endpoints
AWS API Gateway compatible health endpoints:

**1. Simple Ping (AWS API Gateway friendly)**
```
GET /api/health/ping
```
Response: 200 OK if service is UP, 503 if DOWN

**2. Detailed Health**
```
GET /api/health/detailed
```
Returns comprehensive health information including:
- Service status
- MongoDB connection status
- Component statuses

### Monitoring & Metrics

**Actuator Endpoints:**
```
GET /actuator - View all available endpoints
GET /actuator/health - Health check summary
GET /actuator/metrics - Available metrics list
GET /actuator/prometheus - Prometheus-format metrics
```

**Swagger UI:**
```
GET /swagger-ui.html - Interactive API documentation
GET /v3/api-docs - OpenAPI specification
```

---

##  Features

###  Authentication
- API Gateway and Cognito based authentication
- HTTP header data validation


###  Social Media Features
- **Blogging** - Create, read, update, delete posts
- **Messaging** - User-to-user communication
- **Interactions** - Comments, likes, and reactions
- **User Profiles** - User information management

###  gRPC Communication
- Inter-service communication via Protocol Buffers
- High-performance async RPC calls
- User service integration

###  Monitoring & Observability
- Real-time metrics via Prometheus
- JVM, Process, and System metrics
- HTTP request tracking with percentile histograms
- Custom application metrics

---

##  Docker Deployment

The project includes a multi-stage Dockerfile:

**Stage 1: Build**
- Uses Maven 3.9 with Eclipse Temurin JDK 25
- Downloads dependencies (cached layer)
- Builds application with `mvn clean package`

**Stage 2: Runtime**
- Uses Eclipse Temurin JRE 25 Alpine (lightweight)
- Non-root user security (spring:spring)
- Optimized for container deployment

**Deployment Options:**
- Docker Compose (included `docker-compose.yml`)
- AWS ECS/Fargate
- Kubernetes

---

## 📋 Actuator Dependencies

Your project includes `spring-boot-starter-actuator` which provides:

 **Included Features:**
- Health endpoint checks
- Metrics collection
- Environment information
- Thread information
- JVM diagnostics

 **Note:** Actuator is already in the pom.xml and is required for AWS API Gateway health checks. All endpoints are exposed via:
```properties
management.endpoints.web.exposure.include=*
```

---

## 🔧 Configuration Summary

### Key Properties
| Property | Value | Purpose |
|----------|-------|---------|
| `server.port` | 8080 | Application server port |
| `spring.data.mongodb.database` | awsrek_db | MongoDB database name |
| `management.endpoints.web.exposure.include` | * | Expose all actuator endpoints |
| `management.endpoint.health.show-details` | always | Show detailed health info |
| `management.metrics.export.prometheus.enabled` | true | Enable Prometheus export |

### Logging Levels
```properties
logging.level.FCJLaurels.awsrek=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
logging.level.org.springframework.security=DEBUG
```

---

## Project Structure

```
socialmedia/
├── src/
│   ├── main/
│   │   ├── java/FCJLaurels/awsrek/
│   │   │   ├── controller/          # REST controllers (Health, Metrics, APIs)
│   │   │   ├── service/             # Business logic (AWS, Blogging, Messaging)
│   │   │   ├── model/               # Data models
│   │   │   ├── repository/          # MongoDB repositories
│   │   │   ├── DTO/                 # Data transfer objects
│   │   │   ├── config/              # Spring configurations
│   │   │   └── TestingApplication.java
│   │   ├── proto/                   # gRPC Protocol Buffer contract files
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── mvnw, mvnw.cmd
```

---

## 🧪 Testing

Run tests:
```bash
mvn test
```

Health check validation:
```bash
curl http://localhost:8080/api/health/ping
curl http://localhost:8080/api/health/detailed
```

Metrics check:
```bash
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```

---

## 🤝 AWS API Gateway Compatibility

- ✅ Health endpoint returns proper HTTP status codes
- ✅ CORS headers configured for cross-origin requests
- ✅ Timeout-friendly response patterns
- ✅ Proper error handling for gateway requests


---

## 📝 License

Licensed under MIT License. See LICENSE file for details.

---

## 📧 Support & Documentation

- **API Docs:** http://localhost:8080/swagger-ui.html
- **Metrics:** http://localhost:8080/actuator/prometheus
- **Health Check:** http://localhost:8080/api/health/ping

---

**Last Updated:** December 2025  
**Version:** 0.0.1-SNAPSHOT

