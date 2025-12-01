# gRPC BlogUserInfo() Call Flow Documentation

## Overview
This document explains how the blog service calls the `blogUserInfo()` RPC method from the user service to fetch author information (name and avatar).

## Call Flow

### 1. **Request Origin: BlogServiceImpl.mapToBlogDisplay()**
- Located in: `src/main/java/FCJLaurels/awsrek/service/blogging/BlogServiceImpl.java`
- Triggered when: Converting a blog entity to BlogDisplay DTO
- Purpose: Enrich blog data with author information

### 2. **Flow Diagram**

```
┌─────────────────────────────────────────────────────────────────┐
│ BlogServiceImpl.mapToBlogDisplay()                               │
│ - Receives: blog entity with author UUID                        │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│ UUID to String Conversion                                       │
│ - entity.getAuthor().toString()                                 │
│ - Converts: UUID → "550e8400-e29b-41d4-a716-446655440000"      │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│ UserGrpcClientService.getUserInfo(authorIdString)               │
│ - Takes: String userId (UUID format)                            │
│ - Returns: BlogUserInfoResponse | null                          │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│ BlogUserInfoRequest Creation                                    │
│ {                                                               │
│   "id": "550e8400-e29b-41d4-a716-446655440000"                │
│ }                                                               │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│ gRPC Call (BLOCKING)                                            │
│ blockingStub.blogUserInfo(request)                              │
│ - Target: User Service on localhost:9090 (or configured)        │
│ - Timeout: 5 seconds (configurable)                             │
│ - Method: UserInfoService.blogUserInfo()                        │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│ User Service Processing (in user-info service)                  │
│ - Receives: BlogUserInfoRequest with UUID string                │
│ - Converts: String UUID → UUID object                           │
│ - Queries: Database for user by userId                          │
│ - Returns: BlogUserInfoResponse                                 │
│   {                                                             │
│     "name": "John Doe",                                         │
│     "avatar": "https://s3.../avatar.jpg"                        │
│   }                                                             │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│ Response Processing in BlogServiceImpl                           │
│ - Checks: if response != null                                   │
│ - Extracts:                                                     │
│   - authorName = response.getName()                             │
│   - authorAvatar = response.getAvatar()                         │
│ - Handles: Empty/null values with defaults                      │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│ BlogDisplay DTO Creation                                        │
│ {                                                               │
│   "title": "My Blog Post",                                      │
│   "content": "...",                                             │
│   "authorName": "John Doe",    ◄── FROM GRPC CALL              │
│   "authorAvatar": "https://...",   ◄── FROM GRPC CALL          │
│   "imageURL": "...",                                            │
│   "creationDate": "2025-12-01T..."                              │
│ }                                                               │
└─────────────────────────────────────────────────────────────────┘
```

## Proto Contract

### BlogUserInfoRequest (Input)
```protobuf
message BlogUserInfoRequest {
  string id = 1;  // UUID as string: "550e8400-e29b-41d4-a716-446655440000"
}
```

### BlogUserInfoResponse (Output)
```protobuf
message BlogUserInfoResponse {
  string name = 1;      // User's full name (e.g., "John Doe")
  string avatar = 2;    // User's avatar URL (e.g., "https://s3.../avatar.jpg")
}
```

### RPC Definition
```protobuf
service UserInfoService {
  rpc BlogUserInfo(BlogUserInfoRequest) returns (BlogUserInfoResponse);
}
```

## Key Implementation Details

### UUID Handling
- **Input**: Blog entity contains `author` as UUID object
- **Conversion**: `entity.getAuthor().toString()` → String format
- **Format**: "550e8400-e29b-41d4-a716-446655440000"
- **Transmission**: Sent as string in gRPC request
- **Server-side**: User service converts string UUID back to UUID object

### Error Handling
| Error Scenario | Handling | Result |
|---|---|---|
| User not found (NOT_FOUND) | Logged as warning | `authorName = "Unknown User"` |
| Timeout (DEADLINE_EXCEEDED) | Logged as error | Falls back to defaults |
| Server unavailable (UNAVAILABLE) | Logged as error | Falls back to defaults |
| Any other exception | Logged as error | `authorName = "Unknown User"`, increments error metrics |

### Data Flow Summary

```
Blog Entity UUID (Java Object)
    ↓
toString() conversion
    ↓
"550e8400-e29b-41d4-a716-446655440000" (String)
    ↓
BlogUserInfoRequest created with string ID
    ↓
gRPC call over network
    ↓
User Service receives string ID
    ↓
User Service converts to UUID (server-side)
    ↓
User Service queries database
    ↓
Returns BlogUserInfoResponse with name and avatar
    ↓
Blog Service extracts data
    ↓
BlogDisplay DTO enriched with author info
```

## Configuration

### Required Properties (application.properties)
```properties
grpc.client.user-service.address=localhost:9090
grpc.client.user-service.timeout=5
```

### gRPC Channel Settings
- **Address Format**: `host:port`
- **Connection Type**: Plaintext (non-SSL)
- **Timeout**: 5 seconds per call
- **Retry Attempts**: 3
- **KeepAlive**: 30 seconds

## Code Example

### How it's called in BlogServiceImpl:
```java
// 1. Get blog entity with author UUID
blog entity = blogRepository.findById("blog-123");

// 2. Convert UUID to String
String authorIdString = entity.getAuthor().toString();
// Result: "550e8400-e29b-41d4-a716-446655440000"

// 3. Call gRPC service (internally)
BlogUserInfoResponse userInfo = userGrpcClientService.getUserInfo(authorIdString);

// 4. Extract response
String authorName = userInfo.getName();    // "John Doe"
String authorAvatar = userInfo.getAvatar(); // "https://..."

// 5. Build enriched BlogDisplay
BlogDisplay display = BlogDisplay.builder()
    .authorName(authorName)
    .authorAvatar(authorAvatar)
    .title(entity.getTitle())
    .build();
```

## Logging Output

When successful:
```
📞 Calling gRPC blogUserInfo() to fetch user info for author UUID: 550e8400-e29b-41d4-a716-446655440000
✅ gRPC blogUserInfo() fetched successfully - name: 'John Doe', avatar: 'https://s3.../avatar.jpg'
```

When user not found:
```
📞 Calling gRPC blogUserInfo() to fetch user info for author UUID: 550e8400-e29b-41d4-a716-446655440000
⚠️ gRPC blogUserInfo() returned null response for author UUID: 550e8400-e29b-41d4-a716-446655440000
```

## Performance Considerations

### Blocking Nature
- **Type**: Synchronous/Blocking call
- **Impact**: Thread waits for gRPC response
- **Timeout**: Max 5 seconds per call
- **Concurrency**: Limited by thread pool size

### Optimization Strategies
1. **Caching**: Cache user info for frequently requested authors
2. **Batching**: Load multiple authors in one call if service supports it
3. **Async**: Convert to reactive/async if high concurrency needed
4. **Fallback**: Implement circuit breaker to fail fast

## Troubleshooting

### Issue: "Cannot invoke... because 'this.logger' is null"
- **Cause**: Logger not properly initialized
- **Fix**: Ensure `@Slf4j` annotation present on class

### Issue: gRPC call times out
- **Cause**: User service slow or unresponsive
- **Fix**: Check user service is running on configured port, increase timeout

### Issue: User not found even though user exists
- **Cause**: UUID format mismatch or user lookup issue
- **Fix**: Verify UUID format and user service database

### Issue: NullPointerException in response handling
- **Cause**: UserGrpcClientService not injected
- **Fix**: Ensure `@Autowired private UserGrpcClientService userGrpcClientService;`

## Future Improvements

1. **Caching Layer**: Add Redis cache for user info (TTL: 1 hour)
2. **Batch gRPC**: Modify proto to accept list of UUIDs for batch calls
3. **Circuit Breaker**: Add Resilience4j or Hystrix for fault tolerance
4. **Reactive**: Convert to Project Reactor for non-blocking calls
5. **Metrics**: Track gRPC call duration, success rate, errors
6. **Tracing**: Add distributed tracing (Jaeger/Zipkin) for debugging

## Related Files

- **Service**: `src/main/java/FCJLaurels/awsrek/service/UserGrpcClientService.java`
- **Impl**: `src/main/java/FCJLaurels/awsrek/service/blogging/BlogServiceImpl.java`
- **Proto**: `src/main/proto/user_info.proto`
- **Config**: `src/main/resources/application.properties`
- **DTO**: `src/main/java/FCJLaurels/awsrek/DTO/blogDTO/BlogDisplay.java`

---

**Last Updated**: 2025-12-01
**Status**: ✅ Production Ready

