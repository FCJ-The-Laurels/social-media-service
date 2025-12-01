# API Quick Reference Guide

## Table of Contents
1. [Quick Start](#quick-start)
2. [API Endpoints Summary](#api-endpoints-summary)
3. [DTO Cheat Sheet](#dto-cheat-sheet)
4. [Common Tasks](#common-tasks)
5. [Code Examples](#code-examples)

---

## Quick Start

### Base URL
```
http://localhost:8080
```

### Swagger/OpenAPI UI
```
http://localhost:8080/swagger-ui.html
```

### Authentication
```
Header: X-User-Id: {uuid}
```
Required only for: `POST /blogs/create`

---

## API Endpoints Summary

### 📝 Blog Management (`/blogs`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| **POST** | `/create` | Create new blog |
| **GET** | `/{id}/search-by-id` | Get blog by ID |
| **GET** | (root) | Get all blogs |
| **GET** | `/author/{author}` | Get blogs by author |
| **GET** | `/search-by-title?title=` | Search blogs by title |
| **PUT** | `/{id}/update-blog` | Update blog |
| **DELETE** | `/{id}` | Delete blog |
| **GET** | `/paginated?page=0&size=10` | Get paginated blogs |
| **GET** | `/feed?cursor=&size=10` | Get blogs with cursor pagination |
| **GET** | `/newest/cursor?cursor=&size=10` | Get newest blogs (cursor) |
| **GET** | `/newest/paginated?page=0&size=10` | Get newest blogs (paginated) |
| **GET** | `/{id}/display` | Get blog display info |

---

### 💬 Comments (`/comments`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| **POST** | `/create` | Create comment |
| **GET** | `/{id}` | Get comment by ID |
| **GET** | (root) | Get all comments |
| **GET** | `/blog/{blogId}` | Get comments by blog |
| **GET** | `/user/{userId}` | Get comments by user |
| **PUT** | `/{id}` | Update comment |
| **DELETE** | `/{id}` | Delete comment |
| **DELETE** | `/blog/{blogId}` | Delete comments by blog |
| **GET** | `/blog/{blogId}/count` | Count comments on blog |

---

### 👍 Likes (`/likes`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| **POST** | `/create` | Create like |
| **GET** | `/{id}` | Get like by ID |
| **GET** | (root) | Get all likes |
| **GET** | `/blog/{blogId}` | Get likes on blog |
| **GET** | `/user/{userId}` | Get user's likes |
| **GET** | `/check?userId=&blogId=` | Check if user liked blog |
| **GET** | `/user/{userId}/blog/{blogId}` | Get specific like |
| **POST** | `/toggle?userId=&blogId=` | Toggle like (like/unlike) |
| **DELETE** | `/{id}` | Delete like |
| **DELETE** | `/user/{userId}/blog/{blogId}` | Delete specific like |
| **GET** | `/blog/{blogId}/count` | Count likes on blog |
| **GET** | `/user/{userId}/count` | Count user's likes |

---

### 🖼️ Images (`/images`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| **POST** | `/upload` | Upload image to S3 |
| **POST** | `/create` | Create image record |
| **GET** | `/{id}` | Get image by ID |
| **GET** | (root) | Get all images |
| **GET** | `/name/{name}` | Get images by name |
| **GET** | `/type/{type}` | Get images by type |
| **GET** | `/search?keyword=` | Search images by name |
| **PUT** | `/{id}` | Update image |
| **DELETE** | `/{id}` | Delete image |
| **DELETE** | `/type/{type}` | Delete images by type |

---

## DTO Cheat Sheet

### BlogDTO ✍️
```json
{
  "id": "ObjectId",
  "title": "string",
  "content": "string",
  "author": "UUID",
  "creationDate": "LocalDateTime",
  "imageUrl": "string"
}
```
**Use for:** Blog responses

---

### BlogCreationDTO ✍️
```json
{
  "title": "string (required, max 200 chars)",
  "content": "string (required)",
  "imageUrl": "string (optional)"
}
```
**Use for:** Creating blogs

---

### BlogEditDTO ✏️
```json
{
  "title": "string (optional)",
  "content": "string (optional)",
  "imageUrl": "string (optional)"
}
```
**Use for:** Updating blogs

---

### BlogDisplay 📱
```json
{
  "authorName": "string",
  "authorAvatar": "string",
  "title": "string",
  "imageURL": "string",
  "content": "string",
  "creationDate": "LocalDateTime"
}
```
**Use for:** Feed displays, pagination responses

---

### BlogPageResponse 📄
```json
{
  "content": [BlogDisplay[]],
  "page": "int",
  "size": "int",
  "totalElements": "long",
  "totalPages": "int",
  "hasNext": "boolean",
  "hasPrevious": "boolean",
  "nextCursor": "string",
  "previousCursor": "string"
}
```
**Use for:** Offset-based pagination

---

### CursorPageDTO<T> ♾️
```json
{
  "data": [T[]],
  "nextCursor": "string"
}
```
**Use for:** Cursor-based pagination (infinite scroll)

---

### CommentDTO 💬
```json
{
  "id": "ObjectId",
  "blogId": "string",
  "userId": "string",
  "creationDate": "LocalDateTime",
  "content": "string"
}
```
**Use for:** Comment responses

---

### CommentCreationDTO 💬
```json
{
  "blogId": "string (required)",
  "userId": "string (required)",
  "content": "string (required)"
}
```
**Use for:** Creating comments

---

### CommentEditDTO ✏️
```json
{
  "content": "string (required)"
}
```
**Use for:** Updating comments

---

### LikeDTO 👍
```json
{
  "id": "ObjectId",
  "blogId": "string",
  "userId": "string",
  "creationDate": "LocalDateTime"
}
```
**Use for:** Like responses

---

### LikeCreationDTO 👍
```json
{
  "blogId": "string (required)",
  "userId": "string (required)"
}
```
**Use for:** Creating likes

---

### ImageDTO 🖼️
```json
{
  "id": "ObjectId",
  "name": "string",
  "url": "string",
  "type": "string",
  "creationDate": "LocalDateTime"
}
```
**Use for:** Image responses

---

### ImageCreationDTO 🖼️
```json
{
  "name": "string (required)",
  "url": "string (required)",
  "type": "string (required)"
}
```
**Use for:** Creating image records

---

### ImageEditDTO ✏️
```json
{
  "name": "string (optional)",
  "url": "string (optional)",
  "type": "string (optional)"
}
```
**Use for:** Updating images

---

## Common Tasks

### 1️⃣ Create a Blog Post

```bash
POST /blogs/create
Header: X-User-Id: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "title": "My Blog Title",
  "content": "Blog content here...",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Response:** BlogDTO with id

---

### 2️⃣ Get Blog Feed (Infinite Scroll)

**First Load:**
```bash
GET /blogs/newest/cursor?size=10
```

**Next Load:**
```bash
GET /blogs/newest/cursor?cursor={nextCursor}&size=10
```

**Response:** CursorPageDTO<BlogDisplay>

---

### 3️⃣ Comment on Blog

```bash
POST /comments/create
Content-Type: application/json

{
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "content": "Great post!"
}
```

**Response:** CommentDTO

---

### 4️⃣ Like/Unlike Blog

```bash
POST /likes/toggle?userId={userId}&blogId={blogId}
```

**Responses:**
- 200 OK + LikeDTO = Liked
- 204 No Content = Unliked

---

### 5️⃣ Upload Image to S3

```bash
POST /images/upload
Content-Type: multipart/form-data

file: <binary image data>
```

**Response:** ImageDTO with S3 URL

---

### 6️⃣ Update Blog Post

```bash
PUT /blogs/{blogId}/update-blog
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content..."
}
```

**Response:** Updated BlogDTO

---

### 7️⃣ Delete Blog Post

```bash
DELETE /blogs/{blogId}
```

**Response:** 204 No Content

---

### 8️⃣ Get Comments on Blog

```bash
GET /comments/blog/{blogId}
```

**Response:** Array of CommentDTO

---

### 9️⃣ Count Likes on Blog

```bash
GET /likes/blog/{blogId}/count
```

**Response:** Long (number)

---

### 🔟 Search Blogs

```bash
GET /blogs/search-by-title?title=python
```

**Response:** Array of BlogDTO

---

## Code Examples

### JavaScript/TypeScript

```javascript
// Create blog
async function createBlog(title, content, imageUrl, userId) {
  const response = await fetch('/blogs/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': userId
    },
    body: JSON.stringify({
      title,
      content,
      imageUrl
    })
  });
  return response.json();
}

// Get blog feed
async function getBlogFeed(cursor = null, size = 10) {
  const url = cursor 
    ? `/blogs/newest/cursor?cursor=${cursor}&size=${size}`
    : `/blogs/newest/cursor?size=${size}`;
  
  const response = await fetch(url);
  return response.json();
}

// Like/Unlike blog
async function toggleLike(userId, blogId) {
  const response = await fetch(
    `/likes/toggle?userId=${userId}&blogId=${blogId}`,
    { method: 'POST' }
  );
  
  if (response.status === 200) {
    return { liked: true, data: await response.json() };
  } else if (response.status === 204) {
    return { liked: false };
  }
}

// Comment on blog
async function addComment(blogId, userId, content) {
  const response = await fetch('/comments/create', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      blogId,
      userId,
      content
    })
  });
  return response.json();
}

// Upload image
async function uploadImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('/images/upload', {
    method: 'POST',
    body: formData
  });
  return response.json();
}
```

---

### React Hooks Example

```javascript
import { useState, useEffect } from 'react';

function BlogFeed() {
  const [blogs, setBlogs] = useState([]);
  const [cursor, setCursor] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadMoreBlogs = async () => {
    setLoading(true);
    const url = cursor
      ? `/blogs/newest/cursor?cursor=${cursor}&size=10`
      : `/blogs/newest/cursor?size=10`;
    
    const response = await fetch(url);
    const data = await response.json();
    
    setBlogs(prev => [...prev, ...data.data]);
    setCursor(data.nextCursor);
    setLoading(false);
  };

  useEffect(() => {
    loadMoreBlogs();
  }, []);

  return (
    <div>
      {blogs.map(blog => (
        <BlogCard key={blog.id} blog={blog} />
      ))}
      {cursor && (
        <button onClick={loadMoreBlogs} disabled={loading}>
          {loading ? 'Loading...' : 'Load More'}
        </button>
      )}
    </div>
  );
}
```

---

### HTTP Status Code Quick Reference

| Status | Meaning | Action |
|--------|---------|--------|
| **200** | Success | Display data |
| **201** | Created | Show success, redirect |
| **204** | No Content | Success (no data) |
| **400** | Bad Request | Show error, check input |
| **401** | Unauthorized | Add/refresh auth header |
| **404** | Not Found | Show "not found" message |
| **500** | Server Error | Show error, retry later |

---

### Error Handling Example

```javascript
async function apiCall(endpoint, options = {}) {
  try {
    const response = await fetch(endpoint, options);
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'API Error');
    }
    
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    // Show error to user
    showErrorMessage(error.message);
    throw error;
  }
}
```

---

## Response Code Summary

### Successful Responses
- **200 OK** - Request succeeded, return data
- **201 Created** - Resource created successfully
- **204 No Content** - Request succeeded, no data returned

### Client Errors
- **400 Bad Request** - Invalid input, check validation
- **401 Unauthorized** - Missing/invalid auth header
- **404 Not Found** - Resource doesn't exist

### Server Errors
- **500 Internal Server Error** - Server-side problem, retry

---

## Pagination Quick Tips

### When to Use Offset?
- Page numbers (1, 2, 3...)
- Table sorting with page navigation
- User wants to jump to page 5

### When to Use Cursor?
- Infinite scrolling (Facebook style)
- Real-time feeds
- Large datasets (better performance)

### Cursor Pagination Flow
1. First request: `GET /endpoint?size=10`
2. Get `nextCursor` from response
3. Next request: `GET /endpoint?cursor={nextCursor}&size=10`
4. Repeat until `nextCursor` is null or `hasMore` is false

---

## Testing with cURL

### Create Blog
```bash
curl -X POST http://localhost:8080/blogs/create \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -d '{
    "title": "Test Blog",
    "content": "This is a test",
    "imageUrl": "https://example.com/image.jpg"
  }'
```

### Get Blog Feed
```bash
curl -X GET "http://localhost:8080/blogs/newest/cursor?size=10"
```

### Like Blog
```bash
curl -X POST "http://localhost:8080/likes/toggle?userId=550e8400-e29b-41d4-a716-446655440000&blogId=507f1f77bcf86cd799439011"
```

### Upload Image
```bash
curl -X POST http://localhost:8080/images/upload \
  -F "file=@/path/to/image.jpg"
```

### Get Comments
```bash
curl -X GET "http://localhost:8080/comments/blog/507f1f77bcf86cd799439011"
```

---

## Performance Tips

1. **Use cursor pagination for feeds** - Better performance on large datasets
2. **Filter early** - Use specific endpoints instead of fetching all
3. **Cache responses** - Store data locally when appropriate
4. **Batch requests** - Combine related requests
5. **Upload optimized images** - Resize before upload (max 10MB)

---

## Version Info
- **API Version:** 1.0.0
- **Last Updated:** December 2, 2025
- **Database:** MongoDB
- **Framework:** Spring Boot + Spring WebFlux

---

## Support
- 📖 Full documentation: See `API_AND_DTO_DOCUMENTATION.md`
- 🔗 Swagger UI: `http://localhost:8080/swagger-ui.html`
- 📋 OpenAPI Schema: `http://localhost:8080/v3/api-docs`

