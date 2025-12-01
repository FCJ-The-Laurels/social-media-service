# Frontend Integration Guide & Troubleshooting

## Table of Contents
1. [Getting Started](#getting-started)
2. [Setup Instructions](#setup-instructions)
3. [Integration Examples](#integration-examples)
4. [Common Patterns](#common-patterns)
5. [Troubleshooting](#troubleshooting)
6. [Performance Optimization](#performance-optimization)

---

## Getting Started

### Prerequisites
- Frontend framework (React, Vue, Angular, etc.)
- HTTP client library (Fetch API, Axios, etc.)
- Basic understanding of REST APIs

### Base Configuration

```javascript
// config.js
export const API_BASE_URL = 'http://localhost:8080';

export const API_ENDPOINTS = {
  blogs: {
    create: '/blogs/create',
    getById: (id) => `/blogs/${id}/search-by-id`,
    getAll: '/blogs',
    searchByTitle: '/blogs/search-by-title',
    update: (id) => `/blogs/${id}/update-blog`,
    delete: (id) => `/blogs/${id}`,
    paginated: '/blogs/paginated',
    feed: '/blogs/feed',
    newestCursor: '/blogs/newest/cursor',
    newestPaginated: '/blogs/newest/paginated',
    display: (id) => `/blogs/${id}/display`
  },
  comments: {
    create: '/comments/create',
    getById: (id) => `/comments/${id}`,
    getAll: '/comments',
    getByBlog: (blogId) => `/comments/blog/${blogId}`,
    getByUser: (userId) => `/comments/user/${userId}`,
    update: (id) => `/comments/${id}`,
    delete: (id) => `/comments/${id}`,
    deleteByBlog: (blogId) => `/comments/blog/${blogId}`,
    countByBlog: (blogId) => `/comments/blog/${blogId}/count`
  },
  likes: {
    create: '/likes/create',
    getById: (id) => `/likes/${id}`,
    getAll: '/likes',
    getByBlog: (blogId) => `/likes/blog/${blogId}`,
    getByUser: (userId) => `/likes/user/${userId}`,
    check: '/likes/check',
    getByUserAndBlog: (userId, blogId) => `/likes/user/${userId}/blog/${blogId}`,
    toggle: '/likes/toggle',
    delete: (id) => `/likes/${id}`,
    deleteByUserAndBlog: (userId, blogId) => `/likes/user/${userId}/blog/${blogId}`,
    countByBlog: (blogId) => `/likes/blog/${blogId}/count`,
    countByUser: (userId) => `/likes/user/${userId}/count`
  },
  images: {
    upload: '/images/upload',
    create: '/images/create',
    getById: (id) => `/images/${id}`,
    getAll: '/images',
    getByName: (name) => `/images/name/${name}`,
    getByType: (type) => `/images/type/${type}`,
    search: '/images/search',
    update: (id) => `/images/${id}`,
    delete: (id) => `/images/${id}`,
    deleteByType: (type) => `/images/type/${type}`
  }
};
```

---

## Setup Instructions

### 1. Create HTTP Client Wrapper

```javascript
// services/api.js
import { API_BASE_URL } from '../config';

class ApiClient {
  constructor(baseURL = API_BASE_URL) {
    this.baseURL = baseURL;
    this.defaultHeaders = {
      'Content-Type': 'application/json'
    };
  }

  // Add user ID header
  withUserId(userId) {
    return {
      ...this.defaultHeaders,
      'X-User-Id': userId
    };
  }

  // Make request
  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const config = {
      ...options,
      headers: {
        ...this.defaultHeaders,
        ...options.headers
      }
    };

    try {
      const response = await fetch(url, config);

      // Handle non-JSON responses
      if (response.status === 204) {
        return { success: true, status: 204 };
      }

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'API Error');
      }

      return { success: true, data, status: response.status };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: 'ERROR'
      };
    }
  }

  // GET request
  get(endpoint, options = {}) {
    return this.request(endpoint, { method: 'GET', ...options });
  }

  // POST request
  post(endpoint, body, options = {}) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(body),
      ...options
    });
  }

  // PUT request
  put(endpoint, body, options = {}) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(body),
      ...options
    });
  }

  // DELETE request
  delete(endpoint, options = {}) {
    return this.request(endpoint, { method: 'DELETE', ...options });
  }

  // Upload file
  async upload(endpoint, file, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch(url, {
        method: 'POST',
        body: formData,
        ...options
      });

      if (response.status === 204) {
        return { success: true, status: 204 };
      }

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'Upload failed');
      }

      return { success: true, data, status: response.status };
    } catch (error) {
      return {
        success: false,
        error: error.message,
        status: 'ERROR'
      };
    }
  }
}

export default new ApiClient();
```

---

### 2. Create Blog Service

```javascript
// services/blogService.js
import apiClient from './api';
import { API_ENDPOINTS } from '../config';

export const blogService = {
  // Create blog
  createBlog: (title, content, imageUrl, userId) => {
    return apiClient.post(API_ENDPOINTS.blogs.create, {
      title,
      content,
      imageUrl
    }, {
      headers: apiClient.withUserId(userId)
    });
  },

  // Get blog by ID
  getBlogById: (blogId) => {
    return apiClient.get(API_ENDPOINTS.blogs.getById(blogId));
  },

  // Get all blogs
  getAllBlogs: () => {
    return apiClient.get(API_ENDPOINTS.blogs.getAll);
  },

  // Search blogs by title
  searchBlogsByTitle: (title) => {
    return apiClient.get(
      `${API_ENDPOINTS.blogs.searchByTitle}?title=${encodeURIComponent(title)}`
    );
  },

  // Update blog
  updateBlog: (blogId, title, content, imageUrl) => {
    return apiClient.put(API_ENDPOINTS.blogs.update(blogId), {
      title,
      content,
      imageUrl
    });
  },

  // Delete blog
  deleteBlog: (blogId) => {
    return apiClient.delete(API_ENDPOINTS.blogs.delete(blogId));
  },

  // Get paginated blogs
  getPaginatedBlogs: (page = 0, size = 10) => {
    return apiClient.get(
      `${API_ENDPOINTS.blogs.paginated}?page=${page}&size=${size}`
    );
  },

  // Get blog feed (cursor pagination)
  getBlogFeed: (cursor = null, size = 10) => {
    const url = cursor
      ? `${API_ENDPOINTS.blogs.feed}?cursor=${cursor}&size=${size}`
      : `${API_ENDPOINTS.blogs.feed}?size=${size}`;
    return apiClient.get(url);
  },

  // Get newest blogs with cursor
  getNewestBlogsWithCursor: (cursor = null, size = 10) => {
    const url = cursor
      ? `${API_ENDPOINTS.blogs.newestCursor}?cursor=${cursor}&size=${size}`
      : `${API_ENDPOINTS.blogs.newestCursor}?size=${size}`;
    return apiClient.get(url);
  },

  // Get newest blogs paginated
  getNewestBlogsPaginated: (page = 0, size = 10) => {
    return apiClient.get(
      `${API_ENDPOINTS.blogs.newestPaginated}?page=${page}&size=${size}`
    );
  },

  // Get blog display
  getBlogDisplay: (blogId) => {
    return apiClient.get(API_ENDPOINTS.blogs.display(blogId));
  }
};
```

---

### 3. Create Comment Service

```javascript
// services/commentService.js
import apiClient from './api';
import { API_ENDPOINTS } from '../config';

export const commentService = {
  // Create comment
  createComment: (blogId, userId, content) => {
    return apiClient.post(API_ENDPOINTS.comments.create, {
      blogId,
      userId,
      content
    });
  },

  // Get comment by ID
  getCommentById: (commentId) => {
    return apiClient.get(API_ENDPOINTS.comments.getById(commentId));
  },

  // Get all comments
  getAllComments: () => {
    return apiClient.get(API_ENDPOINTS.comments.getAll);
  },

  // Get comments by blog
  getCommentsByBlog: (blogId) => {
    return apiClient.get(API_ENDPOINTS.comments.getByBlog(blogId));
  },

  // Get comments by user
  getCommentsByUser: (userId) => {
    return apiClient.get(API_ENDPOINTS.comments.getByUser(userId));
  },

  // Update comment
  updateComment: (commentId, content) => {
    return apiClient.put(API_ENDPOINTS.comments.update(commentId), {
      content
    });
  },

  // Delete comment
  deleteComment: (commentId) => {
    return apiClient.delete(API_ENDPOINTS.comments.delete(commentId));
  },

  // Count comments on blog
  countComments: (blogId) => {
    return apiClient.get(API_ENDPOINTS.comments.countByBlog(blogId));
  }
};
```

---

### 4. Create Like Service

```javascript
// services/likeService.js
import apiClient from './api';
import { API_ENDPOINTS } from '../config';

export const likeService = {
  // Create like
  createLike: (blogId, userId) => {
    return apiClient.post(API_ENDPOINTS.likes.create, {
      blogId,
      userId
    });
  },

  // Get like by ID
  getLikeById: (likeId) => {
    return apiClient.get(API_ENDPOINTS.likes.getById(likeId));
  },

  // Get all likes
  getAllLikes: () => {
    return apiClient.get(API_ENDPOINTS.likes.getAll);
  },

  // Get likes by blog
  getLikesByBlog: (blogId) => {
    return apiClient.get(API_ENDPOINTS.likes.getByBlog(blogId));
  },

  // Get likes by user
  getLikesByUser: (userId) => {
    return apiClient.get(API_ENDPOINTS.likes.getByUser(userId));
  },

  // Check if user liked
  hasUserLiked: (userId, blogId) => {
    return apiClient.get(
      `${API_ENDPOINTS.likes.check}?userId=${userId}&blogId=${blogId}`
    );
  },

  // Toggle like
  toggleLike: (userId, blogId) => {
    return apiClient.post(
      `${API_ENDPOINTS.likes.toggle}?userId=${userId}&blogId=${blogId}`,
      {}
    );
  },

  // Delete like
  deleteLike: (likeId) => {
    return apiClient.delete(API_ENDPOINTS.likes.delete(likeId));
  },

  // Count likes on blog
  countLikes: (blogId) => {
    return apiClient.get(API_ENDPOINTS.likes.countByBlog(blogId));
  }
};
```

---

### 5. Create Image Service

```javascript
// services/imageService.js
import apiClient from './api';
import { API_ENDPOINTS } from '../config';

export const imageService = {
  // Upload image to S3
  uploadImage: (file) => {
    return apiClient.upload(API_ENDPOINTS.images.upload, file);
  },

  // Create image record
  createImage: (name, url, type) => {
    return apiClient.post(API_ENDPOINTS.images.create, {
      name,
      url,
      type
    });
  },

  // Get image by ID
  getImageById: (imageId) => {
    return apiClient.get(API_ENDPOINTS.images.getById(imageId));
  },

  // Get all images
  getAllImages: () => {
    return apiClient.get(API_ENDPOINTS.images.getAll);
  },

  // Search images
  searchImages: (keyword) => {
    return apiClient.get(
      `${API_ENDPOINTS.images.search}?keyword=${encodeURIComponent(keyword)}`
    );
  },

  // Update image
  updateImage: (imageId, name, url, type) => {
    return apiClient.put(API_ENDPOINTS.images.update(imageId), {
      name,
      url,
      type
    });
  },

  // Delete image
  deleteImage: (imageId) => {
    return apiClient.delete(API_ENDPOINTS.images.delete(imageId));
  }
};
```

---

## Integration Examples

### React Example: Blog Feed with Infinite Scroll

```javascript
// components/BlogFeed.jsx
import React, { useState, useEffect, useRef, useCallback } from 'react';
import { blogService } from '../services/blogService';

function BlogFeed() {
  const [blogs, setBlogs] = useState([]);
  const [cursor, setCursor] = useState(null);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const observerTarget = useRef(null);

  // Load more blogs
  const loadMoreBlogs = useCallback(async () => {
    if (loading || !hasMore) return;

    setLoading(true);
    const result = await blogService.getNewestBlogsWithCursor(cursor, 10);

    if (result.success) {
      setBlogs(prev => [...prev, ...result.data.data]);
      setCursor(result.data.nextCursor);
      setHasMore(!!result.data.nextCursor);
    }

    setLoading(false);
  }, [cursor, loading, hasMore]);

  // Intersection Observer for infinite scroll
  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => {
        if (entries[0].isIntersecting && hasMore && !loading) {
          loadMoreBlogs();
        }
      },
      { threshold: 0.1 }
    );

    if (observerTarget.current) {
      observer.observe(observerTarget.current);
    }

    return () => observer.disconnect();
  }, [loadMoreBlogs, hasMore, loading]);

  // Initial load
  useEffect(() => {
    loadMoreBlogs();
  }, []);

  return (
    <div className="blog-feed">
      {blogs.map(blog => (
        <BlogCard key={blog.id} blog={blog} />
      ))}

      {loading && <div className="loading">Loading...</div>}

      <div ref={observerTarget} className="observer-target" />

      {!hasMore && <div className="end-message">No more blogs</div>}
    </div>
  );
}

function BlogCard({ blog }) {
  const [likeCount, setLikeCount] = useState(0);
  const [commentCount, setCommentCount] = useState(0);
  const [isLiked, setIsLiked] = useState(false);

  useEffect(() => {
    // Load like and comment counts
    Promise.all([
      blogService.getLikesByBlog(blog.id),
      blogService.getCommentsByBlog(blog.id)
    ]).then(([likesRes, commentsRes]) => {
      if (likesRes.success) setLikeCount(likesRes.data.length);
      if (commentsRes.success) setCommentCount(commentsRes.data.length);
    });
  }, [blog.id]);

  return (
    <div className="blog-card">
      <div className="blog-header">
        <img src={blog.authorAvatar} alt={blog.authorName} className="avatar" />
        <h3>{blog.authorName}</h3>
        <time>{new Date(blog.creationDate).toLocaleDateString()}</time>
      </div>

      <h2>{blog.title}</h2>

      {blog.imageURL && <img src={blog.imageURL} alt={blog.title} className="blog-image" />}

      <p className="blog-content">{blog.content}</p>

      <div className="blog-stats">
        <span>{likeCount} Likes</span>
        <span>{commentCount} Comments</span>
      </div>
    </div>
  );
}

export default BlogFeed;
```

---

### React Example: Blog Editor

```javascript
// components/BlogEditor.jsx
import React, { useState } from 'react';
import { blogService } from '../services/blogService';
import { imageService } from '../services/imageService';

function BlogEditor({ userId, onSuccess }) {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [isUploading, setIsUploading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setIsUploading(true);
    const result = await imageService.uploadImage(file);

    if (result.success) {
      setImageUrl(result.data.url);
      setError('');
    } else {
      setError('Image upload failed: ' + result.error);
    }

    setIsUploading(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!title.trim() || !content.trim()) {
      setError('Title and content are required');
      return;
    }

    setIsSubmitting(true);
    const result = await blogService.createBlog(title, content, imageUrl, userId);

    if (result.success) {
      setTitle('');
      setContent('');
      setImageUrl('');
      setError('');
      onSuccess(result.data);
    } else {
      setError('Failed to create blog: ' + result.error);
    }

    setIsSubmitting(false);
  };

  return (
    <form onSubmit={handleSubmit} className="blog-editor">
      <h2>Create New Blog</h2>

      {error && <div className="error-message">{error}</div>}

      <div className="form-group">
        <label>Title</label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Enter blog title"
          maxLength={200}
        />
      </div>

      <div className="form-group">
        <label>Content</label>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Write your blog content here..."
          rows={10}
        />
      </div>

      <div className="form-group">
        <label>Featured Image</label>
        <input
          type="file"
          accept="image/*"
          onChange={handleImageUpload}
          disabled={isUploading}
        />
        {imageUrl && (
          <div className="image-preview">
            <img src={imageUrl} alt="preview" />
          </div>
        )}
      </div>

      <button type="submit" disabled={isSubmitting || isUploading}>
        {isSubmitting ? 'Publishing...' : 'Publish Blog'}
      </button>
    </form>
  );
}

export default BlogEditor;
```

---

### React Example: Like/Comment Section

```javascript
// components/BlogInteractions.jsx
import React, { useState } from 'react';
import { likeService } from '../services/likeService';
import { commentService } from '../services/commentService';

function BlogInteractions({ blogId, userId }) {
  const [isLiked, setIsLiked] = useState(false);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(false);

  // Toggle like
  const handleLike = async () => {
    setLoading(true);
    const result = await likeService.toggleLike(userId, blogId);

    if (result.success) {
      if (result.status === 200) {
        setIsLiked(true);
      } else if (result.status === 204) {
        setIsLiked(false);
      }
    }

    setLoading(false);
  };

  // Add comment
  const handleAddComment = async () => {
    if (!newComment.trim()) return;

    setLoading(true);
    const result = await commentService.createComment(blogId, userId, newComment);

    if (result.success) {
      setComments([...comments, result.data]);
      setNewComment('');
    }

    setLoading(false);
  };

  // Delete comment
  const handleDeleteComment = async (commentId) => {
    const result = await commentService.deleteComment(commentId);

    if (result.success) {
      setComments(comments.filter(c => c.id !== commentId));
    }
  };

  return (
    <div className="blog-interactions">
      <div className="like-section">
        <button
          onClick={handleLike}
          className={`like-btn ${isLiked ? 'liked' : ''}`}
          disabled={loading}
        >
          👍 {isLiked ? 'Liked' : 'Like'}
        </button>
      </div>

      <div className="comment-section">
        <h3>Comments ({comments.length})</h3>

        <div className="comment-input">
          <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Write a comment..."
          />
          <button
            onClick={handleAddComment}
            disabled={loading || !newComment.trim()}
          >
            Post Comment
          </button>
        </div>

        <div className="comments-list">
          {comments.map(comment => (
            <div key={comment.id} className="comment">
              <p>{comment.content}</p>
              <small>{new Date(comment.creationDate).toLocaleString()}</small>
              {comment.userId === userId && (
                <button
                  onClick={() => handleDeleteComment(comment.id)}
                  className="delete-btn"
                >
                  Delete
                </button>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default BlogInteractions;
```

---

## Common Patterns

### Pattern 1: Cache Management

```javascript
// hooks/useApiCache.js
import { useEffect, useState, useCallback } from 'react';

export function useApiCache(key, fetcher, options = {}) {
  const { ttl = 5 * 60 * 1000 } = options; // 5 minutes default
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Use localStorage as cache
  const getCachedData = useCallback(() => {
    const cached = localStorage.getItem(`cache_${key}`);
    if (!cached) return null;

    const { data: cachedData, timestamp } = JSON.parse(cached);
    if (Date.now() - timestamp > ttl) {
      localStorage.removeItem(`cache_${key}`);
      return null;
    }

    return cachedData;
  }, [key, ttl]);

  const setCachedData = useCallback((newData) => {
    localStorage.setItem(`cache_${key}`, JSON.stringify({
      data: newData,
      timestamp: Date.now()
    }));
    setData(newData);
  }, [key]);

  const fetch = useCallback(async (skipCache = false) => {
    if (!skipCache) {
      const cached = getCachedData();
      if (cached) {
        setData(cached);
        return cached;
      }
    }

    setLoading(true);
    try {
      const result = await fetcher();
      setCachedData(result);
      setError(null);
      return result;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetcher, getCachedData, setCachedData]);

  useEffect(() => {
    fetch();
  }, []);

  return { data, loading, error, refetch: () => fetch(true) };
}

// Usage
const { data: blogs, loading, refetch } = useApiCache(
  'blogs_feed',
  () => blogService.getNewestBlogsWithCursor(),
  { ttl: 10 * 60 * 1000 } // 10 minutes
);
```

---

### Pattern 2: Error Boundary

```javascript
// components/ErrorBoundary.jsx
import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('Error caught:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-container">
          <h2>Oops! Something went wrong</h2>
          <p>{this.state.error?.message}</p>
          <button onClick={() => window.location.reload()}>
            Reload Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
```

---

## Troubleshooting

### Issue: CORS Error

**Error Message:** `Access to XMLHttpRequest at 'http://localhost:8080/...' from origin 'http://localhost:3000' has been blocked by CORS policy`

**Solution:**
```javascript
// Your backend Spring Boot application should have CORS enabled
// (This should already be configured in your project)

// Frontend can add headers
const result = await fetch(url, {
  method: 'GET',
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  }
});
```

---

### Issue: 401 Unauthorized on Blog Creation

**Error Message:** `401 Unauthorized - Missing or invalid user ID`

**Solution:**
```javascript
// Make sure to include X-User-Id header
const result = await blogService.createBlog(
  title,
  content,
  imageUrl,
  userId  // ← Must be passed
);
```

---

### Issue: File Upload Fails

**Error Message:** `File size exceeds maximum limit of 10MB` or `Only image files are allowed`

**Solution:**
```javascript
// Check file before upload
const handleImageUpload = (file) => {
  // Check file size (10MB max)
  if (file.size > 10 * 1024 * 1024) {
    alert('File size must be less than 10MB');
    return;
  }

  // Check file type
  if (!file.type.startsWith('image/')) {
    alert('Only image files are allowed');
    return;
  }

  imageService.uploadImage(file);
};
```

---

### Issue: Cursor Pagination Not Working

**Problem:** `nextCursor` is null after first request

**Solution:**
```javascript
// Make sure you're checking for cursor existence
const loadMore = async () => {
  const result = await blogService.getNewestBlogsWithCursor(
    cursor,  // ← Pass cursor from previous response
    10
  );

  if (result.data.nextCursor) {
    // More data available
    setCursor(result.data.nextCursor);
  } else {
    // No more data
    setHasMore(false);
  }
};
```

---

### Issue: Images Not Displaying

**Problem:** Image URLs are broken

**Solution:**
```javascript
// Check if image URL is complete
console.log('Image URL:', imageUrl);

// If using S3 URLs, make sure they're public
// The API returns fully qualified S3 URLs like:
// https://s3-bucket.amazonaws.com/images/file_name.jpg
```

---

## Performance Optimization

### 1. Debounce Search

```javascript
// hooks/useDebounce.js
import { useEffect, useState } from 'react';

export function useDebounce(value, delay = 500) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => clearTimeout(handler);
  }, [value, delay]);

  return debouncedValue;
}

// Usage
function SearchBlogs() {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 300);
  const [results, setResults] = useState([]);

  useEffect(() => {
    if (debouncedSearch) {
      blogService.searchBlogsByTitle(debouncedSearch).then(result => {
        if (result.success) {
          setResults(result.data);
        }
      });
    }
  }, [debouncedSearch]);

  return (
    <>
      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Search blogs..."
      />
      {results.map(blog => (
        <div key={blog.id}>{blog.title}</div>
      ))}
    </>
  );
}
```

---

### 2. Virtual Scrolling for Large Lists

```javascript
// components/VirtualBlogList.jsx
import React from 'react';
import { FixedSizeList } from 'react-window';

function VirtualBlogList({ blogs, itemHeight = 300 }) {
  const Row = ({ index, style }) => (
    <div style={style} className="blog-item">
      <BlogCard blog={blogs[index]} />
    </div>
  );

  return (
    <FixedSizeList
      height={window.innerHeight}
      itemCount={blogs.length}
      itemSize={itemHeight}
      width="100%"
    >
      {Row}
    </FixedSizeList>
  );
}

export default VirtualBlogList;
```

---

### 3. Image Optimization

```javascript
// utils/imageOptimization.js
export async function optimizeImage(file, maxWidth = 1200, maxHeight = 800) {
  return new Promise((resolve) => {
    const reader = new FileReader();

    reader.onload = (e) => {
      const img = new Image();

      img.onload = () => {
        const canvas = document.createElement('canvas');
        let { width, height } = img;

        // Calculate new dimensions
        if (width > maxWidth) {
          height = (height * maxWidth) / width;
          width = maxWidth;
        }

        if (height > maxHeight) {
          width = (width * maxHeight) / height;
          height = maxHeight;
        }

        canvas.width = width;
        canvas.height = height;

        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);

        canvas.toBlob((blob) => {
          resolve(new File([blob], file.name, { type: 'image/jpeg' }));
        }, 'image/jpeg', 0.8); // 80% quality
      };

      img.src = e.target.result;
    };

    reader.readAsDataURL(file);
  });
}

// Usage
const optimizedFile = await optimizeImage(imageFile);
const result = await imageService.uploadImage(optimizedFile);
```

---

## Version Info
- **Last Updated:** December 2, 2025
- **API Version:** 1.0.0
- **Framework:** React (examples provided)

---

## Additional Resources
- 📖 Full API Documentation: `API_AND_DTO_DOCUMENTATION.md`
- ⚡ Quick Reference: `QUICK_REFERENCE.md`
- 🔗 Swagger UI: `http://localhost:8080/swagger-ui.html`

