# Blog Service ↔ User Service gRPC Communication

## 🎭 The Two-Way Communication

### User Service (Server Side)
The user service has implemented:
```java
public void blogUserInfo(BlogUserInfoRequest request, 
                         StreamObserver<BlogUserInfoResponse> responseObserver) {
    try {
        logger.info("gRPC: blogUserInfo called with userId={}", request.getId());
        
        // Convert String UUID to UUID object
        UUID userId = UUID.fromString(request.getId());
        
        // Query database for user info
        UserInfoDTO result = userInfoService.getUserInfoByUserId(userId);
        
        // Build response with only name and avatar
        BlogUserInfoResponse response = BlogUserInfoResponse.newBuilder()
                .setName(result.getFullName() != null ? result.getFullName() : "")
                .setAvatar(result.getAvatarUrl() != null ? result.getAvatarUrl() : "")
                .build();
        
        logger.info("gRPC: blogUserInfo completed successfully for userId={}", userId);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    } catch (UserInfoNotFoundException e) {
        // User not found
        responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
    } catch (IllegalArgumentException e) {
        // Invalid UUID format
        responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid user ID format: " + e.getMessage())
                .asRuntimeException());
    } catch (Exception e) {
        // Other errors
        responseObserver.onError(Status.INTERNAL
                .withDescription("Error retrieving blog user info: " + e.getMessage())
                .asRuntimeException());
    }
}
```

### Blog Service (Client Side)
Your blog service calls it:

```java
// In BlogServiceImpl.mapToBlogDisplay()
String authorIdString = entity.getAuthor().toString();
// "550e8400-e29b-41d4-a716-446655440000"

BlogUserInfoResponse userInfo = userGrpcClientService.getUserInfo(authorIdString);
// Internally calls: blockingStub.blogUserInfo(BlogUserInfoRequest)

String authorName = userInfo.getName();      // Gets "John Doe"
String authorAvatar = userInfo.getAvatar();  // Gets "https://..."
```

---

## 📡 Network Communication

```
Blog Service (Port 8080)              User Service (Port 9090)
       ↓                                        
       │ BlogUserInfoRequest                    
       │ { id: "550e8400-..." }                 
       └──────────────────────→                 
                                    UserInfoDTO lookup
                                    ↓
                                    User found: "John Doe"
                                    Avatar: "https://..."
       ←──────────────────────     
       │ BlogUserInfoResponse       
       │ { name: "John Doe",        
       │   avatar: "https://..." }  
       ↓
Extract data and build BlogDisplay
```

---

## 🔄 Complete Data Flow

```
1. Blog Entity from Database
   {
     id: "blog-123",
     title: "My Blog",
     author: UUID("550e8400-..."),  ← UUID object in Java
     content: "...",
     createdAt: "2025-12-01T..."
   }

2. Convert to String in Blog Service
   UUID → String
   UUID("550e8400-...") → "550e8400-e29b-41d4-a716-446655440000"

3. Send via gRPC
   BlogUserInfoRequest {
     id = "550e8400-e29b-41d4-a716-446655440000"
   }

4. User Service Receives
   - Receives UUID as String
   - Converts back: String → UUID("550e8400-...")
   - Queries: SELECT * FROM users WHERE id = ?
   - Result: UserInfo { name: "John Doe", avatar_url: "https://..." }

5. User Service Responds
   BlogUserInfoResponse {
     name = "John Doe"
     avatar = "https://s3.../avatar.jpg"
   }

6. Blog Service Receives
   - userInfo.getName()      → "John Doe"
   - userInfo.getAvatar()    → "https://s3.../avatar.jpg"

7. Build BlogDisplay DTO
   {
     title: "My Blog",
     content: "...",
     authorName: "John Doe",          ← FROM GRPC
     authorAvatar: "https://...",     ← FROM GRPC
     creationDate: "2025-12-01T..."
   }

8. Return in API Response
   When user calls: GET /api/v1/blogs/newest
   Response includes author info enriched from gRPC call
```

---

## 📊 Data Transformation Summary

| Stage | Format | Type |
|-------|--------|------|
| **Database** | UUID binary | Database column |
| **Java Object** | UUID class | `java.util.UUID` |
| **Blog Entity** | UUID field | `blog.author` |
| **gRPC Request** | String | `"550e8400-..."` |
| **Network** | Protobuf bytes | Binary serialized |
| **User Service** | String → UUID | `UUID.fromString()` |
| **DB Query** | UUID | Lookup parameter |
| **gRPC Response** | String fields | name, avatar |
| **Blog Service** | Java objects | `String` name, avatar |
| **Final DTO** | String fields | `BlogDisplay` |

---

## ⚡ Key UUID Handling Points

### Point 1: Blog Service (Client)
```java
// Blog entity has: author as UUID object
UUID authorUUID = entity.getAuthor();

// Convert to String for gRPC transmission
String authorUUIDString = authorUUID.toString();
// Result: "550e8400-e29b-41d4-a716-446655440000"

// Send in gRPC request (as String)
BlogUserInfoRequest.newBuilder()
    .setId(authorUUIDString)  // String type in proto
    .build()
```

### Point 2: User Service (Server)
```java
// Receive UUID as String
String receivedId = request.getId();
// Value: "550e8400-e29b-41d4-a716-446655440000"

// Convert back to UUID for database lookup
UUID userId = UUID.fromString(receivedId);

// Query database
UserInfoDTO result = userInfoService.getUserInfoByUserId(userId);

// Return only name and avatar
return BlogUserInfoResponse.newBuilder()
    .setName(result.getFullName())   // String
    .setAvatar(result.getAvatarUrl()) // String
    .build();
```

### Point 3: Blog Service (Receive & Use)
```java
// Receive response with String fields
BlogUserInfoResponse userInfo = blockingStub.blogUserInfo(request);

// Extract Strings
String authorName = userInfo.getName();      // "John Doe"
String authorAvatar = userInfo.getAvatar();  // "https://..."

// Use in BlogDisplay
BlogDisplay.builder()
    .authorName(authorName)    // String from gRPC response
    .authorAvatar(authorAvatar) // String from gRPC response
    .build()
```

---

## 🎯 What's Happening at Each Step

| Step | Component | Action | Input | Output |
|------|-----------|--------|-------|--------|
| 1 | BlogServiceImpl | Read blog | `blog entity` | `blog with UUID author` |
| 2 | BlogServiceImpl | Convert UUID | `UUID object` | `"550e8400-..."` string |
| 3 | UserGrpcClientService | Create request | `UUID string` | `BlogUserInfoRequest` |
| 4 | gRPC Channel | Serialize | `BlogUserInfoRequest` | `Protobuf bytes` |
| 5 | Network | Transmit | `Protobuf bytes` | `Protobuf bytes` (received) |
| 6 | UserService | Deserialize | `Protobuf bytes` | `BlogUserInfoRequest` |
| 7 | UserService | Convert UUID | `"550e8400-..." string` | `UUID object` |
| 8 | UserService | Query DB | `UUID` | `UserInfo DTO` |
| 9 | UserService | Map response | `UserInfo DTO` | `BlogUserInfoResponse` |
| 10 | UserService | Serialize | `BlogUserInfoResponse` | `Protobuf bytes` |
| 11 | Network | Transmit | `Protobuf bytes` | `Protobuf bytes` (received) |
| 12 | UserGrpcClientService | Deserialize | `Protobuf bytes` | `BlogUserInfoResponse` |
| 13 | BlogServiceImpl | Extract data | `BlogUserInfoResponse` | `name, avatar strings` |
| 14 | BlogServiceImpl | Build DTO | `name, avatar` | `BlogDisplay DTO` |

---

## ✅ Verification Checklist

- [x] Blog Service converts UUID to String
- [x] gRPC request contains UUID as String
- [x] Network transmits via Protobuf
- [x] User Service receives String UUID
- [x] User Service converts String back to UUID
- [x] User Service queries database
- [x] User Service returns only name and avatar
- [x] Blog Service receives response
- [x] Blog Service extracts name and avatar
- [x] Blog Service builds enriched BlogDisplay
- [x] API returns blog with author information

---

## 🚀 Result

When you call:
```
GET /api/v1/blogs/newest?page=0&size=10
```

You get:
```json
{
  "content": [
    {
      "title": "My Blog Post",
      "content": "...",
      "authorName": "John Doe",           ← From gRPC blogUserInfo()
      "authorAvatar": "https://...",      ← From gRPC blogUserInfo()
      "imageURL": "...",
      "creationDate": "2025-12-01T..."
    }
  ]
}
```

The `authorName` and `authorAvatar` come from the `blogUserInfo()` RPC call to the user service.

---

**Status**: ✅ Complete and Working  
**Last Updated**: 2025-12-01

