# UUID to String Conversion - Visual Diagram

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SOCIAL MEDIA SERVICE                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────┐                                                   │
│  │  BlogController      │                                                   │
│  │  (REST Endpoint)     │                                                   │
│  └──────────┬───────────┘                                                   │
│             │ GET /api/blog/newest                                          │
│             ↓                                                                │
│  ┌──────────────────────┐                                                   │
│  │  BlogServiceImpl      │                                                   │
│  │  getNewestBlogs()    │ ← Calls mapToBlogDisplay()                        │
│  └──────────┬───────────┘                                                   │
│             │                                                                │
│             ↓                                                                │
│  ┌──────────────────────────────────────────┐                               │
│  │  mapToBlogDisplay(blog entity)           │                               │
│  │                                          │                               │
│  │  blog.author: UUID  ─────┐               │                               │
│  │                           ↓               │                               │
│  │  UUID → String            │               │                               │
│  │  .toString()              │               │                               │
│  │                           ↓               │                               │
│  │  "550e8400-e29b-..."      │               │                               │
│  │                           │               │                               │
│  └─────────────────────┬─────────────────────┘                               │
│                        │                                                     │
│                        ↓                                                     │
│  ┌──────────────────────────────────────────────────────┐                   │
│  │  UserGrpcClientService                               │                   │
│  │  getUserInfo(String userId)                          │                   │
│  │                                                      │                   │
│  │  Input: "550e8400-e29b-41d4-a716-446655440000"      │                   │
│  │                                                      │                   │
│  │  ✓ Validate UUID format                             │                   │
│  │  ✓ Create BlogUserInfoRequest                       │                   │
│  │  ✓ Send via gRPC to server                          │                   │
│  │                                                      │                   │
│  └────────────────────┬─────────────────────────────────┘                   │
│                       │                                                      │
└───────────────────────┼──────────────────────────────────────────────────────┘
                        │
                        │ ◄────────────── gRPC Call ─────────────►
                        │
        ┌───────────────┴──────────────┐
        │                              │
        ↓                              ↓
      Network                      gRPC Protocol
    (TCP Port 9090)              (String UUID)
        │                              │
        ↓                              ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                         USER INFO SERVICE (gRPC Server)                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────┐                               │
│  │  UserInfoServiceImpl                      │                               │
│  │  blogUserInfo(request, responseObserver) │                               │
│  │                                          │                               │
│  │  Input: "550e8400-e29b-..."             │                               │
│  │                                          │                               │
│  │  ┌────────────────────────────────────┐  │                               │
│  │  │ String → UUID Conversion          │  │                               │
│  │  │ UUID.fromString(userId)           │  │                               │
│  │  └────────┬─────────────────────────┬┘  │                               │
│  │           │                         │   │                               │
│  │           ↓                         ↓   │                               │
│  │      UUID Object                 Query  │                               │
│  │                                Database │                               │
│  │  ┌──────────────────────────────────────┐ │                               │
│  │  │ User Repository                      │ │                               │
│  │  │ .findById(userUuid)                  │ │                               │
│  │  └──────────┬───────────────────────────┘ │                               │
│  │             │                            │                               │
│  │             ↓                            │                               │
│  │  ┌──────────────────────────┐            │                               │
│  │  │ User Entity from DB      │            │                               │
│  │  │ name: "John Doe"         │            │                               │
│  │  │ avatar: "https://..."    │            │                               │
│  │  └──────────┬───────────────┘            │                               │
│  │             │                            │                               │
│  │             ↓                            │                               │
│  │  ┌──────────────────────────┐            │                               │
│  │  │ Build Response            │            │                               │
│  │  │ BlogUserInfoResponse      │            │                               │
│  │  └──────────┬───────────────┘            │                               │
│  │             │                            │                               │
│  └─────────────┼────────────────────────────┘                               │
│                │                                                             │
└────────────────┼─────────────────────────────────────────────────────────────┘
                 │
                 │ ◄──── Return Response ──────
                 │
        ┌────────┴────────┐
        │                 │
        ↓                 ↓
    gRPC Response    Name + Avatar
    (String fields)  
        │                 │
        ↓                 ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SOCIAL MEDIA SERVICE (continued)                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────┐                               │
│  │  UserGrpcClientService (receives)        │                               │
│  │  return response;                        │                               │
│  └──────────┬───────────────────────────────┘                               │
│             │                                                                │
│             ↓                                                                │
│  ┌──────────────────────────────────────────┐                               │
│  │  BlogServiceImpl.mapToBlogDisplay()       │                               │
│  │  (continues)                             │                               │
│  │                                          │                               │
│  │  authorName = userInfo.getName()         │                               │
│  │  authorAvatar = userInfo.getAvatar()     │                               │
│  │                                          │                               │
│  │  return BlogDisplay.builder()            │                               │
│  │    .authorName(authorName)               │                               │
│  │    .authorAvatar(authorAvatar)           │                               │
│  │    .title(title)                         │                               │
│  │    ...build();                           │                               │
│  └──────────┬───────────────────────────────┘                               │
│             │                                                                │
│             ↓                                                                │
│  ┌──────────────────────────────────────────┐                               │
│  │  BlogController                          │                               │
│  │  Returns JSON Response                   │                               │
│  └──────────┬───────────────────────────────┘                               │
│             │                                                                │
│             ↓                                                                │
│  ┌──────────────────────────────────────────┐                               │
│  │  Client (Frontend)                       │                               │
│  │  {                                       │                               │
│  │    "authorName": "John Doe",             │                               │
│  │    "authorAvatar": "https://...",        │                               │
│  │    "title": "Blog Title",                │                               │
│  │    "content": "...",                     │                               │
│  │    "creationDate": "..."                 │                               │
│  │  }                                       │                               │
│  └──────────────────────────────────────────┘                               │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Type Conversion Chain

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                        UUID CONVERSION CHAIN                                 │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  STEP 1: MongoDB Storage                                                     │
│  ────────────────────────                                                     │
│  db.blog.findOne()                                                           │
│  {                                                                            │
│    "author": UUID("550e8400-e29b-41d4-a716-446655440000")    ← BSON UUID    │
│  }                                                                            │
│                                                                               │
│                                     ↓ (Retrieve from DB)                    │
│                                                                               │
│  STEP 2: Java Object (MongoDB Driver)                                        │
│  ─────────────────────────────────────                                       │
│  blog entity = blogRepository.findById(id);                                  │
│  UUID authorUuid = entity.getAuthor();                                       │
│  authorUuid = 550e8400-e29b-41d4-a716-446655440000           ← UUID Object  │
│                                                                               │
│                                     ↓ (toString() conversion)                │
│                                                                               │
│  STEP 3: String for gRPC                                                     │
│  ─────────────────────────                                                   │
│  String authorIdString = entity.getAuthor().toString();                      │
│  authorIdString = "550e8400-e29b-41d4-a716-446655440000"    ← String        │
│                                                                               │
│                                     ↓ (gRPC transmission)                   │
│                                                                               │
│  STEP 4: gRPC Proto Message                                                  │
│  ───────────────────────────                                                 │
│  message BlogUserInfoRequest {                                               │
│    string id = 1;                                                            │
│    // id = "550e8400-e29b-41d4-a716-446655440000"            ← Proto String  │
│  }                                                                            │
│                                                                               │
│                                     ↓ (Network transmission)                │
│                                                                               │
│  STEP 5: gRPC Server Receives                                                │
│  ──────────────────────────────                                              │
│  String userId = request.getId();                                            │
│  userId = "550e8400-e29b-41d4-a716-446655440000"            ← String        │
│                                                                               │
│                                     ↓ (Server converts back to UUID)        │
│                                                                               │
│  STEP 6: Server UUID Object                                                  │
│  ──────────────────────────                                                  │
│  UUID userUuid = UUID.fromString(userId);                                    │
│  userUuid = 550e8400-e29b-41d4-a716-446655440000            ← UUID Object   │
│                                                                               │
│                                     ↓ (Query database)                      │
│                                                                               │
│  STEP 7: Database Query                                                      │
│  ──────────────────────                                                      │
│  User user = userRepository.findById(userUuid);              ← Queries by UUID│
│                                                                               │
│                                     ↓ (Found user)                          │
│                                                                               │
│  STEP 8: Response                                                            │
│  ─────────────                                                               │
│  return BlogUserInfoResponse.newBuilder()                                    │
│    .setName(user.getName())        // "John Doe"                             │
│    .setAvatar(user.getAvatar())    // "https://..."                          │
│    .build();                                                                 │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Data Type Evolution

```
Representation          Type              Storage         Format
─────────────────────   ──────────────     ──────────      ──────────────────────
MongoDB                 UUID (BSON)        Binary          UUID("550e8400-...")
   ↓
Java Object             UUID (class)       Memory          550e8400-e29b-...
   ↓
gRPC Protocol           String             Text/Binary     "550e8400-e29b-..."
   ↓
Server Java Object      UUID (class)       Memory          550e8400-e29b-...
   ↓
Database Query          UUID               Index/Search    550e8400-e29b-...
   ↓
Result                  User Entity        Memory          {name, avatar, ...}
   ↓
Response                String             Text/JSON       "John Doe", "https://..."
```

---

## Key Points

✓ **Input**: UUID stored in MongoDB  
✓ **Conversion**: `.toString()` method  
✓ **Transport**: String via gRPC  
✓ **Server**: Receives string, converts back to UUID  
✓ **Query**: Uses UUID to find user  
✓ **Output**: Author info in response  

**Result**: Blog with complete author information displayed to client

