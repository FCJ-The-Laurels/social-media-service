# Quick Reference: blogUserInfo() gRPC Call

## 🎯 One-Line Summary
Your blog service calls `UserInfoService.blogUserInfo()` RPC method to fetch author name and avatar using UUID string.

---

## 📍 Where It Happens

**File**: `BlogServiceImpl.java`  
**Method**: `mapToBlogDisplay(blog entity)`  
**Line**: ~420

---

## 🔄 The Call

```java
// 1. Input: blog entity with author UUID (Java object)
UUID authorId = entity.getAuthor();  // e.g., UUID object

// 2. Convert to String
String authorIdString = authorId.toString();  
// Result: "550e8400-e29b-41d4-a716-446655440000"

// 3. Make gRPC call (blocking/sync)
BlogUserInfoResponse userInfo = userGrpcClientService.getUserInfo(authorIdString);
//                              ↓
//                    Calls: blogUserInfo() in user service

// 4. Extract fields
String name = userInfo.getName();      // "John Doe"
String avatar = userInfo.getAvatar();  // "https://s3.../avatar.jpg"
```

---

## 📨 Proto Messages

**Request**:
```protobuf
message BlogUserInfoRequest {
  string id = 1;  // UUID as string
}
```

**Response**:
```protobuf
message BlogUserInfoResponse {
  string name = 1;      // Author's name
  string avatar = 2;    // Author's avatar URL
}
```

---

## ⚙️ Configuration

```properties
grpc.client.user-service.address=localhost:9090
grpc.client.user-service.timeout=5
```

---

## ✅ What This Achieves

When you fetch blogs, each blog now includes:
- ✅ Author name (from gRPC call)
- ✅ Author avatar URL (from gRPC call)
- ✅ Blog title, content, creation date
- ✅ Blog image URL

---

## 🧪 Example Response

```json
GET /api/v1/blogs/newest?page=0&size=10

{
  "content": [
    {
      "title": "Learning Spring Boot",
      "content": "In this tutorial...",
      "authorName": "Alice Johnson",      // ← FROM GRPC
      "authorAvatar": "https://...",      // ← FROM GRPC
      "imageURL": "https://...",
      "creationDate": "2025-12-01T10:30:00"
    }
  ]
}
```

---

## 🚨 Error Handling

| Error | Result |
|-------|--------|
| User not found | `authorName = "Unknown User"` |
| Server down | `authorName = "Unknown User"` |
| Timeout | `authorName = "Unknown User"` |
| Network error | `authorName = "Unknown User"` |

**All errors are logged** for debugging.

---

## 📞 Testing

1. **Start user service** on port 9090
2. **Create a blog** with a valid user ID
3. **Fetch blogs** via API
4. **Check response** for authorName and authorAvatar

---

## 🔗 Related Files

- `UserGrpcClientService.java` - gRPC client
- `BlogServiceImpl.java` - Service using gRPC
- `user_info.proto` - Proto contract
- `GRPC_BLOG_USER_INFO_FLOW.md` - Detailed flow

---

**Status**: ✅ Ready to Use

