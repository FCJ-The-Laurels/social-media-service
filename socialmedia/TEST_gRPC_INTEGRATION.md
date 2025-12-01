# Test Your gRPC Integration Now

## Quick Start Testing

### Prerequisites
1. gRPC service running on `localhost:9090`
2. Spring Boot application running on `localhost:8080`
3. At least one blog in MongoDB with an author

---

## Test Command 1: Basic Request

```bash
curl -X GET "http://localhost:8080/blogs/newest/paginated?page=0&size=10" \
  -H "Content-Type: application/json"
```

### Expected Response (HTTP 200)
```json
{
  "content": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "Blog Title",
      "imageURL": "https://example.com/image.jpg",
      "content": "Blog content here...",
      "creationDate": "2025-11-30T10:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

**Verify**:
- ✅ `"authorName"` is NOT null (from gRPC)
- ✅ `"authorAvatar"` URL is present (from gRPC)
- ✅ HTTP 200 response

---

## Test Command 2: With PowerShell

If using PowerShell:

```powershell
$response = Invoke-WebRequest -Uri "http://localhost:8080/blogs/newest/paginated?page=0&size=10" `
  -Method GET `
  -ContentType "application/json"

$response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
```

---

## Test Command 3: With Page and Size

```bash
# Get second page with 5 items per page
curl -X GET "http://localhost:8080/blogs/newest/paginated?page=1&size=5" \
  -H "Content-Type: application/json" | jq '.'
```

---

## Test Command 4: Check Logs

```bash
# Watch for gRPC calls in real-time
tail -f application.log | grep -i "grpc\|user info\|fetching author"
```

**Expected log output**:
```
DEBUG [testing] Fetching user info via gRPC for author: 550e8400-...
DEBUG [testing] User info fetched - name: John Doe, avatar: https://...
```

---

## Test Command 5: Verify gRPC Service Connection

```bash
# Check if gRPC service is running
netstat -ano | findstr :9090

# Should return something like:
# TCP    127.0.0.1:9090    0.0.0.0:0    LISTENING    12345
```

---

## Test Command 6: Using Postman

1. **Method**: GET
2. **URL**: `http://localhost:8080/blogs/newest/paginated?page=0&size=10`
3. **Headers**:
   - `Content-Type: application/json`
4. **Send Request**
5. **Check Response**:
   - Status: 200
   - Body includes `authorName` and `authorAvatar`

---

## Test Command 7: Using gRPC Health Check

If you have grpcurl installed:

```bash
# Install grpcurl first:
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest

# Test the gRPC service
grpcurl -plaintext localhost:9090 list

# Should output:
# FCJ.user.grpc.UserInfoService
# grpc.reflection.v1.ServerReflection
# grpc.reflection.v1alpha.ServerReflection
```

---

## Test Command 8: Trace Request Flow

```bash
# In one terminal, watch logs:
tail -f application.log | grep "gRPC\|blogService\|mapToBlog"

# In another terminal, make request:
curl "http://localhost:8080/blogs/newest/paginated?page=0&size=2"

# In logs, you should see:
# 1. "Fetching newest blogs with pagination"
# 2. "Fetching user info via gRPC for author: ..." (twice, for 2 blogs)
# 3. "User info fetched - name: ..., avatar: ..."
# 4. "Retrieved 2 newest blogs for page 0"
```

---

## What to Check

### ✅ Success Indicators
- [ ] HTTP 200 response
- [ ] Response contains `"content"` array
- [ ] Each item has `"authorName"` (not null)
- [ ] Each item has `"authorAvatar"` (URL or null)
- [ ] Logs show "Fetching user info via gRPC"
- [ ] Logs show "User info fetched"

### ❌ Problem Indicators
- [ ] HTTP 500 error → Check application logs
- [ ] `"authorName": null` → gRPC service not responding
- [ ] `"authorName": "Unknown User"` → gRPC service returned null
- [ ] No gRPC logs → gRPC client not being called

---

## Debugging if Something's Wrong

### Problem 1: gRPC Service Connection Refused

**Error**: `Connection refused` or `UNAVAILABLE`

**Solution**:
```bash
# 1. Check if gRPC service is running
netstat -ano | findstr :9090

# 2. If not running, start it first (depends on your setup)

# 3. Verify port is correct in application.properties:
# grpc.client.user-service.address=localhost:9090

# 4. Try manual gRPC call
grpcurl -plaintext localhost:9090 list
```

### Problem 2: Proto Field Mismatch

**Error**: `Invalid wire type in tag` or deserialization error

**Solution**:
```bash
# 1. Verify proto file has correct field numbers:
cat src/main/proto/user_info.proto | grep -A 5 "BlogUserInfoResponse"

# Should show:
# message BlogUserInfoResponse{
#   string name=1;
#   string avatar=2;
# }

# 2. Rebuild proto files:
mvn clean compile

# 3. Rebuild project:
mvn clean package
```

### Problem 3: Timeout Error

**Error**: `DEADLINE_EXCEEDED`

**Solution**:
```bash
# 1. Increase timeout in application.properties:
# grpc.client.user-service.timeout=10

# 2. Check if gRPC service is slow
# 3. Check network latency

# 4. Restart application after config change
```

### Problem 4: Author Information Not Fetched

**Symptom**: `"authorName": "Unknown User"` in response

**Debugging**:
```bash
# 1. Check logs for errors
tail -f application.log | grep -i error

# 2. Verify gRPC service is returning data
grpcurl -plaintext -d '{"id":"test-user-id"}' localhost:9090 userinfo.UserInfoService/BlogUserInfo

# 3. Check if user exists in User service database
```

---

## Performance Testing

### Test 1: Single Page (10 blogs)
```bash
time curl "http://localhost:8080/blogs/newest/paginated?page=0&size=10" > /dev/null

# Expected: 1-2 seconds
```

### Test 2: Multiple Pages
```bash
for page in {0..5}; do
  echo "Page $page:"
  time curl "http://localhost:8080/blogs/newest/paginated?page=$page&size=10" > /dev/null
done
```

### Test 3: Load Test (Linux/Mac)
```bash
# Install Apache Bench
# apt-get install apache2-utils  (Linux)
# brew install httpd  (Mac)

# Run 100 requests with 5 concurrent
ab -n 100 -c 5 "http://localhost:8080/blogs/newest/paginated?page=0&size=10"
```

---

## Expected Performance Metrics

| Scenario | Response Time | gRPC Calls |
|----------|---------------|-----------|
| 10 blogs/page | 1-2 seconds | 10 |
| 50 blogs/page | 5-10 seconds | 50 |
| 100 blogs/page | 10-20 seconds | 100 |

Each gRPC call: ~100-200ms

---

## Success Checklist

- [ ] gRPC service running on 9090
- [ ] Spring Boot app running on 8080
- [ ] Endpoint returns HTTP 200
- [ ] Response includes `authorName` (not null)
- [ ] Response includes `authorAvatar` (URL)
- [ ] Logs show gRPC calls
- [ ] No errors in logs
- [ ] Response time is reasonable (1-2 seconds for 10 blogs)

---

## If Everything Works ✅

Great! Your gRPC integration is complete and working properly. 

The `getNewestBlogsWithPagination()` endpoint is successfully:
1. ✅ Fetching blogs from MongoDB
2. ✅ Calling gRPC service for each blog's author
3. ✅ Including author name and avatar in response
4. ✅ Handling errors gracefully
5. ✅ Returning paginated results

---

## Next Steps

1. **Deploy to production** (your gRPC + Spring Boot services)
2. **Monitor performance** (track gRPC call times)
3. **Consider optimizations** (batch requests, caching)
4. **Test with real load** (multiple concurrent users)


