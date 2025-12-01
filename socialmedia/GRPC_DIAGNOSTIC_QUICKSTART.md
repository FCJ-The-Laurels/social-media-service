# 🔍 gRPC Connectivity Diagnostic Tools - Quick Start

## Problem: Can't fetch user info from gRPC service?

Use these tools to diagnose and fix the issue quickly!

---

## 🚀 Quick Diagnosis (30 seconds)

### Option 1: Run PowerShell Diagnostic Script (Recommended)
```powershell
# In PowerShell, navigate to project directory and run:
cd C:\FPTU\6\awsrek\socialmedia
.\test-grpc.ps1
```

**This script will:**
- ✅ Check if gRPC service is running (port 9090)
- ✅ Check if Spring Boot app is running (port 8080)
- ✅ Test TCP connection to gRPC service
- ✅ Test gRPC health check
- ✅ Test fetching a blog with author info
- ✅ Show system information
- ✅ Provide next steps if issues found

### Option 2: Manual Health Check
```bash
# 1. Check if gRPC service is running
netstat -ano | findstr :9090

# 2. Test gRPC health from your app
curl http://localhost:8080/api/health/grpc

# 3. Test fetching user info
curl http://localhost:8080/api/health/grpc/test/{userId}
```

---

## 📋 New Diagnostic Endpoints

### 1. gRPC Health Check
```bash
GET http://localhost:8080/api/health/grpc
```

**Response when working:**
```json
{
  "status": "UP",
  "service": "user-info-grpc",
  "timestamp": "2025-11-30T10:00:00"
}
```

**Response when NOT working:**
```json
{
  "status": "DOWN",
  "service": "user-info-grpc",
  "error": "gRPC service is not responding on configured address",
  "timestamp": "2025-11-30T10:00:00"
}
```

### 2. Test User Info Fetch
```bash
GET http://localhost:8080/api/health/grpc/test/{userId}
```

Example:
```bash
curl http://localhost:8080/api/health/grpc/test/550e8400-e29b-41d4-a716-446655440000
```

**Response when working:**
```json
{
  "status": "SUCCESS",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "avatar": "https://example.com/avatar.jpg"
}
```

**Response when user not found:**
```json
{
  "status": "FAILED",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "error": "User info returned null - user may not exist or service unavailable"
}
```

**Response when service error:**
```json
{
  "status": "ERROR",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "error": "UNAVAILABLE: io exception",
  "stackTrace": "io.grpc.StatusRuntimeException"
}
```

### 3. System Information
```bash
GET http://localhost:8080/api/health/system
```

**Response:**
```json
{
  "timestamp": "2025-11-30T10:00:00",
  "javaVersion": "21.0.1",
  "osName": "Windows 11",
  "osVersion": "10.0",
  "availableProcessors": 8,
  "maxMemory": "4096 MB",
  "totalMemory": "256 MB",
  "freeMemory": "128 MB"
}
```

---

## 🔧 Enhanced Logging

The gRPC client now has enhanced logging with emojis for easy identification:

### Startup Logs
```
=================================================================
🚀 Initializing gRPC channel to localhost:9090 with timeout 5s
=================================================================
✅ gRPC channel initialized successfully
📡 Target: localhost:9090
⏱️  Timeout: 5s
✅ gRPC server is REACHABLE and HEALTHY
=================================================================
```

### Runtime Logs
```
📡 Fetching user info for userId: 550e8400-...
✅ Successfully fetched user info - name: 'John Doe', avatar: 'https://...'
```

### Error Logs
```
❌ gRPC call TIMEOUT (5s exceeded) for userId: ...
💡 TIP: Check if user-info service is slow or check network latency

🔌 gRPC server UNAVAILABLE for userId: ... - Is the service running on localhost:9090?
💡 TIP: Verify user-info service is running with: netstat -ano | findstr :9090

🔍 User NOT FOUND for userId: ... - User may not exist in database
```

---

## 📚 Documentation Files

1. **GRPC_TROUBLESHOOTING.md** - Complete troubleshooting guide
   - Common issues and solutions
   - Step-by-step debugging
   - Configuration examples
   - Expected vs actual behavior

2. **test-grpc.ps1** - Automated diagnostic script
   - Runs all health checks
   - Tests connectivity
   - Provides actionable feedback

3. **This file** - Quick reference guide

---

## 🎯 Most Common Issues (90% of cases)

### Issue #1: gRPC Service Not Running
**Quick Fix:**
```bash
# Check if service is running
netstat -ano | findstr :9090

# If nothing shows up, start your user-info service
```

### Issue #2: Wrong Server Address
**Quick Fix:**
Check `application.properties`:
```properties
grpc.client.user-service.address=localhost:9090
```

Make sure it matches where your user-info service is running.

### Issue #3: Timeout Too Short
**Quick Fix:**
Increase timeout in `application.properties`:
```properties
grpc.client.user-service.timeout=10
```

### Issue #4: User Doesn't Exist
**Quick Fix:**
Make sure the user ID exists in your user-info database:
```bash
# Test with a valid user ID
curl http://localhost:8080/api/health/grpc/test/{valid-user-id}
```

---

## 🔄 Restart Checklist

After making changes:

1. ✅ Stop Spring Boot application
2. ✅ Verify user-info gRPC service is running
3. ✅ Start Spring Boot application
4. ✅ Check startup logs for gRPC initialization
5. ✅ Run: `.\test-grpc.ps1`
6. ✅ Verify health check passes
7. ✅ Test fetching blogs

---

## 💡 Pro Tips

1. **Enable DEBUG logging** for detailed diagnostics:
   ```properties
   logging.level.FCJLaurels.awsrek.service.UserGrpcClientService=DEBUG
   logging.level.io.grpc=DEBUG
   ```

2. **Use the diagnostic script** before debugging manually:
   ```powershell
   .\test-grpc.ps1
   ```

3. **Check logs on BOTH services**:
   - Social media service (this app)
   - User-info gRPC service

4. **Verify proto files match** on both services:
   ```protobuf
   message BlogUserInfoResponse{
     string name=1;    // Must be 1!
     string avatar=2;  // Must be 2!
   }
   ```

5. **Test incrementally**:
   - First: Health check
   - Second: Fetch single user
   - Third: Fetch blogs with user info

---

## 🆘 Still Having Issues?

1. Run the diagnostic script:
   ```powershell
   .\test-grpc.ps1
   ```

2. Read the full troubleshooting guide:
   ```
   GRPC_TROUBLESHOOTING.md
   ```

3. Check application logs with DEBUG enabled

4. Collect diagnostic info:
   ```bash
   # gRPC service status
   netstat -ano | findstr :9090
   
   # Health check
   curl http://localhost:8080/api/health/grpc
   
   # System info
   curl http://localhost:8080/api/health/system
   
   # Test user fetch
   curl http://localhost:8080/api/health/grpc/test/{userId}
   ```

5. Verify configuration in `application.properties`:
   - `grpc.client.user-service.address`
   - `grpc.client.user-service.timeout`

---

## ✅ Success Indicators

You'll know it's working when:

1. ✅ Health check returns `"status": "UP"`
2. ✅ Test user fetch returns name and avatar
3. ✅ Blog fetch shows real author names (not "Unknown User")
4. ✅ Logs show: `✅ Successfully fetched user info`
5. ✅ No timeout or unavailable errors in logs

---

## 📞 Quick Command Reference

```bash
# Run diagnostic script
.\test-grpc.ps1

# Manual checks
netstat -ano | findstr :9090
curl http://localhost:8080/api/health/grpc
curl http://localhost:8080/api/health/grpc/test/{userId}
curl http://localhost:8080/blogs/newest/paginated?page=0&size=1

# Enable debug logging
# Add to application.properties:
logging.level.FCJLaurels.awsrek.service.UserGrpcClientService=DEBUG
```

Good luck! 🚀

