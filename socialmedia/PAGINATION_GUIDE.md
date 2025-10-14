# Blog Pagination Guide - Infinite Scrolling Implementation

This guide explains how to use the pagination features implemented in the blog controller, similar to Facebook feed and Amazon product listings.

## Overview

We've implemented **two types of pagination**:

1. **Traditional Offset-Based Pagination** - For page numbers (e.g., Page 1, 2, 3...)
2. **Cursor-Based Pagination** - For infinite scrolling (like Facebook/Amazon)

---

## 1. Traditional Offset-Based Pagination

### Endpoint
```
GET /blogs/paginated?page={page}&size={size}
```

### Parameters
- `page` (optional, default: 0) - Page number (0-based index)
- `size` (optional, default: 10) - Number of items per page

### Example Request
```http
GET http://localhost:8080/blogs/paginated?page=0&size=10
```

### Example Response
```json
{
  "content": [
    {
      "id": "123",
      "title": "My Blog Post",
      "content": "Blog content here...",
      "author": "John Doe",
      "creationDate": "2025-10-14T10:30:00",
      "imageUrl": "https://example.com/image.jpg"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 100,
  "totalPages": 10,
  "hasNext": true,
  "hasPrevious": false,
  "nextCursor": "MjAyNS0xMC0xNFQxMDozMDowMA==",
  "previousCursor": "MjAyNS0xMC0xNFQxMTozMDowMA=="
}
```

### Use Case
Best for:
- Traditional pagination with page numbers
- When you need to know total pages/items
- When users need to jump to specific pages

---

## 2. Cursor-Based Pagination (Infinite Scroll) ⭐

### Endpoint
```
GET /blogs/feed?cursor={cursor}&size={size}
```

### Parameters
- `cursor` (optional) - Cursor string from previous response (omit for first page)
- `size` (optional, default: 10) - Number of items to fetch

### How It Works

#### **Step 1: First Request (Initial Load)**
```http
GET http://localhost:8080/blogs/feed?size=10
```
or
```http
GET http://localhost:8080/blogs/feed?cursor=&size=10
```

**Response:**
```json
{
  "content": [
    {
      "id": "1",
      "title": "Latest Blog Post",
      "content": "...",
      "author": "Jane Doe",
      "creationDate": "2025-10-14T15:00:00",
      "imageUrl": "..."
    },
    // ... 9 more items
  ],
  "nextCursor": "MjAyNS0xMC0xNFQxNDowMDowMA==",
  "hasMore": true,
  "size": 10
}
```

#### **Step 2: Load More (Scroll Down)**
When user scrolls to the bottom, use the `nextCursor` from the previous response:

```http
GET http://localhost:8080/blogs/feed?cursor=MjAyNS0xMC0xNFQxNDowMDowMA==&size=10
```

**Response:**
```json
{
  "content": [
    // Next 10 items
  ],
  "nextCursor": "MjAyNS0xMC0xNFQxMzowMDowMA==",
  "hasMore": true,
  "size": 10
}
```

#### **Step 3: Continue Until End**
Keep using the `nextCursor` until `hasMore` becomes `false`:

```json
{
  "content": [
    // Last batch of items (could be less than 10)
  ],
  "nextCursor": null,
  "hasMore": false,
  "size": 5
}
```

---

## Frontend Implementation Examples

### JavaScript/React Example

```javascript
import { useState, useEffect } from 'react';

function BlogFeed() {
  const [blogs, setBlogs] = useState([]);
  const [cursor, setCursor] = useState(null);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);

  const loadBlogs = async () => {
    if (loading || !hasMore) return;
    
    setLoading(true);
    try {
      const url = cursor 
        ? `http://localhost:8080/blogs/feed?cursor=${cursor}&size=10`
        : `http://localhost:8080/blogs/feed?size=10`;
      
      const response = await fetch(url);
      const data = await response.json();
      
      setBlogs(prev => [...prev, ...data.content]);
      setCursor(data.nextCursor);
      setHasMore(data.hasMore);
    } catch (error) {
      console.error('Error loading blogs:', error);
    } finally {
      setLoading(false);
    }
  };

  // Initial load
  useEffect(() => {
    loadBlogs();
  }, []);

  // Infinite scroll listener
  useEffect(() => {
    const handleScroll = () => {
      if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 500) {
        loadBlogs();
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [cursor, hasMore, loading]);

  return (
    <div>
      {blogs.map(blog => (
        <div key={blog.id}>
          <h2>{blog.title}</h2>
          <p>{blog.content}</p>
          <small>By {blog.author} on {blog.creationDate}</small>
        </div>
      ))}
      {loading && <p>Loading more...</p>}
      {!hasMore && <p>No more blogs to load</p>}
    </div>
  );
}
```

### jQuery Example

```javascript
let cursor = null;
let hasMore = true;
let loading = false;

function loadBlogs() {
  if (loading || !hasMore) return;
  
  loading = true;
  $('#loading').show();
  
  const url = cursor 
    ? `/blogs/feed?cursor=${cursor}&size=10`
    : `/blogs/feed?size=10`;
  
  $.get(url)
    .done(function(data) {
      data.content.forEach(blog => {
        $('#blog-container').append(`
          <div class="blog-item">
            <h2>${blog.title}</h2>
            <p>${blog.content}</p>
            <small>By ${blog.author} on ${blog.creationDate}</small>
          </div>
        `);
      });
      
      cursor = data.nextCursor;
      hasMore = data.hasMore;
    })
    .fail(function(error) {
      console.error('Error:', error);
    })
    .always(function() {
      loading = false;
      $('#loading').hide();
    });
}

// Infinite scroll
$(window).scroll(function() {
  if ($(window).scrollTop() + $(window).height() > $(document).height() - 500) {
    loadBlogs();
  }
});

// Initial load
$(document).ready(function() {
  loadBlogs();
});
```

### Vue.js Example

```vue
<template>
  <div>
    <div v-for="blog in blogs" :key="blog.id" class="blog-item">
      <h2>{{ blog.title }}</h2>
      <p>{{ blog.content }}</p>
      <small>By {{ blog.author }} on {{ blog.creationDate }}</small>
    </div>
    <div v-if="loading">Loading more...</div>
    <div v-if="!hasMore">No more blogs</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      blogs: [],
      cursor: null,
      hasMore: true,
      loading: false
    };
  },
  mounted() {
    this.loadBlogs();
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  methods: {
    async loadBlogs() {
      if (this.loading || !this.hasMore) return;
      
      this.loading = true;
      try {
        const url = this.cursor 
          ? `/blogs/feed?cursor=${this.cursor}&size=10`
          : `/blogs/feed?size=10`;
        
        const response = await fetch(url);
        const data = await response.json();
        
        this.blogs.push(...data.content);
        this.cursor = data.nextCursor;
        this.hasMore = data.hasMore;
      } catch (error) {
        console.error('Error loading blogs:', error);
      } finally {
        this.loading = false;
      }
    },
    handleScroll() {
      const bottomOfWindow = window.innerHeight + window.scrollY >= document.body.offsetHeight - 500;
      if (bottomOfWindow) {
        this.loadBlogs();
      }
    }
  }
};
</script>
```

---

## Performance Benefits of Cursor-Based Pagination

### Why Cursor-Based is Better for Infinite Scroll:

1. **Performance**: 
   - Doesn't count total items (no COUNT query)
   - Uses indexed fields (creationDate) for fast queries
   - Consistent performance regardless of page depth

2. **Consistency**:
   - No duplicate items if data changes during pagination
   - No missing items if new items are added

3. **Scalability**:
   - Works efficiently with millions of records
   - No OFFSET overhead (MongoDB doesn't skip records)

### Comparison:

| Feature | Offset-Based | Cursor-Based |
|---------|--------------|--------------|
| Performance | Slow for large offsets | Constant speed |
| Data consistency | Can have duplicates/gaps | Always consistent |
| Random page access | ✅ Yes | ❌ No |
| Total count | ✅ Yes | ❌ No |
| Best for | Page numbers | Infinite scroll |

---

## Testing with Swagger

1. Navigate to: `http://localhost:8080/swagger-ui.html`
2. Find "Blog Management" section
3. Try endpoints:
   - **GET /blogs/paginated** - Traditional pagination
   - **GET /blogs/feed** - Cursor-based pagination

---

## Mobile App Implementation Tips

For mobile apps (Android/iOS):

1. **Load Initial Batch**: Call `/blogs/feed?size=20` on screen load
2. **Detect Scroll**: Monitor RecyclerView/UITableView scroll position
3. **Load More**: When user reaches last 5 items, fetch next batch
4. **Store Cursor**: Keep cursor in memory or local storage
5. **Pull to Refresh**: Reset cursor to null and clear list

---

## Best Practices

1. **Choose appropriate size**: 10-20 items for mobile, 20-50 for desktop
2. **Show loading indicator**: Always indicate when loading more data
3. **Handle errors**: Gracefully handle network errors and retry
4. **Debounce scroll**: Don't trigger multiple simultaneous requests
5. **Cache locally**: Consider caching for offline support

---

## Troubleshooting

### Issue: Getting duplicate items
**Solution**: Make sure you're not calling the API multiple times. Add loading flag.

### Issue: Cursor is null but hasMore is true
**Solution**: This shouldn't happen. Check backend logs or contact support.

### Issue: Items in wrong order
**Solution**: Blogs are sorted by creationDate DESC (newest first). This is by design.

---

## API Response Fields Explained

### BlogPageResponse (Offset-Based)
- `content`: Array of blog DTOs
- `page`: Current page number (0-based)
- `size`: Items per page requested
- `totalElements`: Total number of blogs in database
- `totalPages`: Total number of pages available
- `hasNext`: Boolean indicating if there's a next page
- `hasPrevious`: Boolean indicating if there's a previous page
- `nextCursor`: Cursor for next page (bonus feature)
- `previousCursor`: Cursor for previous page (bonus feature)

### BlogCursorResponse (Cursor-Based)
- `content`: Array of blog DTOs
- `nextCursor`: Cursor string to fetch next batch (null if no more)
- `hasMore`: Boolean indicating if there are more items
- `size`: Actual number of items returned

---

## Summary

✅ **Use `/blogs/paginated`** when:
- You need page numbers (1, 2, 3...)
- Users need to jump to specific pages
- You need to show total count

✅ **Use `/blogs/feed`** when:
- Implementing infinite scroll
- Building mobile apps
- Performance is critical
- Data consistency matters

---

For more information, check the Swagger documentation at `/swagger-ui.html`

