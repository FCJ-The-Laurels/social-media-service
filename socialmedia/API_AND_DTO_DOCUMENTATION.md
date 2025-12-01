# API & DTO Documentation

## Table of Contents
1. [Base URL & Configuration](#base-url--configuration)
2. [Authentication](#authentication)
3. [Response Codes](#response-codes)
4. [Blog Management APIs](#blog-management-apis)
5. [Comment Management APIs](#comment-management-apis)
6. [Like Management APIs](#like-management-apis)
7. [Image Management APIs](#image-management-apis)
8. [Data Transfer Objects (DTOs)](#data-transfer-objects-dtos)
9. [Pagination Strategies](#pagination-strategies)
10. [Error Handling](#error-handling)

---

## Base URL & Configuration

### Base URL
```
http://localhost:8080
```

### API Documentation (Swagger/OpenAPI)
```
http://localhost:8080/swagger-ui.html
```

### API JSON Schema
```
http://localhost:8080/v3/api-docs
```

---

## Authentication

### Required Header for Protected Endpoints
- **Header Name:** `X-User-Id`
- **Type:** String (UUID format)
- **Description:** User ID extracted from API Gateway
- **Example:** `X-User-Id: 550e8400-e29b-41d4-a716-446655440000`

**Endpoints requiring X-User-Id:**
- `POST /blogs/create` - Creating a new blog

---

## Response Codes

### Standard HTTP Status Codes

| Code | Name | Meaning | Common Use |
|------|------|---------|-----------|
| 200 | OK | Request succeeded, data returned | GET, PUT operations successful |
| 201 | Created | Resource created successfully | POST operations successful |
| 204 | No Content | Request succeeded, no data returned | DELETE operations successful |
| 400 | Bad Request | Invalid input data or validation errors | Malformed request, missing required fields |
| 401 | Unauthorized | Missing or invalid authentication | Missing X-User-Id header |
| 404 | Not Found | Resource does not exist | Resource ID does not exist |
| 500 | Internal Server Error | Server error during operation | Database errors, unexpected exceptions |

---

## Blog Management APIs

### Base Path: `/blogs`

#### 1. Create a New Blog Post

**Endpoint:** `POST /blogs/create`

**Description:** Creates a new blog post with the provided information. User ID is extracted from X-User-Id header.

**Required Headers:**
- `X-User-Id: {user-id}` (Required)

**Request Body:**
```json
{
  "title": "My First Blog",
  "content": "This is the content of my first blog post...",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Response Codes:**
- **201 CREATED** - Blog successfully created
- **400 BAD REQUEST** - Invalid input data (validation errors)
- **401 UNAUTHORIZED** - Missing or invalid user ID in header
- **500 INTERNAL SERVER ERROR** - Server error during blog creation

**Success Response (201):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "My First Blog",
  "content": "This is the content of my first blog post...",
  "author": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T10:30:00",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Validation Rules:**
- `title`: Required, max 200 characters
- `content`: Required
- `imageUrl`: Optional

---

#### 2. Get Blog by ID

**Endpoint:** `GET /blogs/{id}/search-by-id`

**Description:** Retrieves a specific blog post by its unique identifier.

**Path Parameters:**
- `id` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Blog found
- **404 NOT FOUND** - Blog does not exist

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "My First Blog",
  "content": "This is the content of my first blog post...",
  "author": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T10:30:00",
  "imageUrl": "https://example.com/image.jpg"
}
```

---

#### 3. Get All Blogs

**Endpoint:** `GET /blogs`

**Description:** Retrieves a list of all blog posts.

**Response Codes:**
- **200 OK** - Successfully retrieved all blogs (empty array if no blogs exist)
- **500 INTERNAL SERVER ERROR** - Server error during retrieval

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "title": "My First Blog",
    "content": "Content...",
    "author": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T10:30:00",
    "imageUrl": "https://example.com/image.jpg"
  },
  {
    "id": "507f1f77bcf86cd799439012",
    "title": "My Second Blog",
    "content": "Content...",
    "author": "550e8400-e29b-41d4-a716-446655440001",
    "creationDate": "2025-01-16T11:15:00",
    "imageUrl": "https://example.com/image2.jpg"
  }
]
```

---

#### 4. Get Blogs by Author

**Endpoint:** `GET /blogs/author/{author}`

**Description:** Retrieves all blog posts written by a specific author.

**Path Parameters:**
- `author` (string, required): Author name

**Response Codes:**
- **200 OK** - Successfully retrieved blogs (empty array if no blogs found)
- **400 BAD REQUEST** - Invalid author parameter

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "title": "Blog by John",
    "content": "Content...",
    "author": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T10:30:00",
    "imageUrl": "https://example.com/image.jpg"
  }
]
```

---

#### 5. Search Blogs by Title

**Endpoint:** `GET /blogs/search-by-title`

**Description:** Searches for blog posts containing the specified title keyword (case-insensitive).

**Query Parameters:**
- `title` (string, required): Title search keyword

**Response Codes:**
- **200 OK** - Successfully retrieved matching blogs
- **400 BAD REQUEST** - Invalid search query

**Example Request:**
```
GET /blogs/search-by-title?title=python
```

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "title": "Learning Python for Beginners",
    "content": "Content...",
    "author": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T10:30:00",
    "imageUrl": "https://example.com/image.jpg"
  }
]
```

---

#### 6. Update a Blog Post

**Endpoint:** `PUT /blogs/{id}/update-blog`

**Description:** Updates an existing blog post with the provided information.

**Path Parameters:**
- `id` (string, required): Blog ID

**Request Body:**
```json
{
  "title": "Updated Blog Title",
  "content": "Updated content...",
  "imageUrl": "https://example.com/new-image.jpg"
}
```

**Response Codes:**
- **200 OK** - Blog successfully updated
- **400 BAD REQUEST** - Invalid input data
- **404 NOT FOUND** - Blog does not exist
- **500 INTERNAL SERVER ERROR** - Server error during update

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "Updated Blog Title",
  "content": "Updated content...",
  "author": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T10:30:00",
  "imageUrl": "https://example.com/new-image.jpg"
}
```

**Note:** All fields are optional. Only provided fields will be updated.

---

#### 7. Delete a Blog Post

**Endpoint:** `DELETE /blogs/{id}`

**Description:** Deletes a blog post by its unique identifier.

**Path Parameters:**
- `id` (string, required): Blog ID

**Response Codes:**
- **204 NO CONTENT** - Blog successfully deleted
- **404 NOT FOUND** - Blog does not exist
- **500 INTERNAL SERVER ERROR** - Server error during deletion

**Success Response (204):**
No response body returned.

---

#### 8. Get Paginated Blogs (Offset-based)

**Endpoint:** `GET /blogs/paginated`

**Description:** Retrieves blogs with traditional offset-based pagination. Useful for displaying page numbers.

**Query Parameters:**
- `page` (integer, optional, default: 0): Page number (0-based)
- `size` (integer, optional, default: 10): Number of blogs per page

**Response Codes:**
- **200 OK** - Successfully retrieved paginated blogs
- **400 BAD REQUEST** - Invalid pagination parameters
- **500 INTERNAL SERVER ERROR** - Server error during retrieval

**Example Request:**
```
GET /blogs/paginated?page=0&size=10
```

**Success Response (200):**
```json
{
  "content": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "My First Blog",
      "imageURL": "https://example.com/image.jpg",
      "content": "Content...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 45,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false,
  "nextCursor": "base64_encoded_timestamp",
  "previousCursor": null
}
```

---

#### 9. Get Blogs with Cursor Pagination (Infinite Scroll)

**Endpoint:** `GET /blogs/feed`

**Description:** Retrieves blogs using cursor-based pagination for infinite scrolling. Perfect for continuous loading like Facebook feed or Amazon product list.

**Query Parameters:**
- `cursor` (string, optional): Cursor for pagination (from previous response's nextCursor)
- `size` (integer, optional, default: 10): Number of blogs to retrieve

**Response Codes:**
- **200 OK** - Successfully retrieved blogs
- **400 BAD REQUEST** - Invalid cursor or size parameter
- **500 INTERNAL SERVER ERROR** - Server error during retrieval

**Example Request (First Page):**
```
GET /blogs/feed?size=10
```

**Example Request (Next Page):**
```
GET /blogs/feed?cursor=eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE1VDEwOjMwOjAwIn0&size=10
```

**Success Response (200):**
```json
{
  "content": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "My First Blog",
      "imageURL": "https://example.com/image.jpg",
      "content": "Content...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "nextCursor": "eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE0VTA5OjMwOjAwIn0",
  "hasMore": true,
  "size": 10
}
```

---

#### 10. Get Newest Blogs with Cursor Pagination (Display Format)

**Endpoint:** `GET /blogs/newest/cursor`

**Description:** Retrieves the newest blog posts using cursor-based pagination. Optimized for social media feeds with author information.

**Query Parameters:**
- `cursor` (string, optional): Cursor for pagination (Base64 encoded timestamp)
- `size` (integer, optional, default: 10): Number of blogs to retrieve

**Response Codes:**
- **200 OK** - Successfully retrieved newest blogs
- **400 BAD REQUEST** - Invalid cursor or size parameter
- **500 INTERNAL SERVER ERROR** - Server error during retrieval

**Example Request:**
```
GET /blogs/newest/cursor?size=10
```

**Success Response (200):**
```json
{
  "data": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "My First Blog",
      "imageURL": "https://example.com/image.jpg",
      "content": "Content...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "nextCursor": "eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE0VTA5OjMwOjAwIn0"
}
```

---

#### 11. Get Newest Blogs with Offset Pagination (Display Format)

**Endpoint:** `GET /blogs/newest/paginated`

**Description:** Retrieves the newest blog posts using traditional offset-based pagination with BlogDisplay format. Includes author information and comprehensive pagination metadata.

**Query Parameters:**
- `page` (integer, optional, default: 0): Page number (0-based)
- `size` (integer, optional, default: 10): Number of blogs per page

**Response Codes:**
- **200 OK** - Successfully retrieved newest blogs
- **400 BAD REQUEST** - Invalid pagination parameters
- **500 INTERNAL SERVER ERROR** - Server error during retrieval

**Example Request:**
```
GET /blogs/newest/paginated?page=0&size=10
```

**Success Response (200):**
```json
{
  "content": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "My First Blog",
      "imageURL": "https://example.com/image.jpg",
      "content": "Content...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10,
  "hasNext": true,
  "hasPrevious": false,
  "nextCursor": "base64_encoded_timestamp",
  "previousCursor": null
}
```

---

#### 12. Get Blog Display by ID

**Endpoint:** `GET /blogs/{id}/display`

**Description:** Retrieves a specific blog post by its ID in display format with author information.

**Path Parameters:**
- `id` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Blog found
- **404 NOT FOUND** - Blog does not exist

**Success Response (200):**
```json
{
  "authorName": "John Doe",
  "authorAvatar": "https://example.com/avatar.jpg",
  "title": "My First Blog",
  "imageURL": "https://example.com/image.jpg",
  "content": "Content...",
  "creationDate": "2025-01-15T10:30:00"
}
```

---

## Comment Management APIs

### Base Path: `/comments`

#### 1. Create a New Comment

**Endpoint:** `POST /comments/create`

**Description:** Creates a new comment on a blog post.

**Request Body:**
```json
{
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "content": "Great blog post! I learned a lot from this."
}
```

**Response Codes:**
- **201 CREATED** - Comment successfully created
- **400 BAD REQUEST** - Invalid input data
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (201):**
```json
{
  "id": "507f1f77bcf86cd799439050",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:30:00",
  "content": "Great blog post! I learned a lot from this."
}
```

**Validation Rules:**
- `blogId`: Required
- `userId`: Required
- `content`: Required

---

#### 2. Get Comment by ID

**Endpoint:** `GET /comments/{id}`

**Description:** Retrieves a specific comment by its unique identifier.

**Path Parameters:**
- `id` (string, required): Comment ID

**Response Codes:**
- **200 OK** - Comment found
- **404 NOT FOUND** - Comment does not exist

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439050",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:30:00",
  "content": "Great blog post! I learned a lot from this."
}
```

---

#### 3. Get All Comments

**Endpoint:** `GET /comments`

**Description:** Retrieves a list of all comments.

**Response Codes:**
- **200 OK** - Successfully retrieved all comments
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439050",
    "blogId": "507f1f77bcf86cd799439011",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T11:30:00",
    "content": "Great blog post!"
  }
]
```

---

#### 4. Get Comments by Blog ID

**Endpoint:** `GET /comments/blog/{blogId}`

**Description:** Retrieves all comments for a specific blog post.

**Path Parameters:**
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Successfully retrieved comments (empty array if no comments)
- **400 BAD REQUEST** - Invalid blog ID

**Example Request:**
```
GET /comments/blog/507f1f77bcf86cd799439011
```

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439050",
    "blogId": "507f1f77bcf86cd799439011",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T11:30:00",
    "content": "Great blog post!"
  },
  {
    "id": "507f1f77bcf86cd799439051",
    "blogId": "507f1f77bcf86cd799439011",
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "creationDate": "2025-01-15T12:00:00",
    "content": "I agree with you!"
  }
]
```

---

#### 5. Get Comments by User ID

**Endpoint:** `GET /comments/user/{userId}`

**Description:** Retrieves all comments created by a specific user.

**Path Parameters:**
- `userId` (string, required): User ID

**Response Codes:**
- **200 OK** - Successfully retrieved comments
- **400 BAD REQUEST** - Invalid user ID

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439050",
    "blogId": "507f1f77bcf86cd799439011",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T11:30:00",
    "content": "Great blog post!"
  }
]
```

---

#### 6. Update a Comment

**Endpoint:** `PUT /comments/{id}`

**Description:** Updates an existing comment with the provided information.

**Path Parameters:**
- `id` (string, required): Comment ID

**Request Body:**
```json
{
  "content": "Updated comment content..."
}
```

**Response Codes:**
- **200 OK** - Comment successfully updated
- **400 BAD REQUEST** - Invalid input data
- **404 NOT FOUND** - Comment does not exist
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439050",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:30:00",
  "content": "Updated comment content..."
}
```

**Validation Rules:**
- `content`: Required, cannot be empty

---

#### 7. Delete a Comment

**Endpoint:** `DELETE /comments/{id}`

**Description:** Deletes a comment by its unique identifier.

**Path Parameters:**
- `id` (string, required): Comment ID

**Response Codes:**
- **204 NO CONTENT** - Comment successfully deleted
- **404 NOT FOUND** - Comment does not exist
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (204):**
No response body returned.

---

#### 8. Delete Comments by Blog ID

**Endpoint:** `DELETE /comments/blog/{blogId}`

**Description:** Deletes all comments associated with a specific blog post.

**Path Parameters:**
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Successfully deleted comments

**Success Response (200):**
```json
3
```
(Returns the count of deleted comments)

---

#### 9. Count Comments by Blog ID

**Endpoint:** `GET /comments/blog/{blogId}/count`

**Description:** Returns the total number of comments for a blog post.

**Path Parameters:**
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Successfully retrieved comment count

**Success Response (200):**
```json
15
```

---

## Like Management APIs

### Base Path: `/likes`

#### 1. Create a New Like

**Endpoint:** `POST /likes/create`

**Description:** Creates a new like when a user likes a blog post.

**Request Body:**
```json
{
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response Codes:**
- **201 CREATED** - Like successfully created
- **400 BAD REQUEST** - Invalid input data
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (201):**
```json
{
  "id": "507f1f77bcf86cd799439070",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:45:00"
}
```

**Validation Rules:**
- `blogId`: Required
- `userId`: Required

---

#### 2. Get Like by ID

**Endpoint:** `GET /likes/{id}`

**Description:** Retrieves a specific like by its unique identifier.

**Path Parameters:**
- `id` (string, required): Like ID

**Response Codes:**
- **200 OK** - Like found
- **404 NOT FOUND** - Like does not exist

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439070",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:45:00"
}
```

---

#### 3. Get All Likes

**Endpoint:** `GET /likes`

**Description:** Retrieves a list of all likes.

**Response Codes:**
- **200 OK** - Successfully retrieved all likes
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439070",
    "blogId": "507f1f77bcf86cd799439011",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T11:45:00"
  }
]
```

---

#### 4. Get Likes by Blog ID

**Endpoint:** `GET /likes/blog/{blogId}`

**Description:** Retrieves all likes for a specific blog post.

**Path Parameters:**
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Successfully retrieved likes
- **400 BAD REQUEST** - Invalid blog ID

**Example Request:**
```
GET /likes/blog/507f1f77bcf86cd799439011
```

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439070",
    "blogId": "507f1f77bcf86cd799439011",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T11:45:00"
  }
]
```

---

#### 5. Get Likes by User ID

**Endpoint:** `GET /likes/user/{userId}`

**Description:** Retrieves all likes created by a specific user.

**Path Parameters:**
- `userId` (string, required): User ID

**Response Codes:**
- **200 OK** - Successfully retrieved likes
- **400 BAD REQUEST** - Invalid user ID

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439070",
    "blogId": "507f1f77bcf86cd799439011",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "creationDate": "2025-01-15T11:45:00"
  }
]
```

---

#### 6. Check if User Liked Blog

**Endpoint:** `GET /likes/check`

**Description:** Checks whether a user has liked a specific blog post.

**Query Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Successfully checked like status
- **400 BAD REQUEST** - Invalid parameters

**Example Request:**
```
GET /likes/check?userId=550e8400-e29b-41d4-a716-446655440000&blogId=507f1f77bcf86cd799439011
```

**Success Response (200):**
```json
true
```
(Returns boolean: true if user liked the blog, false otherwise)

---

#### 7. Get Like by User and Blog

**Endpoint:** `GET /likes/user/{userId}/blog/{blogId}`

**Description:** Retrieves a specific like by user ID and blog ID.

**Path Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Like found
- **404 NOT FOUND** - No like found for this user-blog combination

**Example Request:**
```
GET /likes/user/550e8400-e29b-41d4-a716-446655440000/blog/507f1f77bcf86cd799439011
```

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439070",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:45:00"
}
```

---

#### 8. Toggle Like (Like/Unlike)

**Endpoint:** `POST /likes/toggle`

**Description:** Likes the blog if not liked, unlikes if already liked.

**Query Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Blog liked successfully (returns created like)
- **204 NO CONTENT** - Blog unliked successfully
- **400 BAD REQUEST** - Invalid parameters

**Example Request (First Like):**
```
POST /likes/toggle?userId=550e8400-e29b-41d4-a716-446655440000&blogId=507f1f77bcf86cd799439011
```

**Success Response (200 - Liked):**
```json
{
  "id": "507f1f77bcf86cd799439070",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:45:00"
}
```

**Success Response (204 - Unliked):**
No response body returned.

---

#### 9. Delete a Like

**Endpoint:** `DELETE /likes/{id}`

**Description:** Deletes a like by its unique identifier.

**Path Parameters:**
- `id` (string, required): Like ID

**Response Codes:**
- **204 NO CONTENT** - Like successfully deleted
- **404 NOT FOUND** - Like does not exist
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (204):**
No response body returned.

---

#### 10. Delete Like by User and Blog

**Endpoint:** `DELETE /likes/user/{userId}/blog/{blogId}`

**Description:** Deletes a like by user ID and blog ID.

**Path Parameters:**
- `userId` (string, required): User ID
- `blogId` (string, required): Blog ID

**Response Codes:**
- **204 NO CONTENT** - Like successfully deleted
- **404 NOT FOUND** - No like found for this user-blog combination

**Success Response (204):**
No response body returned.

---

#### 11. Count Likes by Blog ID

**Endpoint:** `GET /likes/blog/{blogId}/count`

**Description:** Returns the total number of likes for a blog post.

**Path Parameters:**
- `blogId` (string, required): Blog ID

**Response Codes:**
- **200 OK** - Successfully retrieved like count

**Success Response (200):**
```json
42
```

---

#### 12. Count Likes by User ID

**Endpoint:** `GET /likes/user/{userId}/count`

**Description:** Returns the total number of likes created by a user.

**Path Parameters:**
- `userId` (string, required): User ID

**Response Codes:**
- **200 OK** - Successfully retrieved like count

**Success Response (200):**
```json
15
```

---

## Image Management APIs

### Base Path: `/images`

#### 1. Upload Image to S3

**Endpoint:** `POST /images/upload`

**Description:** Uploads an image file to AWS S3 and returns the public URL along with saved metadata.

**Content-Type:** `multipart/form-data`

**Form Parameters:**
- `file` (file, required): Image file to upload

**Response Codes:**
- **201 CREATED** - Image successfully uploaded
- **400 BAD REQUEST** - Invalid file or validation errors
- **500 INTERNAL SERVER ERROR** - S3 upload failure

**Example Request (cURL):**
```bash
curl -X POST "http://localhost:8080/images/upload" \
  -F "file=@/path/to/image.jpg"
```

**Success Response (201):**
```json
{
  "id": "507f1f77bcf86cd799439100",
  "name": "image_1234567890.jpg",
  "url": "https://s3-bucket.amazonaws.com/images/image_1234567890.jpg",
  "type": "jpg",
  "creationDate": "2025-01-15T12:00:00"
}
```

**Error Response (400):**
```json
"File is empty"
```

**Validation Rules:**
- File must not be empty
- File size must not exceed 10MB
- File must be an image (content-type starts with `image/`)

---

#### 2. Create Image Record

**Endpoint:** `POST /images/create`

**Description:** Creates a new image record with name, URL, and type.

**Request Body:**
```json
{
  "name": "my-profile-image",
  "url": "https://example.com/images/profile.jpg",
  "type": "jpg"
}
```

**Response Codes:**
- **201 CREATED** - Image successfully created
- **400 BAD REQUEST** - Invalid input data
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (201):**
```json
{
  "id": "507f1f77bcf86cd799439100",
  "name": "my-profile-image",
  "url": "https://example.com/images/profile.jpg",
  "type": "jpg",
  "creationDate": "2025-01-15T12:00:00"
}
```

**Validation Rules:**
- `name`: Required
- `url`: Required
- `type`: Required

---

#### 3. Get Image by ID

**Endpoint:** `GET /images/{id}`

**Description:** Retrieves a specific image by its unique identifier.

**Path Parameters:**
- `id` (string, required): Image ID

**Response Codes:**
- **200 OK** - Image found
- **404 NOT FOUND** - Image does not exist

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439100",
  "name": "my-profile-image",
  "url": "https://example.com/images/profile.jpg",
  "type": "jpg",
  "creationDate": "2025-01-15T12:00:00"
}
```

---

#### 4. Get All Images

**Endpoint:** `GET /images`

**Description:** Retrieves a list of all images.

**Response Codes:**
- **200 OK** - Successfully retrieved all images
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439100",
    "name": "my-profile-image",
    "url": "https://example.com/images/profile.jpg",
    "type": "jpg",
    "creationDate": "2025-01-15T12:00:00"
  }
]
```

---

#### 5. Get Images by Name

**Endpoint:** `GET /images/name/{name}`

**Description:** Retrieves all images with a specific name.

**Path Parameters:**
- `name` (string, required): Image name

**Response Codes:**
- **200 OK** - Successfully retrieved images
- **400 BAD REQUEST** - Invalid name parameter

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439100",
    "name": "my-profile-image",
    "url": "https://example.com/images/profile.jpg",
    "type": "jpg",
    "creationDate": "2025-01-15T12:00:00"
  }
]
```

---

#### 6. Get Images by Type

**Endpoint:** `GET /images/type/{type}`

**Description:** Retrieves all images of a specific type (e.g., jpg, png, gif).

**Path Parameters:**
- `type` (string, required): Image type

**Response Codes:**
- **200 OK** - Successfully retrieved images by type
- **400 BAD REQUEST** - Invalid type parameter

**Example Request:**
```
GET /images/type/jpg
```

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439100",
    "name": "my-profile-image",
    "url": "https://example.com/images/profile.jpg",
    "type": "jpg",
    "creationDate": "2025-01-15T12:00:00"
  }
]
```

---

#### 7. Search Images by Name

**Endpoint:** `GET /images/search`

**Description:** Searches for images containing the specified keyword in their name (case-insensitive).

**Query Parameters:**
- `keyword` (string, required): Name search keyword

**Response Codes:**
- **200 OK** - Successfully retrieved matching images
- **400 BAD REQUEST** - Invalid search query

**Example Request:**
```
GET /images/search?keyword=profile
```

**Success Response (200):**
```json
[
  {
    "id": "507f1f77bcf86cd799439100",
    "name": "my-profile-image",
    "url": "https://example.com/images/profile.jpg",
    "type": "jpg",
    "creationDate": "2025-01-15T12:00:00"
  }
]
```

---

#### 8. Update an Image

**Endpoint:** `PUT /images/{id}`

**Description:** Updates an existing image's information.

**Path Parameters:**
- `id` (string, required): Image ID

**Request Body:**
```json
{
  "name": "updated-profile-image",
  "url": "https://example.com/images/new-profile.jpg",
  "type": "png"
}
```

**Response Codes:**
- **200 OK** - Image successfully updated
- **400 BAD REQUEST** - Invalid input data
- **404 NOT FOUND** - Image does not exist
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (200):**
```json
{
  "id": "507f1f77bcf86cd799439100",
  "name": "updated-profile-image",
  "url": "https://example.com/images/new-profile.jpg",
  "type": "png",
  "creationDate": "2025-01-15T12:00:00"
}
```

**Note:** All fields are optional.

---

#### 9. Delete an Image

**Endpoint:** `DELETE /images/{id}`

**Description:** Deletes an image by its unique identifier.

**Path Parameters:**
- `id` (string, required): Image ID

**Response Codes:**
- **204 NO CONTENT** - Image successfully deleted
- **404 NOT FOUND** - Image does not exist
- **500 INTERNAL SERVER ERROR** - Server error

**Success Response (204):**
No response body returned.

---

#### 10. Delete Images by Type

**Endpoint:** `DELETE /images/type/{type}`

**Description:** Deletes all images of a specific type.

**Path Parameters:**
- `type` (string, required): Image type

**Response Codes:**
- **200 OK** - Successfully deleted images

**Success Response (200):**
```json
5
```
(Returns the count of deleted images)

---

## Data Transfer Objects (DTOs)

### Blog DTOs

#### BlogDTO
Used for standard blog responses containing complete blog information.

```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "My First Blog",
  "content": "This is the content of my first blog post...",
  "author": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T10:30:00",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Fields:**
- `id` (string): Unique identifier (MongoDB ObjectId)
- `title` (string): Blog title (max 200 characters)
- `content` (string): Blog content
- `author` (UUID): Author's unique identifier
- `creationDate` (datetime): When the blog was created
- `imageUrl` (string): URL to the blog's featured image

---

#### BlogCreationDTO
Used when creating a new blog post.

```json
{
  "title": "My First Blog",
  "content": "This is the content of my first blog post...",
  "imageUrl": "https://example.com/image.jpg"
}
```

**Fields:**
- `title` (string, required): Blog title (max 200 characters)
- `content` (string, required): Blog content
- `imageUrl` (string, optional): Featured image URL

---

#### BlogEditDTO
Used when updating an existing blog post.

```json
{
  "title": "Updated Blog Title",
  "content": "Updated content...",
  "imageUrl": "https://example.com/new-image.jpg"
}
```

**Fields:**
- `title` (string, optional): Blog title (max 200 characters)
- `content` (string, optional): Blog content
- `imageUrl` (string, optional): Featured image URL

**Note:** All fields are optional. Only provided fields will be updated.

---

#### BlogDisplay
Used for displaying blogs with author information in feeds and pagination responses.

```json
{
  "authorName": "John Doe",
  "authorAvatar": "https://example.com/avatar.jpg",
  "title": "My First Blog",
  "imageURL": "https://example.com/image.jpg",
  "content": "Content...",
  "creationDate": "2025-01-15T10:30:00"
}
```

**Fields:**
- `authorName` (string): Name of the blog author
- `authorAvatar` (string): Avatar URL of the blog author
- `title` (string): Blog title
- `imageURL` (string): Featured image URL
- `content` (string): Blog content
- `creationDate` (datetime): When the blog was created

---

#### BlogPageResponse
Used for offset-based pagination responses.

```json
{
  "content": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "My First Blog",
      "imageURL": "https://example.com/image.jpg",
      "content": "Content...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 45,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false,
  "nextCursor": "base64_encoded_timestamp",
  "previousCursor": null
}
```

**Fields:**
- `content` (array): List of BlogDisplay objects
- `page` (integer): Current page number (0-based)
- `size` (integer): Number of items per page
- `totalElements` (integer): Total number of blogs
- `totalPages` (integer): Total number of pages
- `hasNext` (boolean): Whether there's a next page
- `hasPrevious` (boolean): Whether there's a previous page
- `nextCursor` (string): Base64 encoded cursor for next page
- `previousCursor` (string): Base64 encoded cursor for previous page

---

#### BlogCursorResponse
Used for cursor-based pagination responses (infinite scroll).

```json
{
  "content": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "My First Blog",
      "imageURL": "https://example.com/image.jpg",
      "content": "Content...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "nextCursor": "eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE0VTA5OjMwOjAwIn0",
  "hasMore": true,
  "size": 10
}
```

**Fields:**
- `content` (array): List of BlogDisplay objects
- `nextCursor` (string): Base64 encoded cursor for next batch
- `hasMore` (boolean): Whether there are more items to load
- `size` (integer): Number of items in current batch

---

#### CursorPageDTO<T>
Generic cursor-based pagination response wrapper.

```json
{
  "data": [
    {
      "authorName": "John Doe",
      "authorAvatar": "https://example.com/avatar.jpg",
      "title": "My First Blog",
      "imageURL": "https://example.com/image.jpg",
      "content": "Content...",
      "creationDate": "2025-01-15T10:30:00"
    }
  ],
  "nextCursor": "eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE0VTA5OjMwOjAwIn0"
}
```

**Fields:**
- `data` (array): List of items of type T (e.g., BlogDisplay)
- `nextCursor` (string): Base64 encoded cursor for next batch

---

### Comment DTOs

#### CommentDTO
Used for standard comment responses.

```json
{
  "id": "507f1f77bcf86cd799439050",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:30:00",
  "content": "Great blog post! I learned a lot from this."
}
```

**Fields:**
- `id` (string): Unique identifier (MongoDB ObjectId)
- `blogId` (string): ID of the blog being commented on
- `userId` (string): ID of the user who made the comment
- `creationDate` (datetime): When the comment was created
- `content` (string): Comment content

---

#### CommentCreationDTO
Used when creating a new comment.

```json
{
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "content": "Great blog post! I learned a lot from this."
}
```

**Fields:**
- `blogId` (string, required): ID of the blog being commented on
- `userId` (string, required): ID of the user making the comment
- `content` (string, required): Comment content

---

#### CommentEditDTO
Used when updating an existing comment.

```json
{
  "content": "Updated comment content..."
}
```

**Fields:**
- `content` (string, required): Updated comment content

---

### Like DTOs

#### LikeDTO
Used for standard like responses.

```json
{
  "id": "507f1f77bcf86cd799439070",
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "creationDate": "2025-01-15T11:45:00"
}
```

**Fields:**
- `id` (string): Unique identifier (MongoDB ObjectId)
- `blogId` (string): ID of the blog being liked
- `userId` (string): ID of the user who liked the blog
- `creationDate` (datetime): When the like was created

---

#### LikeCreationDTO
Used when creating a new like.

```json
{
  "blogId": "507f1f77bcf86cd799439011",
  "userId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Fields:**
- `blogId` (string, required): ID of the blog to like
- `userId` (string, required): ID of the user making the like

---

### Image DTOs

#### ImageDTO
Used for standard image responses.

```json
{
  "id": "507f1f77bcf86cd799439100",
  "name": "my-profile-image",
  "url": "https://example.com/images/profile.jpg",
  "type": "jpg",
  "creationDate": "2025-01-15T12:00:00"
}
```

**Fields:**
- `id` (string): Unique identifier (MongoDB ObjectId)
- `name` (string): Image name
- `url` (string): Image URL
- `type` (string): Image type (jpg, png, gif, etc.)
- `creationDate` (datetime): When the image was uploaded

---

#### ImageCreationDTO
Used when creating a new image record.

```json
{
  "name": "my-profile-image",
  "url": "https://example.com/images/profile.jpg",
  "type": "jpg"
}
```

**Fields:**
- `name` (string, required): Image name
- `url` (string, required): Image URL
- `type` (string, required): Image type

---

#### ImageEditDTO
Used when updating an image record.

```json
{
  "name": "updated-profile-image",
  "url": "https://example.com/images/new-profile.jpg",
  "type": "png"
}
```

**Fields:**
- `name` (string, optional): Image name
- `url` (string, optional): Image URL
- `type` (string, optional): Image type

---

## Pagination Strategies

### Offset-Based Pagination (Traditional)

**Best for:** Page numbers display (1, 2, 3, 4...)

**Advantages:**
- Users can jump to specific page
- Total page count known upfront
- Simple to understand

**Disadvantages:**
- Performance issues with large datasets
- Inconsistent results if data changes between requests

**Usage Example:**
```
GET /blogs/paginated?page=0&size=10
```

**Response:**
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 45,
  "totalPages": 5,
  "hasNext": true,
  "hasPrevious": false
}
```

---

### Cursor-Based Pagination (Infinite Scroll)

**Best for:** Infinite scrolling (Facebook feed, Amazon products)

**Advantages:**
- Efficient for large datasets
- Consistent results even if data changes
- Better performance on large tables
- Used by social media platforms

**Disadvantages:**
- Cannot jump to specific page
- Total count might not be known

**Usage Example (First Request):**
```
GET /blogs/feed?size=10
```

**Response:**
```json
{
  "content": [...],
  "nextCursor": "eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE0VTA5OjMwOjAwIn0",
  "hasMore": true,
  "size": 10
}
```

**Usage Example (Next Request):**
```
GET /blogs/feed?cursor=eyJjcmVhdGlvbkRhdGUiOiIyMDI1LTAxLTE0VTA5OjMwOjAwIn0&size=10
```

---

## Error Handling

### Error Response Format

All errors return a structured response with appropriate HTTP status codes.

**General Error Response:**
```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Title must not exceed 200 characters",
  "path": "/blogs/create"
}
```

---

### Common Error Scenarios

#### 400 Bad Request - Validation Error
**Cause:** Invalid input data
```json
{
  "message": "Title must not exceed 200 characters"
}
```

#### 400 Bad Request - Invalid File Upload
**Cause:** File validation failed
```json
"File size exceeds maximum limit of 10MB"
```

#### 401 Unauthorized - Missing Header
**Cause:** Missing X-User-Id header
```json
{
  "message": "Missing or invalid user ID"
}
```

#### 404 Not Found
**Cause:** Resource does not exist
```json
{
  "message": "Blog with id 507f1f77bcf86cd799439999 not found"
}
```

#### 500 Internal Server Error
**Cause:** Server-side error
```json
{
  "message": "Internal server error occurred"
}
```

---

## Frontend Integration Guide

### Getting Started

1. **Setup Base URL:**
   ```javascript
   const BASE_URL = 'http://localhost:8080';
   ```

2. **Include User ID Header (where required):**
   ```javascript
   const headers = {
     'X-User-Id': userId,
     'Content-Type': 'application/json'
   };
   ```

3. **Handle Pagination:**
   - Use cursor pagination for infinite scrolling feeds
   - Use offset pagination for page number navigation

4. **File Upload Example:**
   ```javascript
   const formData = new FormData();
   formData.append('file', imageFile);
   
   fetch(`${BASE_URL}/images/upload`, {
     method: 'POST',
     body: formData
   });
   ```

---

## Example Frontend Flows

### Create and Display Blog Post

1. User creates blog → `POST /blogs/create`
2. Receive `BlogDTO` with blog ID
3. Display created blog

### Display Blog Feed (Infinite Scroll)

1. Load first page → `GET /blogs/newest/cursor?size=10`
2. Display results using `CursorPageDTO<BlogDisplay>`
3. On scroll to bottom, load next → `GET /blogs/newest/cursor?cursor={nextCursor}&size=10`
4. Repeat until `hasMore` is false

### Comment on Blog

1. User types comment
2. Submit → `POST /comments/create`
3. Receive `CommentDTO`
4. Add to comment list

### Like/Unlike Blog

1. User clicks like button
2. Call → `POST /likes/toggle?userId={userId}&blogId={blogId}`
3. Toggle like state based on response

---

## Support & Testing

For API testing, use:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Postman:** Import the OpenAPI schema from `/v3/api-docs`
- **cURL:** See examples in endpoint sections

---

## Last Updated
December 2, 2025

## Version
1.0.0

