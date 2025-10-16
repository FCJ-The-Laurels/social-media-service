# Comment and Like API Documentation

## Overview
This document provides comprehensive documentation for the Comment and Like management APIs in the social media blogging platform. Both APIs are built with Spring WebFlux for reactive, non-blocking, high-concurrency operations.

---

## 📝 Comment API

### Base URL
```
/comments
```

### Endpoints

#### 1. Create Comment
**POST** `/comments/create`

Creates a new comment on a blog post.

**Request Body:**
```json
{
  "blogId": "string (required)",
  "userId": "string (required)",
  "content": "string (required)"
}
```

**Response:**
- **201 CREATED**: Comment successfully created
```json
{
  "id": "generated-uuid",
  "blogId": "blog-id",
  "userId": "user-id",
  "content": "comment content",
  "creationDate": "2025-10-16T10:30:00"
}
```
- **400 BAD REQUEST**: Invalid input (validation errors)
- **500 INTERNAL SERVER ERROR**: Server error

---

#### 2. Get Comment by ID
**GET** `/comments/{id}`

Retrieves a specific comment by its ID.

**Path Parameters:**
- `id` (string, required): Comment ID

**Response:**
- **200 OK**: Comment found
- **404 NOT FOUND**: Comment doesn't exist

---

#### 3. Get All Comments
**GET** `/comments`

Retrieves all comments in the system.

**Response:**
- **200 OK**: Returns array of comments (can be empty)

---

#### 4. Get Comments by Blog ID
**GET** `/comments/blog/{blogId}`

Retrieves all comments for a specific blog post.

**Path Parameters:**
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Returns array of comments for the blog

**Example Response:**
```json
[
  {
    "id": "comment-1",
    "blogId": "blog-123",
    "userId": "user-456",
    "content": "Great post!",
    "creationDate": "2025-10-16T10:30:00"
  },
  {
    "id": "comment-2",
    "blogId": "blog-123",
    "userId": "user-789",
    "content": "Thanks for sharing!",
    "creationDate": "2025-10-16T11:45:00"
  }
]
```

---

#### 5. Get Comments by User ID
**GET** `/comments/user/{userId}`

Retrieves all comments created by a specific user.

**Path Parameters:**
- `userId` (string, required): User ID

**Response:**
- **200 OK**: Returns array of comments by the user

---

#### 6. Update Comment
**PUT** `/comments/{id}`

Updates an existing comment's content.

**Path Parameters:**
- `id` (string, required): Comment ID

**Request Body:**
```json
{
  "content": "string (required)"
}
```

**Response:**
- **200 OK**: Comment updated successfully
- **400 BAD REQUEST**: Invalid input
- **404 NOT FOUND**: Comment doesn't exist

---

#### 7. Delete Comment
**DELETE** `/comments/{id}`

Deletes a specific comment.

**Path Parameters:**
- `id` (string, required): Comment ID

**Response:**
- **204 NO CONTENT**: Comment deleted successfully
- **404 NOT FOUND**: Comment doesn't exist

---

#### 8. Delete Comments by Blog ID
**DELETE** `/comments/blog/{blogId}`

Deletes all comments associated with a blog post. Useful when deleting a blog.

**Path Parameters:**
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Returns count of deleted comments
```json
5
```

---

#### 9. Count Comments by Blog ID
**GET** `/comments/blog/{blogId}/count`

Returns the total number of comments for a blog post.

**Path Parameters:**
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Returns comment count
```json
42
```

---

## 👍 Like API

### Base URL
```
/likes
```

### Endpoints

#### 1. Create Like
**POST** `/likes/create`

Creates a new like (user likes a blog post).

**Request Body:**
```json
{
  "blogId": "string (required)",
  "userId": "string (required)"
}
```

**Response:**
- **201 CREATED**: Like successfully created
```json
{
  "id": "generated-uuid",
  "blogId": "blog-id",
  "userId": "user-id",
  "creationDate": "2025-10-16T10:30:00"
}
```
- **400 BAD REQUEST**: Invalid input
- **500 INTERNAL SERVER ERROR**: Server error

---

#### 2. Get Like by ID
**GET** `/likes/{id}`

Retrieves a specific like by its ID.

**Path Parameters:**
- `id` (string, required): Like ID

**Response:**
- **200 OK**: Like found
- **404 NOT FOUND**: Like doesn't exist

---

#### 3. Get All Likes
**GET** `/likes`

Retrieves all likes in the system.

**Response:**
- **200 OK**: Returns array of likes

---

#### 4. Get Likes by Blog ID
**GET** `/likes/blog/{blogId}`

Retrieves all likes for a specific blog post.

**Path Parameters:**
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Returns array of likes

---

#### 5. Get Likes by User ID
**GET** `/likes/user/{userId}`

Retrieves all likes created by a specific user.

**Path Parameters:**
- `userId` (string, required): User ID

**Response:**
- **200 OK**: Returns array of likes by the user

---

#### 6. Check if User Liked Blog
**GET** `/likes/check?userId={userId}&blogId={blogId}`

Checks whether a user has liked a specific blog post.

**Query Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Returns boolean
```json
true
```

**Example Usage:**
```
GET /likes/check?userId=user-123&blogId=blog-456
```

---

#### 7. Get Like by User and Blog
**GET** `/likes/user/{userId}/blog/{blogId}`

Retrieves the specific like record for a user and blog combination.

**Path Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Like found
- **404 NOT FOUND**: No like exists for this combination

---

#### 8. Toggle Like ⭐ (Recommended)
**POST** `/likes/toggle?userId={userId}&blogId={blogId}`

Smart endpoint that likes the blog if not liked, or unlikes if already liked.

**Query Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Blog liked successfully (returns like object)
```json
{
  "id": "like-id",
  "blogId": "blog-id",
  "userId": "user-id",
  "creationDate": "2025-10-16T10:30:00"
}
```
- **204 NO CONTENT**: Blog unliked successfully (no body)

**Example Usage:**
```
POST /likes/toggle?userId=user-123&blogId=blog-456
```

---

#### 9. Delete Like by ID
**DELETE** `/likes/{id}`

Deletes a specific like by its ID.

**Path Parameters:**
- `id` (string, required): Like ID

**Response:**
- **204 NO CONTENT**: Like deleted successfully
- **404 NOT FOUND**: Like doesn't exist

---

#### 10. Delete Like by User and Blog
**DELETE** `/likes/user/{userId}/blog/{blogId}`

Deletes a like using user and blog IDs.

**Path Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog post ID

**Response:**
- **204 NO CONTENT**: Like deleted successfully
- **404 NOT FOUND**: No like found

---

#### 11. Count Likes by Blog ID
**GET** `/likes/blog/{blogId}/count`

Returns the total number of likes for a blog post.

**Path Parameters:**
- `blogId` (string, required): Blog post ID

**Response:**
- **200 OK**: Returns like count
```json
127
```

---

#### 12. Count Likes by User ID
**GET** `/likes/user/{userId}/count`

Returns the total number of likes created by a user.

**Path Parameters:**
- `userId` (string, required): User ID

**Response:**
- **200 OK**: Returns like count
```json
53
```

---

## 🔧 Technical Implementation Details

### Architecture
- **Framework**: Spring Boot 3.5.6 with WebFlux
- **Database**: MongoDB (Reactive)
- **Pattern**: Repository-Service-Controller
- **API Style**: RESTful with reactive streams

### Non-Blocking & High Concurrency
Both APIs use Spring WebFlux with Reactor types:
- **Mono<T>**: For single result operations (0 or 1 item)
- **Flux<T>**: For multiple result operations (0 to N items)

This provides:
✅ Non-blocking I/O operations
✅ Backpressure support
✅ High concurrency handling
✅ Better resource utilization

### Database Indexes (Recommended)
For optimal performance, create these MongoDB indexes:

**Comments Collection:**
```javascript
db.comment.createIndex({ "blogId": 1 })
db.comment.createIndex({ "userId": 1 })
db.comment.createIndex({ "creationDate": -1 })
db.comment.createIndex({ "blogId": 1, "creationDate": -1 })
```

**Likes Collection:**
```javascript
db.like.createIndex({ "blogId": 1 })
db.like.createIndex({ "userId": 1 })
db.like.createIndex({ "userId": 1, "blogId": 1 }, { unique: true })
db.like.createIndex({ "creationDate": -1 })
```

### Validation Rules

**CommentCreationDTO:**
- `blogId`: Required, cannot be blank
- `userId`: Required, cannot be blank
- `content`: Required, cannot be blank

**CommentEditDTO:**
- `content`: Required, cannot be empty

**LikeCreationDTO:**
- `blogId`: Required, cannot be blank
- `userId`: Required, cannot be blank

---

## 🚀 Usage Examples

### Example 1: Create and Display Comments

```javascript
// Create a comment
const createComment = async () => {
  const response = await fetch('http://localhost:8080/comments/create', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      blogId: 'blog-123',
      userId: 'user-456',
      content: 'This is a great article!'
    })
  });
  return response.json();
};

// Get all comments for a blog
const getComments = async (blogId) => {
  const response = await fetch(`http://localhost:8080/comments/blog/${blogId}`);
  return response.json();
};

// Get comment count
const getCommentCount = async (blogId) => {
  const response = await fetch(`http://localhost:8080/comments/blog/${blogId}/count`);
  return response.json();
};
```

### Example 2: Toggle Like Functionality

```javascript
// Toggle like (like/unlike)
const toggleLike = async (userId, blogId) => {
  const response = await fetch(
    `http://localhost:8080/likes/toggle?userId=${userId}&blogId=${blogId}`,
    { method: 'POST' }
  );
  
  if (response.status === 200) {
    const like = await response.json();
    console.log('Liked!', like);
  } else if (response.status === 204) {
    console.log('Unliked!');
  }
};

// Check if user has liked
const hasLiked = async (userId, blogId) => {
  const response = await fetch(
    `http://localhost:8080/likes/check?userId=${userId}&blogId=${blogId}`
  );
  return response.json(); // returns true or false
};

// Get like count
const getLikeCount = async (blogId) => {
  const response = await fetch(`http://localhost:8080/likes/blog/${blogId}/count`);
  return response.json();
};
```

### Example 3: React Component Example

```jsx
import { useState, useEffect } from 'react';

function BlogPost({ blogId, userId }) {
  const [comments, setComments] = useState([]);
  const [likeCount, setLikeCount] = useState(0);
  const [hasLiked, setHasLiked] = useState(false);
  const [newComment, setNewComment] = useState('');

  useEffect(() => {
    // Load comments
    fetch(`http://localhost:8080/comments/blog/${blogId}`)
      .then(res => res.json())
      .then(setComments);

    // Load like count
    fetch(`http://localhost:8080/likes/blog/${blogId}/count`)
      .then(res => res.json())
      .then(setLikeCount);

    // Check if user liked
    fetch(`http://localhost:8080/likes/check?userId=${userId}&blogId=${blogId}`)
      .then(res => res.json())
      .then(setHasLiked);
  }, [blogId, userId]);

  const handleToggleLike = async () => {
    const response = await fetch(
      `http://localhost:8080/likes/toggle?userId=${userId}&blogId=${blogId}`,
      { method: 'POST' }
    );
    
    if (response.status === 200) {
      setHasLiked(true);
      setLikeCount(prev => prev + 1);
    } else if (response.status === 204) {
      setHasLiked(false);
      setLikeCount(prev => prev - 1);
    }
  };

  const handleAddComment = async () => {
    const response = await fetch('http://localhost:8080/comments/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        blogId,
        userId,
        content: newComment
      })
    });
    
    const comment = await response.json();
    setComments([...comments, comment]);
    setNewComment('');
  };

  return (
    <div>
      <button onClick={handleToggleLike}>
        {hasLiked ? '❤️' : '🤍'} {likeCount}
      </button>
      
      <div>
        {comments.map(comment => (
          <div key={comment.id}>
            <p>{comment.content}</p>
            <small>{comment.creationDate}</small>
          </div>
        ))}
      </div>
      
      <input 
        value={newComment} 
        onChange={e => setNewComment(e.target.value)}
        placeholder="Add a comment..."
      />
      <button onClick={handleAddComment}>Post</button>
    </div>
  );
}
```

---

## 🔒 Security Considerations

### Authentication & Authorization
Currently, the APIs accept `userId` as a parameter. In production:
1. Extract `userId` from JWT token (already implemented in your project)
2. Validate user permissions before operations
3. Ensure users can only edit/delete their own comments

### Rate Limiting
Consider implementing rate limiting for:
- Comment creation: Max 10 per minute per user
- Like operations: Max 20 per minute per user

### Input Validation
All DTOs are validated using Jakarta Bean Validation:
- `@NotBlank`: Ensures required fields are provided
- Additional custom validation can be added as needed

---

## 📊 Performance Optimization Tips

### 1. Caching Like Counts
For frequently accessed blogs, cache like counts:
```java
@Cacheable(value = "likeCount", key = "#blogId")
public Mono<Long> countLikesByBlogId(String blogId) {
    return likeRepository.countByBlogId(blogId);
}
```

### 2. Pagination for Comments
For blogs with many comments, implement pagination:
```java
Flux<CommentDTO> getCommentsByBlogId(String blogId, int page, int size);
```

### 3. Batch Operations
Use reactive batch operations for bulk deletions (already implemented).

---

## 🧪 Testing Endpoints

### Using cURL

**Create Comment:**
```bash
curl -X POST http://localhost:8080/comments/create \
  -H "Content-Type: application/json" \
  -d '{"blogId":"blog-123","userId":"user-456","content":"Great post!"}'
```

**Toggle Like:**
```bash
curl -X POST "http://localhost:8080/likes/toggle?userId=user-123&blogId=blog-456"
```

**Get Comments:**
```bash
curl http://localhost:8080/comments/blog/blog-123
```

**Get Like Count:**
```bash
curl http://localhost:8080/likes/blog/blog-123/count
```

---

## 📝 Summary

Your Comment and Like APIs are fully implemented with:

✅ **Complete CRUD operations** for both entities
✅ **Reactive programming** with WebFlux (non-blocking, high concurrency)
✅ **Swagger documentation** (accessible at `/swagger-ui.html`)
✅ **Input validation** with proper error handling
✅ **Optimized queries** with custom repository methods
✅ **RESTful design** with proper HTTP status codes
✅ **Flexible endpoints** for various use cases

### Key Features:
- 9 endpoints for Comments
- 12 endpoints for Likes
- Full reactive stack with Mono/Flux
- MongoDB with reactive driver
- Toggle like functionality for better UX
- Bulk operations for efficiency
- Comprehensive error handling

---

## 🔗 Related APIs

- **Blog API**: Manage blog posts
- **Image API**: Handle blog images
- **User API**: User management (if implemented)

---

*Last Updated: October 16, 2025*

