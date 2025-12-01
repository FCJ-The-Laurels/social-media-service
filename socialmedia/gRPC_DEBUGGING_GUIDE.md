# gRPC Backend Fetching - Debugging & Troubleshooting Guide

## Summary of Fixes Applied

### 1. ✅ Fixed Proto Contract Issue
**File**: `src/main/proto/user_info.proto`
- **Problem**: Field numbering was wrong (started at 2 and 3 instead of 1 and 2)
- **Fix**: Updated `BlogUserInfoResponse` message:
  ```protobuf
  message BlogUserInfoResponse{
    string name=1;      // Changed from 2
    string avatar=2;    // Changed from 3
  }
  ```
- **Impact**: Proper serialization/deserialization of gRPC messages

### 2. ✅ Enhanced UserGrpcClientService
**File**: `src/main/java/FCJLaurels/awsrek/service/UserGrpcClientService.java`

**Improvements**:
- ✅ Added timeout configuration: 5 seconds default
- ✅ Added async methods using `CompletableFuture` for non-blocking calls
- ✅ Added health check mechanism
- ✅ Added connection pooling settings
- ✅ Better error handling for `StatusRuntimeException`
- ✅ Keep-alive settings for stable connections
- ✅ Retry configuration (3 attempts)

**New Methods**:
```java
// Non-blocking async method (PREFERRED)
CompletableFuture<BlogUserInfoResponse> getUserInfoAsync(String userId)

// Async convenience methods
CompletableFuture<String> getUserNameAsync(String userId)
CompletableFuture<String> getUserAvatarUrlAsync(String userId)

// Health check
boolean isServerHealthy()

// Old blocking methods (still available for compatibility)
BlogUserInfoResponse getUserInfo(String userId)  // DEPRECATED
String getUserName(String userId)                 // DEPRECATED
String getUserAvatarUrl(String userId)            // DEPRECATED
```

### 3. ✅ Updated Configuration
**File**: `src/main/resources/application.properties`
- Removed `static://` prefix from gRPC address (was causing parsing issues)
- Changed from: `grpc.client.user-service.address=static://localhost:9090`
- Changed to: `grpc.client.user-service.address=localhost:9090`
- Added timeout config: `grpc.client.user-service.timeout=5`

### 4. ✅ Enhanced BlogServiceImpl
**File**: `src/main/java/FCJLaurels/awsrek/service/blogging/BlogServiceImpl.java`
- Added async mapping method: `mapToBlogDisplayAsync()`
- Marked old blocking method as `@Deprecated` for future removal
- New method returns `CompletableFuture<BlogDisplay>` for non-blocking operations

---

## Testing & Verification

### Step 1: Verify gRPC Service is Running
```bash
# Check if gRPC service is listening on port 9090
netstat -ano | findstr :9090

# Or use a gRPC health check client
# Install: go install github.com/grpc-ecosystem/grpc-health-probe@latest
grpc-health-probe -addr=localhost:9090
```

### Step 2: Check Application Logs
```bash
# Look for these log messages:
# 1. Initialization log
"Initializing gRPC channel to localhost:9090 with timeout 5s"

# 2. Channel creation log
"gRPC channel initialized successfully"

# 3. Call execution logs
"Fetching user info asynchronously for userId: xxx"
"Successfully fetched user info - name: xxx, avatar: xxx"

# 4. Error logs (if any)
"gRPC call timeout for userId xxx"
"gRPC call failed for userId xxx"
```

### Step 3: Verify Proto Regeneration
```bash
# Regenerate proto classes after fixing proto file
mvn clean compile

# Check if new classes were generated
dir target/generated-sources/protobuf/java-grpc/FCJ/user/grpc/

# Should see:
# - UserInfoServiceGrpc.java
# - BlogUserInfoRequest.java
# - BlogUserInfoResponse.java (updated with correct field numbers)
```

### Step 4: Test gRPC Call
**Via Curl** (if you have grpcurl installed):
```bash
# Install grpcurl: go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest

# List services
grpcurl -plaintext localhost:9090 list

# Call BlogUserInfo
grpcurl -plaintext -d '{"id":"123"}' localhost:9090 userinfo.UserInfoService/BlogUserInfo

# Expected response:
# {
#   "name": "John Doe",
#   "avatar": "https://example.com/avatar.jpg"
# }
```

**Via Java Test**:
```java
@Test
public void testGrpcConnection() throws Exception {
    UserGrpcClientService service = new UserGrpcClientService();
    service.init();
    
    boolean healthy = service.isServerHealthy();
    System.out.println("Server healthy: " + healthy);
    
    CompletableFuture<BlogUserInfoResponse> future = 
        service.getUserInfoAsync("your-user-id");
    
    BlogUserInfoResponse response = future.get(10, TimeUnit.SECONDS);
    System.out.println("User: " + response.getName());
    
    service.shutdown();
}
```

---

## Common Issues & Solutions

### Issue 1: Connection Refused
**Error**: `io.grpc.StatusRuntimeException: UNAVAILABLE: io.grpc.netty.shaded.io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused`

**Solutions**:
1. Verify gRPC service is running on port 9090
2. Check firewall settings
3. Verify address in application.properties: `grpc.client.user-service.address=localhost:9090`
4. Try connecting from another service to verify port is open

```bash
# Test connection
telnet localhost 9090
# Should show: Connected to localhost
```

### Issue 2: Timeout Errors
**Error**: `io.grpc.StatusRuntimeException: DEADLINE_EXCEEDED`

**Solutions**:
1. Increase timeout in application.properties: `grpc.client.user-service.timeout=10` (change to 10 seconds)
2. Check if gRPC service is slow
3. Check network latency
4. Use async methods instead of blocking to avoid thread blocking

### Issue 3: Field Parsing Errors
**Error**: `java.io.IOException: Invalid wire type in tag`

**Solutions** (NOW FIXED):
1. Ensure proto field numbering is correct (should be 1, 2, 3, not 2, 3, 4)
2. Regenerate proto files: `mvn clean compile`
3. Clear target directory: `mvn clean`

### Issue 4: Null Pointer Exception
**Error**: `java.lang.NullPointerException: Cannot get name() of null`

**Solutions**:
1. Add null checks (already done in new async method)
2. Ensure user ID is not null before calling gRPC
3. Check UserGrpcClientService initialization in Spring

### Issue 5: Performance Issues (Slow Pagination)
**Symptoms**: Each paginated request takes N+1 seconds (N = number of blogs)

**Solutions** (FOR FUTURE):
1. Use batch fetching for multiple user IDs
2. Implement caching (LocalCache or Redis)
3. Use CompletableFuture to fetch all users in parallel
4. Consider implementing a separate endpoint for batch user info

---

## Performance Optimization (Next Steps)

### Option A: Caching with @Cacheable
```java
@Cacheable(value = "userInfo", key = "#userId")
public CompletableFuture<BlogUserInfoResponse> getUserInfoAsync(String userId) {
    // ... existing code
}
```

**Configuration needed**:
```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: "maximumSize=10000,expireAfterWrite=1h"
```

### Option B: Batch User Info Fetching
Create a new gRPC method in user-service:
```protobuf
rpc BatchGetUserInfo(BatchGetUserInfoRequest) returns (BatchGetUserInfoResponse);

message BatchGetUserInfoRequest {
  repeated string ids = 1;
}

message BatchGetUserInfoResponse {
  map<string, BlogUserInfoResponse> users = 1;
}
```

### Option C: Circuit Breaker with Resilience4j
```java
@CircuitBreaker(name = "userService", fallbackMethod = "fallback")
public CompletableFuture<BlogUserInfoResponse> getUserInfoAsync(String userId) {
    // ... existing code
}

public CompletableFuture<BlogUserInfoResponse> fallback(String userId, Exception ex) {
    log.warn("Circuit breaker opened for userId: {}", userId);
    return CompletableFuture.completedFuture(
        BlogUserInfoResponse.newBuilder()
            .setName("User #" + userId)
            .build()
    );
}
```

---

## Monitoring & Observability

### Prometheus Metrics for gRPC
Add to your metrics:
```java
private final Counter grpcCallCounter = Counter.builder("grpc.calls")
    .tag("service", "user-info")
    .register(meterRegistry);

private final Timer grpcDuration = Timer.builder("grpc.duration")
    .tag("service", "user-info")
    .register(meterRegistry);
```

### Health Endpoint
Add to application.properties:
```properties
management.endpoint.health.show-details=always
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
```

Add custom health indicator:
```java
@Component
public class GrpcHealthIndicator extends AbstractHealthIndicator {
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        boolean healthy = userGrpcClientService.isServerHealthy();
        builder.status(healthy ? "UP" : "DOWN")
               .withDetail("gRPC service", "localhost:9090");
    }
}
```

---

## Next Steps

1. **Rebuild project**:
   ```bash
   mvn clean install
   ```

2. **Test the changes**:
   ```bash
   mvn test
   ```

3. **Run application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Monitor logs**:
   ```bash
   tail -f target/application.log | grep -i grpc
   ```

5. **Make a test request**:
   ```bash
   curl http://localhost:8080/api/blogs?page=0&size=10
   ```

6. **Check health**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

---

## Questions & Debugging

If you still have issues:

1. **Check the logs** for exact error messages
2. **Verify gRPC service** is running: `netstat -ano | findstr :9090`
3. **Test proto regeneration**: Look for generated files in `target/generated-sources/protobuf/`
4. **Use grpcurl** to manually test gRPC calls
5. **Enable debug logging**: Add `logging.level.FCJLaurels.awsrek=TRACE` to properties
6. **Check network**: Ensure firewall allows port 9090

---

## Version Information
- Spring Boot: 3.5.6
- gRPC: 1.71.0
- Protobuf: 3.25.1
- Java: 25

