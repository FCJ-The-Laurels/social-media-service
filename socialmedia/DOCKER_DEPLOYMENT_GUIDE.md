# Docker Deployment Guide

## 📦 Docker Files Created

Your project now includes:
- **Dockerfile** - Multi-stage optimized Docker image
- **docker-compose.yml** - Full stack with local MongoDB
- **docker-compose.prod.yml** - Production setup with cloud MongoDB
- **.dockerignore** - Optimizes build process
- **.env.example** - Environment variables template

## 🚀 Quick Start

### Option 1: Using Cloud MongoDB (Recommended)

```bash
# Build and run with your existing cloud MongoDB
docker build -t socialmedia-api .
docker run -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI="mongodb+srv://LTadmin:xiaomi14T!@messaging.wpz9cv0.mongodb.net/?retryWrites=true&w=majority&appName=messaging" \
  socialmedia-api
```

### Option 2: Using Docker Compose (Local MongoDB)

```bash
# Start all services (app + local MongoDB)
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down
```

### Option 3: Production Setup

```bash
# Create .env file
cp .env.example .env
# Edit .env with your production values

# Start with production config
docker-compose -f docker-compose.prod.yml up -d
```

## 🔧 Detailed Instructions

### 1. Build the Docker Image

```bash
# Build the image
docker build -t socialmedia-api:latest .

# Build with specific tag
docker build -t socialmedia-api:1.0.0 .

# Build with no cache
docker build --no-cache -t socialmedia-api:latest .
```

### 2. Run the Container

**Basic run:**
```bash
docker run -d \
  --name socialmedia-api \
  -p 8080:8080 \
  socialmedia-api:latest
```

**With environment variables:**
```bash
docker run -d \
  --name socialmedia-api \
  -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI="your-mongodb-uri" \
  -e JWT_SECRET="your-secret-key" \
  -e CORS_ALLOWED_ORIGINS="http://localhost:3000" \
  socialmedia-api:latest
```

**With environment file:**
```bash
docker run -d \
  --name socialmedia-api \
  -p 8080:8080 \
  --env-file .env \
  socialmedia-api:latest
```

### 3. Using Docker Compose

**Start services:**
```bash
# Start in detached mode
docker-compose up -d

# Start with rebuild
docker-compose up -d --build

# Start specific service
docker-compose up -d app
```

**View logs:**
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f app

# Last 100 lines
docker-compose logs --tail=100 -f app
```

**Stop services:**
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Stop and remove images
docker-compose down --rmi all
```

**Scale services:**
```bash
# Run multiple instances
docker-compose up -d --scale app=3
```

### 4. Docker Compose with MongoDB

The `docker-compose.yml` includes a local MongoDB instance:

```yaml
services:
  app:
    - Connected to local MongoDB
    - Waits for MongoDB to be healthy
  mongodb:
    - MongoDB 7.0
    - Port 27017
    - Persistent data storage
```

**Access MongoDB:**
```bash
# Connect to MongoDB container
docker exec -it socialmedia-mongodb mongosh

# Use database
use awsrek_db

# List collections
show collections
```

### 5. Monitoring Stack

The compose file includes Prometheus and Grafana:

**Access:**
- Application: http://localhost:8080
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin)

**Setup Grafana:**
1. Login to Grafana (admin/admin)
2. Add Prometheus data source: http://prometheus:9090
3. Import Spring Boot dashboard (ID: 11378)

## 🔍 Verification & Testing

### Check Container Status
```bash
# List running containers
docker ps

# Check specific container
docker ps -f name=socialmedia-api

# Check container health
docker inspect --format='{{.State.Health.Status}}' socialmedia-api
```

### Test the Application
```bash
# Health check
curl http://localhost:8080/actuator/health

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus

# API test
curl http://localhost:8080/api/blogs
```

### View Container Logs
```bash
# Follow logs
docker logs -f socialmedia-api

# Last 100 lines
docker logs --tail=100 socialmedia-api

# Since 10 minutes ago
docker logs --since=10m socialmedia-api
```

### Enter Container Shell
```bash
# Open bash shell
docker exec -it socialmedia-api bash

# Or sh if bash not available
docker exec -it socialmedia-api sh
```

## 🐛 Troubleshooting

### Container Won't Start

**Check logs:**
```bash
docker logs socialmedia-api
```

**Common issues:**
- MongoDB connection refused → Check MONGODB_URI
- Port already in use → Change port mapping
- Out of memory → Increase Docker memory limit

### Container Starts but Not Accessible

**Check port binding:**
```bash
docker port socialmedia-api
```

**Check network:**
```bash
docker network inspect socialmedia-network
```

### MongoDB Connection Issues

**Test MongoDB connectivity:**
```bash
# From host
docker exec socialmedia-mongodb mongosh --eval "db.adminCommand('ping')"

# From app container
docker exec socialmedia-api curl -f http://localhost:8080/actuator/health
```

### Application Errors

**View full logs:**
```bash
docker-compose logs --tail=500 app
```

**Restart container:**
```bash
docker-compose restart app
```

## 🔐 Security Best Practices

### 1. Use Environment Variables
Never hardcode sensitive data. Use environment variables or secrets:

```bash
# Use .env file
docker-compose --env-file .env.prod up -d

# Or pass via command line
docker run -e JWT_SECRET="${JWT_SECRET}" ...
```

### 2. Use Non-Root User
The Dockerfile already uses a non-root user (spring:spring)

### 3. Limit Resources
```yaml
deploy:
  resources:
    limits:
      cpus: '2'
      memory: 2G
```

### 4. Use Health Checks
Already configured in Dockerfile and docker-compose.yml

## 📊 Resource Management

### Check Resource Usage
```bash
# All containers
docker stats

# Specific container
docker stats socialmedia-api

# Once (no streaming)
docker stats --no-stream
```

### Cleanup

**Remove stopped containers:**
```bash
docker container prune
```

**Remove unused images:**
```bash
docker image prune -a
```

**Remove unused volumes:**
```bash
docker volume prune
```

**Complete cleanup:**
```bash
docker system prune -a --volumes
```

## 🚀 Production Deployment

### 1. Use Production Compose File
```bash
# Start production stack
docker-compose -f docker-compose.prod.yml up -d
```

### 2. Configure Environment Variables
```bash
# Create .env file with production values
cp .env.example .env
# Edit .env
```

### 3. Enable HTTPS (with reverse proxy)
Use nginx or traefik as reverse proxy with SSL certificates

### 4. Setup Container Orchestration
For production at scale, consider:
- **Docker Swarm** - Built-in orchestration
- **Kubernetes** - Industry standard
- **AWS ECS/EKS** - Cloud-managed

## 📈 Performance Optimization

### JVM Tuning
Already configured in Dockerfile:
- UseContainerSupport
- MaxRAMPercentage=75%
- G1GC garbage collector

### Adjust Memory
```bash
# Run with more memory
docker run -m 2g socialmedia-api
```

### Multi-instance Setup
```bash
# Run behind load balancer
docker-compose up -d --scale app=3
```

## 🔄 CI/CD Integration

### GitHub Actions Example
```yaml
- name: Build Docker Image
  run: docker build -t myorg/socialmedia-api:${{ github.sha }} .

- name: Push to Registry
  run: docker push myorg/socialmedia-api:${{ github.sha }}
```

### Jenkins Example
```groovy
stage('Build') {
    docker.build("socialmedia-api:${BUILD_NUMBER}")
}
```

## 📝 Useful Commands Cheatsheet

```bash
# Build
docker build -t socialmedia-api .

# Run
docker run -d -p 8080:8080 socialmedia-api

# Start compose
docker-compose up -d

# Stop compose
docker-compose down

# Logs
docker-compose logs -f app

# Rebuild
docker-compose up -d --build

# Scale
docker-compose up -d --scale app=3

# Health check
curl http://localhost:8080/actuator/health

# Enter container
docker exec -it socialmedia-api sh

# View stats
docker stats

# Cleanup
docker system prune -a
```

## ✅ Deployment Checklist

Before deploying to production:

- [ ] Environment variables configured in .env
- [ ] JWT_SECRET is strong and unique
- [ ] MongoDB connection string is correct
- [ ] CORS origins are properly set
- [ ] Logging level is appropriate (WARN/ERROR)
- [ ] Health checks are working
- [ ] Resource limits are configured
- [ ] Backup strategy for data
- [ ] Monitoring is set up (Prometheus/Grafana)
- [ ] SSL/TLS configured (via reverse proxy)
- [ ] Security scanning completed

## 🎉 Success!

Your application is now containerized and ready to deploy anywhere Docker runs!

**Access your application:**
- API: http://localhost:8080
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus
- Swagger: http://localhost:8080/swagger-ui.html
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

