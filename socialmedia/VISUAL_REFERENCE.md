# API Visual Reference & Data Flows

## 🗂️ Complete API Structure

### All Available Endpoints

```
BASE URL: http://localhost:8080

📝 BLOGS
  ├─ POST   /blogs/create                    Create blog (needs X-User-Id)
  ├─ GET    /blogs                           Get all blogs
  ├─ GET    /blogs/{id}/search-by-id         Get blog by ID
  ├─ GET    /blogs/{id}/display              Get blog with author info
  ├─ GET    /blogs/author/{author}           Get blogs by author
  ├─ GET    /blogs/search-by-title?title=    Search by title
  ├─ PUT    /blogs/{id}/update-blog          Update blog
  ├─ DELETE /blogs/{id}                      Delete blog
  ├─ GET    /blogs/paginated?page=&size=     Get paginated (offset)
  ├─ GET    /blogs/feed?cursor=&size=        Get with cursor pagination
  ├─ GET    /blogs/newest/cursor?cursor=&    Get newest (cursor)
  └─ GET    /blogs/newest/paginated?page=    Get newest (offset)

💬 COMMENTS
  ├─ POST   /comments/create                 Create comment
  ├─ GET    /comments                        Get all comments
  ├─ GET    /comments/{id}                   Get comment by ID
  ├─ GET    /comments/blog/{blogId}          Get comments on blog
  ├─ GET    /comments/user/{userId}          Get comments by user
  ├─ GET    /comments/blog/{blogId}/count    Count comments on blog
  ├─ PUT    /comments/{id}                   Update comment
  ├─ DELETE /comments/{id}                   Delete comment
  └─ DELETE /comments/blog/{blogId}          Delete all on blog

👍 LIKES
  ├─ POST   /likes/create                    Create like
  ├─ GET    /likes                           Get all likes
  ├─ GET    /likes/{id}                      Get like by ID
  ├─ GET    /likes/blog/{blogId}             Get likes on blog
  ├─ GET    /likes/user/{userId}             Get likes by user
  ├─ GET    /likes/check?userId=&blogId=    Check if liked
  ├─ GET    /likes/user/{userId}/blog/{bid}  Get specific like
  ├─ POST   /likes/toggle?userId=&blogId=   Toggle like
  ├─ GET    /likes/blog/{blogId}/count       Count likes on blog
  ├─ GET    /likes/user/{userId}/count       Count user's likes
  ├─ DELETE /likes/{id}                      Delete like
  └─ DELETE /likes/user/{userId}/blog/{bid}  Delete specific like

🖼️ IMAGES
  ├─ POST   /images/upload                   Upload to S3
  ├─ POST   /images/create                   Create image record
  ├─ GET    /images                          Get all images
  ├─ GET    /images/{id}                     Get image by ID
  ├─ GET    /images/name/{name}              Get by name
  ├─ GET    /images/type/{type}              Get by type
  ├─ GET    /images/search?keyword=          Search images
  ├─ PUT    /images/{id}                     Update image
  ├─ DELETE /images/{id}                     Delete image
  └─ DELETE /images/type/{type}              Delete by type
```

---

## 📊 Data Flow Diagrams

### Flow 1: Create Blog Post

```
┌─────────────┐
│   Frontend  │
└──────┬──────┘
       │ 1. User fills form
       │    (title, content, image)
       ▼
┌──────────────────┐
│  Upload Image    │
│ POST /images/     │◄─── If user selects image
│ (multipart)      │
└────────┬─────────┘
         │ 2. Returns ImageDTO
         │    with S3 URL
         ▼
┌──────────────────┐
│  Create Blog     │
│ POST /blogs/     │
│ create           │
│ + X-User-Id      │◄─── Include user ID
└────────┬─────────┘
         │ 3. Returns BlogDTO
         │    with generated ID
         ▼
┌──────────────────┐
│  Display Success │
│  Redirect to     │
│  blog detail     │
└──────────────────┘
```

---

### Flow 2: View Blog Feed (Infinite Scroll)

```
┌─────────────┐
│   Frontend  │
└──────┬──────┘
       │ 1. Page loads
       ▼
┌──────────────────────────┐
│ GET /blogs/newest/cursor │
│ (no cursor, size=10)     │
└────────┬─────────────────┘
         │ 2. Returns:
         │    CursorPageDTO{
         │      data: [BlogDisplay[]],
         │      nextCursor: "xyz"
         │    }
         ▼
┌──────────────────┐
│  Display Blogs   │
│  (10 items)      │
└────────┬─────────┘
         │ 3. User scrolls
         │    to bottom
         ▼
┌──────────────────────────────┐
│ GET /blogs/newest/cursor     │
│ (cursor="xyz", size=10)      │
└────────┬─────────────────────┘
         │ 4. Returns more blogs
         │    + new nextCursor
         ▼
┌──────────────────┐
│  Append Blogs    │
│  (20 items now)  │
└────────┬─────────┘
         │ 5. User scrolls again
         │    repeat...
         ▼
┌──────────────────┐
│ Continue until   │
│ nextCursor=null  │
│ (no more blogs)  │
└──────────────────┘
```

---

### Flow 3: Like & Comment on Blog

```
User Interaction                    API Calls
─────────────────                   ─────────

User likes blog        ─────────>    POST /likes/toggle
                                     ?userId={id}
                                     &blogId={id}
                                     │
                                     ├─ If not liked before
                                     │  └─ Returns 200 + LikeDTO
                                     │
                                     └─ If already liked
                                        └─ Returns 204
                       <─────────
Display updated state
(liked/unliked)

User types comment     ─────────>    POST /comments/create
                                     {
                                       blogId,
                                       userId,
                                       content
                                     }
                       <─────────
Returns CommentDTO


User submits           ─────────>    GET /comments/blog/{blogId}
                                     (refresh comments list)
                       <─────────
Display all comments
```

---

### Flow 4: Complete Blog Interaction

```
┌──────────────────────────────────────────────────────┐
│         USER VIEWING BLOG FEED                        │
└──────────────┬───────────────────────────────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
    ▼                     ▼
GET /blogs/           GET /likes/blog/{id}/
newest/cursor          count (for each blog)
    │                     │
    └──────────┬──────────┘
               │
               ▼
        Display blogs with
        like counts
               │
    ┌──────────┴──────────────────────────┐
    │                                      │
    ▼                                      ▼
User clicks                         User clicks
Like button                         Comments
    │                                      │
    ▼                                      ▼
POST /likes/                        GET /comments/
toggle                              blog/{blogId}
    │                                      │
    ▼                                      ▼
Update like count                   Display comments
& button state                      list
    │                                      │
    └──────────────────────┬───────────────┘
                           │
                    User interacts
                    with comments
                           │
    ┌──────────────────────┴────────────────────┐
    │                                           │
    ▼                                           ▼
POST /comments/create              DELETE /comments/{id}
    │                                           │
    ▼                                           ▼
Refresh comments list            Remove comment
```

---

## 🔄 Response Status Code Flows

### Create/Update Operations
```
Client Request
    │
    ▼
Validation
    │
    ├─ Failed? ─> 400 Bad Request
    │
    ▼ Passed
Database Operation
    │
    ├─ Resource exists (for update)? ─> 404 Not Found
    │
    ├─ Success? ─> 201 Created (POST)
    │            └─> 200 OK (PUT)
    │
    └─ Database error? ─> 500 Internal Server Error

Return Response with DTO
```

---

### Read Operations
```
Client Request
    │
    ├─ Missing auth header? ─> 401 Unauthorized
    │
    ▼
Find Resource
    │
    ├─ Not found? ─> 404 Not Found
    │
    ▼ Found
Process Data
    │
    ├─ Error? ─> 500 Internal Server Error
    │
    ▼ Success
Return 200 OK with data
```

---

### Delete Operations
```
Client Request
    │
    ▼
Find Resource
    │
    ├─ Not found? ─> 404 Not Found
    │
    ▼ Found
Delete Operation
    │
    ├─ Error? ─> 500 Internal Server Error
    │
    ▼ Success
Return 204 No Content
(no response body)
```

---

## 🎯 DTO Transformation Flows

### Blog DTO Transformations

```
Database Entity (Blog)
└─ Contains: id, title, content, author(UUID), creationDate, imageUrl

     │
     ├─────────────────┬──────────────────┐
     │                 │                  │
     ▼                 ▼                  ▼
  BlogDTO      BlogDisplay         BlogPageResponse
  
  (Raw data)   (Display format)    (Paginated data)
               + Author Info
               (fetched via gRPC)
```

### Usage:
```
BlogDTO ──────────────> Used in single blog endpoints
BlogDisplay ──────────> Used in feeds and paginated responses
BlogPageResponse ─────> Wrapper for paginated results
BlogCreationDTO ──────> Request body for creating blogs
BlogEditDTO ──────────> Request body for updating blogs
CursorPageDTO<T> ─────> Generic wrapper for cursor pagination
```

---

## 📤 Request/Response Examples

### Example 1: Create Blog

**Request:**
```
POST /blogs/create
X-User-Id: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "title": "Learning MongoDB",
  "content": "MongoDB is a NoSQL database...",
  "imageUrl": "https://s3.amazonaws.com/images/mongodb.jpg"
}
```

**Response (201):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "Learning MongoDB",
  "content": "MongoDB is a NoSQL database...",
  "author": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T10:30:00",
  "imageUrl": "https://s3.amazonaws.com/images/mongodb.jpg"
}
```

---

### Example 2: Get Blog Feed

**Request:**
```
GET /blogs/newest/cursor?size=10
```

**Response (200):**
```json
{
  "data": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://s3.amazonaws.com/avatars/john.jpg",
      "title": "Learning MongoDB",
      "imageURL": "https://s3.amazonaws.com/images/mongodb.jpg",
      "content": "MongoDB is a NoSQL database...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "nextCursor": "eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE0VTA5OjMwOjAwIn0"
}
```

---

### Example 3: Toggle Like

**Request:**
```
POST /likes/toggle?userId=550e8400-e29b-41d4-a716-446655440000&blogId=507f1f77bcf86cd799439011
```

**Response 1 (First Like - 200):**
```json
{
  "id": "507f1f77bcf86cd799439070",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:45:00"
}
```

**Response 2 (Unlike - 204):**
```
(No response body)
```

---

### Example 4: Upload Image

**Request:**
```
POST /images/upload
Content-Type: multipart/form-data

file: [binary image data]
```

**Response (201):**
```json
{
  "id": "507f1f77bcf86cd799439100",
  "name": "image_1234567890.jpg",
  "url": "https://s3-bucket.amazonaws.com/images/image_1234567890.jpg",
  "type": "jpg",
  "creationDate": "2025-01-15T12:00:00"
}
```

---

## 🔐 Security & Headers

### Required Headers

```
All Requests (optional for most endpoints):
┌────────────────────────────────────────────┐
│ Content-Type: application/json             │
└────────────────────────────────────────────┘

Blog Creation (REQUIRED):
┌────────────────────────────────────────────┐
│ X-User-Id: {uuid}                          │
│ (User ID from API Gateway)                 │
└────────────────────────────────────────────┘

Image Upload (multipart):
┌────────────────────────────────────────────┐
│ Content-Type: multipart/form-data          │
└────────────────────────────────────────────┘
```

---

## ⚡ Performance Tips Cheat Sheet

### ✅ DO:
```
✓ Use cursor pagination for feeds
✓ Cache images on frontend
✓ Debounce search queries
✓ Batch load-related data
✓ Lazy load components
✓ Compress images before upload (< 10MB)
✓ Use virtual scrolling for large lists
```

### ❌ DON'T:
```
✗ Don't load all blogs at once
✗ Don't call API for every keystroke (search)
✗ Don't upload images without optimization
✗ Don't make unnecessary API calls
✗ Don't render large lists without pagination
```

---

## 🚨 Error Handling Quick Guide

### 400 Bad Request
```
Cause: Invalid input
Action: Check request body, validate fields
Example: Title exceeds 200 characters
```

### 401 Unauthorized
```
Cause: Missing X-User-Id header
Action: Add X-User-Id header to POST /blogs/create
Example: POST /blogs/create without X-User-Id
```

### 404 Not Found
```
Cause: Resource doesn't exist
Action: Verify ID, check if resource was deleted
Example: GET /blogs/invalid-id
```

### 500 Internal Server Error
```
Cause: Server-side issue
Action: Retry, check server logs
Example: Database connection error
```

---

## 🔗 Integration Checklist

### Before Deployment:

- [ ] All endpoints tested with sample data
- [ ] Error handling implemented
- [ ] Loading states added
- [ ] Images optimized
- [ ] Pagination working
- [ ] Authentication headers included
- [ ] CORS issues resolved
- [ ] Caching strategy implemented
- [ ] Performance tested
- [ ] Mobile responsive

---

## 📚 Quick Reference Table

| Operation | Endpoint | Method | Auth | Response |
|-----------|----------|--------|------|----------|
| Create Blog | /blogs/create | POST | ✓ | BlogDTO (201) |
| Get Blog | /blogs/{id}/search-by-id | GET | ✗ | BlogDTO (200) |
| Update Blog | /blogs/{id}/update-blog | PUT | ✗ | BlogDTO (200) |
| Delete Blog | /blogs/{id} | DELETE | ✗ | 204 |
| Get Feed | /blogs/newest/cursor | GET | ✗ | CursorPageDTO (200) |
| Create Comment | /comments/create | POST | ✗ | CommentDTO (201) |
| Get Comments | /comments/blog/{id} | GET | ✗ | CommentDTO[] (200) |
| Delete Comment | /comments/{id} | DELETE | ✗ | 204 |
| Like Blog | /likes/toggle | POST | ✗ | LikeDTO/204 (200/204) |
| Count Likes | /likes/blog/{id}/count | GET | ✗ | long (200) |
| Upload Image | /images/upload | POST | ✗ | ImageDTO (201) |
| Search Images | /images/search | GET | ✗ | ImageDTO[] (200) |

---

## 🎯 Implementation Order Recommendation

### Phase 1: Core Setup (Day 1)
1. Set up HTTP client
2. Create service layer
3. Implement blog listing

### Phase 2: User Features (Day 2)
1. Blog creation
2. Blog editing/deletion
3. Image upload

### Phase 3: Social Features (Day 3)
1. Comments
2. Likes
3. Like/comment counts

### Phase 4: Optimization (Day 4+)
1. Infinite scroll
2. Caching
3. Performance tuning

---

## 📖 Document Quick Links

| Document | Use For |
|----------|---------|
| DOCUMENTATION_INDEX.md | **START HERE** |
| API_AND_DTO_DOCUMENTATION.md | Complete reference |
| QUICK_REFERENCE.md | Quick lookups |
| FRONTEND_INTEGRATION_GUIDE.md | Code examples |
| (This file) | Visual reference |

---

**Version:** 1.0.0 | **Updated:** December 2, 2025 | **Status:** Ready ✅

