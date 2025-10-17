# Prometheus Monitoring - Quick Start Guide

## ✅ What's Been Implemented

Your Spring Boot application now has comprehensive Prometheus monitoring with:

1. **Actuator Endpoints** - Health checks and metrics exposure
2. **Custom Metrics** - Business-specific tracking (blogs, comments, likes, images)
3. **Auto-tracking** - HTTP requests automatically monitored
4. **MetricsService** - Easy-to-use service for tracking operations
5. **Health Indicators** - Custom MongoDB health checks
6. **Metrics API** - Endpoints to view metrics information

## 🚀 Quick Start

### 1. Start Your Application
```bash
mvn clean install
mvn spring-boot:run
```

### 2. Access Prometheus Metrics
Open in your browser or use curl:
```bash
curl http://localhost:8080/actuator/prometheus
```

You'll see metrics in Prometheus format like:
```
# HELP blog_posts_created_total Total number of blog posts created
# TYPE blog_posts_created_total counter
blog_posts_created_total{application="socialmedia-api",type="blog"} 0.0

# HELP http_requests_total Total HTTP requests
# TYPE http_requests_total counter
http_requests_total{method="GET",uri="/api/blogs",status="200"} 5.0
```

### 3. Check Application Health
```bash
curl http://localhost:8080/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "mongoHealthIndicator": {
      "status": "UP",
      "details": {
        "database": "awsrek_db",
        "status": "Connected"
      }
    }
  }
}
```

### 4. View Available Metrics
```bash
curl http://localhost:8080/api/metrics/info
```

### 5. View Metrics Summary
```bash
curl http://localhost:8080/api/metrics/summary
```

Response:
```json
{
  "blog_posts_created": 10,
  "blog_posts_deleted": 2,
  "comments_created": 45,
  "likes_added": 123,
  "images_uploaded": 8,
  "api_errors": 0
}
```

## 📊 Metrics Being Tracked

### Automatically Tracked (for all endpoints):
- ✅ HTTP request count
- ✅ HTTP request duration
- ✅ HTTP errors (4xx, 5xx)
- ✅ JVM memory usage
- ✅ CPU usage
- ✅ Thread counts
- ✅ Garbage collection

### Custom Business Metrics (Blog Service Example):
- ✅ Blog posts created
- ✅ Blog posts deleted
- ✅ API errors with error types

### Ready to Add (in other services):
- Comments created/deleted
- Likes added/removed
- Images uploaded/deleted
- Authentication attempts/failures

## 🔧 How Metrics Are Tracked

The `BlogServiceImpl` has been updated as an example. Here's how it works:

```java
@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private MetricsService metricsService;
    
    @Override
    public Mono<BlogDTO> createBlog(BlogCreationDTO dto) {
        return blogRepository.save(newBlog)
            .doOnSuccess(saved -> metricsService.incrementBlogCreated())
            .doOnError(error -> metricsService.incrementApiError("BlogCreationError"))
            .map(this::maptoDTO);
    }
    
    @Override
    public Mono<Boolean> deleteBlog(String id) {
        return blogRepository.deleteById(id)
            .doOnSuccess(v -> metricsService.incrementBlogDeleted())
            .thenReturn(true);
    }
}
```

## 📝 Adding Metrics to Other Services

### For Comment Service:
```java
@Autowired
private MetricsService metricsService;

public Mono<CommentDTO> createComment(CommentCreationDTO dto) {
    return commentRepository.save(newComment)
        .doOnSuccess(saved -> metricsService.incrementCommentCreated())
        .map(this::mapToDTO);
}

public Mono<Void> deleteComment(String id) {
    return commentRepository.deleteById(id)
        .doOnSuccess(v -> metricsService.incrementCommentDeleted());
}
```

### For Like Service:
```java
public Mono<LikeDTO> addLike(LikeCreationDTO dto) {
    return likeRepository.save(newLike)
        .doOnSuccess(saved -> metricsService.incrementLikeAdded())
        .map(this::mapToDTO);
}

public Mono<Void> removeLike(String id) {
    return likeRepository.deleteById(id)
        .doOnSuccess(v -> metricsService.incrementLikeRemoved());
}
```

### For Image Service:
```java
public Mono<ImageDTO> uploadImage(ImageCreationDTO dto) {
    return imageRepository.save(newImage)
        .doOnSuccess(saved -> metricsService.incrementImageUploaded())
        .map(this::mapToDTO);
}

public Mono<Void> deleteImage(String id) {
    return imageRepository.deleteById(id)
        .doOnSuccess(v -> metricsService.incrementImageDeleted());
}
```

## 🎯 Setting Up Prometheus Server (Optional)

### 1. Download Prometheus
- Windows: https://prometheus.io/download/
- Extract to `C:\prometheus` (or any location)

### 2. Use the Configuration
Copy `prometheus.yml` from project root to Prometheus directory

### 3. Start Prometheus
```bash
cd C:\prometheus
prometheus.exe --config.file=prometheus.yml
```

### 4. Access Prometheus UI
Open: http://localhost:9090

### 5. Query Metrics
Try these queries in Prometheus:
- `blog_posts_created_total` - Total blogs created
- `rate(http_requests_total[5m])` - Request rate per second
- `http_requests_errors_total` - Total errors
- `jvm_memory_used_bytes` - Memory usage

## 📈 Setting Up Grafana (Optional)

### 1. Install Grafana
Download from: https://grafana.com/grafana/download

### 2. Start Grafana
- Windows: Run `grafana-server.exe` from `bin` folder
- Access: http://localhost:3000
- Default login: admin/admin

### 3. Add Prometheus Data Source
- Configuration → Data Sources → Add Prometheus
- URL: http://localhost:9090
- Save & Test

### 4. Create Dashboard
- Import dashboard ID: 11378 (Spring Boot Statistics)
- Or create custom dashboards

## 🧪 Testing the Implementation

### Test 1: Create a Blog Post
```bash
curl -X POST http://localhost:8080/api/blogs \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Blog",
    "content": "Testing metrics",
    "author": "test@example.com"
  }'
```

Then check:
```bash
curl http://localhost:8080/api/metrics/summary
```

You should see `blog_posts_created` increment!

### Test 2: Generate Some Traffic
```bash
# Make multiple requests
for i in {1..10}; do
  curl http://localhost:8080/api/blogs
done
```

Then check:
```bash
curl http://localhost:8080/actuator/prometheus | grep http_requests_total
```

## 📋 All Actuator Endpoints

Visit: http://localhost:8080/actuator

Available endpoints:
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Available metrics list
- `/actuator/metrics/{metricName}` - Specific metric details
- `/actuator/prometheus` - Prometheus format metrics
- `/actuator/env` - Environment properties
- `/actuator/loggers` - Logger configuration

## 🔐 Security Note

Currently, actuator endpoints are publicly accessible for development. In production:

1. Secure the endpoints:
```java
.requestMatchers("/actuator/**").hasRole("ADMIN")
```

2. Or restrict to specific endpoints:
```properties
management.endpoints.web.exposure.include=health,prometheus
```

3. Use network security to restrict Prometheus scraping to internal networks

## 📚 Next Steps

1. ✅ **Done**: Basic monitoring setup
2. 🔄 **Todo**: Add metrics to Comment, Like, and Image services
3. 🔄 **Todo**: Set up Prometheus server (optional)
4. 🔄 **Todo**: Set up Grafana dashboards (optional)
5. 🔄 **Todo**: Configure alerting rules
6. 🔄 **Todo**: Add custom metrics for specific business needs

## 🆘 Troubleshooting

### Metrics endpoint returns 404
- Ensure actuator dependency is in pom.xml ✅
- Check `management.endpoints.web.exposure.include=*` in properties ✅

### Metrics show 0 values
- Generate some activity by creating blogs, comments, etc.
- Metrics start at 0 and increment with usage

### Prometheus can't scrape
- Verify application is running on port 8080
- Check Windows Firewall settings
- Verify prometheus.yml configuration

## 🎉 Success!

Your application now has enterprise-grade monitoring! The metrics will help you:
- Track business KPIs (posts, comments, likes)
- Monitor performance (response times, error rates)
- Debug issues (error tracking with types)
- Scale effectively (resource usage monitoring)

