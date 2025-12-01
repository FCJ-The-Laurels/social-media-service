# 🔧 gRPC Connectivity Troubleshooting Guide

## Problem: Backend Can't Fetch Data from gRPC Service

### Quick Diagnostics (Run These First)

#### 1. Check if gRPC Service is Running
```powershell
# Check if port 9090 is listening
netstat -ano | findstr :9090

# Expected output if service is running:
# TCP    0.0.0.0:9090           0.0.0.0:0              LISTENING       12345
# TCP    [::]:9090              [::]:0                 LISTENING       12345
```

**If nothing appears**: The user-info gRPC service is NOT running!

#### 2. Test gRPC Health from Your Application
```bash
# After starting your Spring Boot app, call:
curl http://localhost:8080/api/health/grpc

# Expected if gRPC is working:
{
  "status": "UP",
  "service": "user-info-grpc",
  "timestamp": "2025-11-30T10:00:00"
}

# Expected if gRPC is NOT working:
{
  "status": "DOWN",
  "service": "user-info-grpc",
  "error": "gRPC service is not responding on configured address"
}
```

#### 3. Test Fetching a Specific User
```bash
# Replace {userId} with an actual user ID from your database
curl http://localhost:8080/api/health/grpc/test/{userId}

# Example:
curl http://localhost:8080/api/health/grpc/test/550e8400-e29b-41d4-a716-446655440000

# Expected SUCCESS response:
{
  "status": "SUCCESS",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "avatar": "https://example.com/avatar.jpg"
}

# Expected FAILURE responses:
# User not found:
{
  "status": "FAILED",
  "userId": "...",
  "error": "User info returned null - user may not exist or service unavailable"
}

# Service unavailable:
{
  "status": "ERROR",
  "userId": "...",
  "error": "UNAVAILABLE: io exception",
  "stackTrace": "io.grpc.StatusRuntimeException"
}
```

---

## Common Issues and Solutions

### Issue 1: gRPC Service Not Running ❌
**Symptoms:**
- Logs show: "gRPC server UNAVAILABLE"
- Health check returns status: "DOWN"
- `netstat` shows no listener on port 9090

**Solution:**
1. Start your user-info gRPC service
2. Verify it's configured to listen on port 9090
3. Check the service's logs for startup errors

```bash
# If using Spring Boot for user-info service:
# Check application.properties or application.yml
grpc.server.port=9090
```

---

### Issue 2: Wrong gRPC Server Address 🔌
**Symptoms:**
- Connection timeouts
- Logs show: "gRPC call TIMEOUT"
- Service is running but can't connect

**Solution:**
Check `application.properties` in your social media service:

```properties
# Current configuration:
grpc.client.user-service.address=localhost:9090
grpc.client.user-service.timeout=5

# If user-info service is on different host:
grpc.client.user-service.address=192.168.1.100:9090

# If using Docker:
grpc.client.user-service.address=user-info-service:9090

# If using Kubernetes:
grpc.client.user-service.address=user-info-service.default.svc.cluster.local:9090
```

Then restart your application.

---

### Issue 3: Firewall Blocking Connection 🔥
**Symptoms:**
- Service is running
- `netstat` shows listener
- Still can't connect

**Solution (Windows):**
```powershell
# Add firewall rule to allow port 9090
New-NetFirewallRule -DisplayName "gRPC User Service" -Direction Inbound -LocalPort 9090 -Protocol TCP -Action Allow

# Or disable firewall temporarily for testing:
Set-NetFirewallProfile -Profile Domain,Public,Private -Enabled False
```

**Solution (Linux/Mac):**
```bash
# Check if port is accessible
telnet localhost 9090

# Or use nc (netcat)
nc -zv localhost 9090
```

---

### Issue 4: User Info Service Database Not Connected 💾
**Symptoms:**
- gRPC service is running
- Health check passes
- User fetch returns null or NOT_FOUND

**Solution:**
Check the user-info service's database connection:
1. Verify MongoDB/PostgreSQL connection in user-info service
2. Check if user data exists in the database
3. Verify user IDs match between services (UUID format)

---

### Issue 5: Proto File Mismatch 📄
**Symptoms:**
- Connection works but data is malformed
- Null values for name/avatar
- Serialization errors

**Solution:**
Ensure BOTH services use the same proto file:

```protobuf
// user_info.proto - MUST BE IDENTICAL in both services
message BlogUserInfoRequest{
  string id=1;  // ⚠️ Must be 1, not 2!
}

message BlogUserInfoResponse{
  string name=1;    // ⚠️ Must be 1, not 2!
  string avatar=2;  // ⚠️ Must be 2, not 3!
}
```

After updating proto:
```bash
# Regenerate classes
mvn clean compile

# Restart both services
```

---

### Issue 6: Network Timeout Too Short ⏱️
**Symptoms:**
- Intermittent failures
- Logs show "DEADLINE_EXCEEDED"
- Works sometimes, fails other times

**Solution:**
Increase timeout in `application.properties`:

```properties
# Current (5 seconds)
grpc.client.user-service.timeout=5

# Increase to 10 seconds
grpc.client.user-service.timeout=10
```

---

## Debugging Steps (Follow in Order)

### Step 1: Check Application Logs on Startup
```
Look for these log messages when your app starts:

✅ Good signs:
=================================================================
🚀 Initializing gRPC channel to localhost:9090 with timeout 5s
=================================================================
✅ gRPC channel initialized successfully
📡 Target: localhost:9090
⏱️  Timeout: 5s
✅ gRPC server is REACHABLE and HEALTHY
=================================================================

❌ Bad signs:
⚠️  gRPC server is NOT REACHABLE - Please check if user-info service is running on localhost:9090
```

### Step 2: Enable DEBUG Logging
Add to `application.properties`:
```properties
logging.level.FCJLaurels.awsrek.service.UserGrpcClientService=DEBUG
logging.level.io.grpc=DEBUG
```

### Step 3: Check Individual Blog Fetch
```bash
# Get a blog that should trigger gRPC call
curl http://localhost:8080/blogs/newest/paginated?page=0&size=1

# Watch logs for:
📡 Fetching user info for userId: 550e8400-...
✅ Successfully fetched user info - name: 'John Doe', avatar: 'https://...'

# Or errors like:
❌ gRPC call TIMEOUT (5s exceeded) for userId: ...
🔌 gRPC server UNAVAILABLE for userId: ... - Is the service running on localhost:9090?
```

### Step 4: Test Direct gRPC Call
Use the health endpoint:
```bash
# Test with a known user ID
curl http://localhost:8080/api/health/grpc/test/YOUR_USER_ID

# Check the response and logs
```

### Step 5: Verify Network Connectivity
```powershell
# Test TCP connection to gRPC service
Test-NetConnection -ComputerName localhost -Port 9090

# Expected output if working:
TcpTestSucceeded : True
```

---

## Application Restart Checklist

After making ANY configuration changes:

1. ✅ Stop the social media service
2. ✅ Verify user-info gRPC service is running: `netstat -ano | findstr :9090`
3. ✅ Start the social media service
4. ✅ Check startup logs for gRPC initialization
5. ✅ Test health endpoint: `curl http://localhost:8080/api/health/grpc`
6. ✅ Test fetching blogs: `curl http://localhost:8080/blogs/newest/paginated?page=0&size=10`

---

## Still Not Working?

### Enable Maximum Logging
```properties
logging.level.root=DEBUG
logging.level.io.grpc.netty=TRACE
logging.level.io.netty=DEBUG
```

### Collect Diagnostic Information
```bash
# 1. Check gRPC server is running
netstat -ano | findstr :9090

# 2. Test connectivity
Test-NetConnection -ComputerName localhost -Port 9090

# 3. Check application health
curl http://localhost:8080/api/health/grpc
curl http://localhost:8080/api/health/system

# 4. Get full logs
# (Copy the console output when starting the app)
```

### Provide These Details for Help:
1. gRPC service status: Running/Not Running
2. `netstat` output for port 9090
3. Health check response from `/api/health/grpc`
4. Startup logs from social media service
5. Configuration from `application.properties`:
   - `grpc.client.user-service.address`
   - `grpc.client.user-service.timeout`

---

## Quick Test Commands Summary

```bash
# 1. Check if gRPC service is running
netstat -ano | findstr :9090

# 2. Test gRPC health
curl http://localhost:8080/api/health/grpc

# 3. Test user fetch
curl http://localhost:8080/api/health/grpc/test/{userId}

# 4. Test blog fetch (should trigger gRPC)
curl http://localhost:8080/blogs/newest/paginated?page=0&size=1

# 5. Check system info
curl http://localhost:8080/api/health/system
```

---

## Expected Working Flow

1. **Social media service starts** → Initializes gRPC channel
2. **gRPC channel connects** → To user-info service on localhost:9090
3. **Health check runs** → Verifies connection is working
4. **Blog API called** → `/blogs/newest/paginated`
5. **For each blog** → Calls `userGrpcClientService.getUserInfo(authorId)`
6. **gRPC call made** → To user-info service
7. **Response received** → BlogUserInfoResponse with name and avatar
8. **Blog display created** → With author info from gRPC
9. **Response returned** → With complete blog information

Any break in this chain will cause the issue you're experiencing!

