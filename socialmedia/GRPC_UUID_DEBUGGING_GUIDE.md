# gRPC UUID Conversion - Debugging Guide

## Quick Test

### 1. Start Your Services
```powershell
# Terminal 1: Start user-info service (gRPC server)
cd C:\FPTU\6\awsrek
mvn spring-boot:run

# Terminal 2: Start social-media service (gRPC client)
cd C:\FPTU\6\awsrek\socialmedia
mvn spring-boot:run
```

### 2. Create a Blog Post

```bash
POST http://localhost:8080/api/blog/create
Authorization: Bearer <YOUR_JWT_TOKEN>
Content-Type: application/json

{
  "title": "Test Blog with UUID Author",
  "content": "Testing UUID to String conversion in gRPC",
  "imageUrl": "https://example.com/image.jpg"
}
```

### 3. Check Logs

Look for these log messages:

**When creating blog:**
```
Creating blog for userId: 550e8400-e29b-41d4-a716-446655440000
Blog created successfully with id: [blog-id]
```

**When fetching blog with author info:**
```
🔄 Converting UUID author to string: 550e8400-e29b-41d4-a716-446655440000
📞 Calling gRPC service to fetch user info for author UUID: 550e8400-e29b-41d4-a716-446655440000
🔍 Fetching user info via gRPC for userId: 550e8400-e29b-41d4-a716-446655440000 (UUID format)
✅ UUID validation passed for: 550e8400-e29b-41d4-a716-446655440000
📤 Sending gRPC request with UUID: 550e8400-e29b-41d4-a716-446655440000
✅ User info fetched successfully - name: 'John Doe', avatar: 'https://...'
```

## Verify UUID Conversion

### Check MongoDB Document
```javascript
db.blog.findOne({}, {author: 1})

// Output should show UUID in MongoDB format:
{
  "_id": "...",
  "author": UUID("550e8400-e29b-41d4-a716-446655440000")
}
```

### Verify String Conversion in Code

The conversion happens here:
```java
// blog entity has UUID
UUID authorUuid = blog.getAuthor();  // 550e8400-e29b-41d4-a716-446655440000

// Convert to String for gRPC
String authorString = authorUuid.toString();  // "550e8400-e29b-41d4-a716-446655440000"

// Send to gRPC
userGrpcClientService.getUserInfo(authorString);
```

## Expected Flow

```
1. Blog.author (UUID in DB)
   ↓
2. entity.getAuthor().toString()
   ↓
3. UserGrpcClientService.getUserInfo(String)
   ↓
4. BlogUserInfoRequest with string ID
   ↓
5. gRPC Server receives string UUID
   ↓
6. gRPC Server converts string → UUID
   ↓
7. Query user database with UUID
   ↓
8. Return BlogUserInfoResponse
```

## Troubleshooting Checklist

- [ ] gRPC server is running on `localhost:9090`
- [ ] Social media service can connect to gRPC server
- [ ] User ID extracted from JWT is valid UUID format
- [ ] Blog author field in MongoDB is UUID type
- [ ] UUID.toString() produces standard format: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
- [ ] gRPC request contains string UUID
- [ ] gRPC server responds with user info

## Log Levels

To see all UUID conversion details, set log level in application.properties:

```properties
# Add these to application.properties
logging.level.FCJLaurels.awsrek.service.UserGrpcClientService=DEBUG
logging.level.FCJLaurels.awsrek.service.blogging.BlogServiceImpl=DEBUG
```

Then run:
```powershell
mvn clean spring-boot:run -Dspring-boot.run.arguments="--debug"
```

## Testing UUID Validation

Your code now validates UUID format. Test with invalid UUID:

```bash
# This should log warning about invalid UUID format
# (but still send to gRPC server to let it handle)
POST http://localhost:8080/api/blog/123  # Invalid UUID in path
```

Expected log:
```
⚠️  Invalid UUID format provided: 123
```

## gRPC Service Verification

Ensure your gRPC server contract expects string UUID:

**user_info.proto:**
```protobuf
service UserInfoService {
  rpc BlogUserInfo(BlogUserInfoRequest) returns (BlogUserInfoResponse);
}

message BlogUserInfoRequest {
  string id = 1;  // ← Should be string, not UUID
}
```

## Performance Notes

- UUID to String conversion is very fast (< 1ms)
- String format is standard and no parsing needed by gRPC
- Logging may impact performance - use DEBUG level only when needed

---

**Your system correctly handles UUID → String conversion for gRPC communication.**

