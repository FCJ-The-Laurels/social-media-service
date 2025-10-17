package FCJLaurels.awsrek.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller to provide information about available metrics
 */
@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Metrics information endpoints")
public class MetricsInfoController {

    private final MeterRegistry meterRegistry;

    @GetMapping("/info")
    @Operation(summary = "Get available metrics information",
               description = "Returns a list of all available custom metrics in the application")
    public ResponseEntity<Map<String, Object>> getMetricsInfo() {
        Map<String, Object> metricsInfo = new HashMap<>();

        // Get all meter names
        var meterNames = meterRegistry.getMeters().stream()
                .map(meter -> meter.getId().getName())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        metricsInfo.put("total_metrics", meterNames.size());
        metricsInfo.put("metrics", meterNames);
        metricsInfo.put("prometheus_endpoint", "/actuator/prometheus");
        metricsInfo.put("health_endpoint", "/actuator/health");
        metricsInfo.put("info_endpoint", "/actuator/info");

        // Custom metrics
        Map<String, String> customMetrics = new HashMap<>();
        customMetrics.put("blog.posts.created", "Total number of blog posts created");
        customMetrics.put("blog.posts.deleted", "Total number of blog posts deleted");
        customMetrics.put("comments.created", "Total number of comments created");
        customMetrics.put("comments.deleted", "Total number of comments deleted");
        customMetrics.put("likes.added", "Total number of likes added");
        customMetrics.put("likes.removed", "Total number of likes removed");
        customMetrics.put("images.uploaded", "Total number of images uploaded");
        customMetrics.put("images.deleted", "Total number of images deleted");
        customMetrics.put("api.errors", "Total number of API errors");
        customMetrics.put("authentication.attempts", "Total authentication attempts");
        customMetrics.put("authentication.failures", "Total authentication failures");
        customMetrics.put("http.server.requests.custom", "HTTP request duration");
        customMetrics.put("http.requests.total", "Total HTTP requests");
        customMetrics.put("http.requests.errors", "HTTP request errors");

        metricsInfo.put("custom_metrics", customMetrics);

        return ResponseEntity.ok(metricsInfo);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get metrics summary",
               description = "Returns a summary of key application metrics")
    public ResponseEntity<Map<String, Object>> getMetricsSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Get specific metric values
        var blogCreated = meterRegistry.find("blog.posts.created").counter();
        var blogDeleted = meterRegistry.find("blog.posts.deleted").counter();
        var commentsCreated = meterRegistry.find("comments.created").counter();
        var likesAdded = meterRegistry.find("likes.added").counter();
        var imagesUploaded = meterRegistry.find("images.uploaded").counter();
        var apiErrors = meterRegistry.find("api.errors").counter();

        summary.put("blog_posts_created", blogCreated != null ? blogCreated.count() : 0);
        summary.put("blog_posts_deleted", blogDeleted != null ? blogDeleted.count() : 0);
        summary.put("comments_created", commentsCreated != null ? commentsCreated.count() : 0);
        summary.put("likes_added", likesAdded != null ? likesAdded.count() : 0);
        summary.put("images_uploaded", imagesUploaded != null ? imagesUploaded.count() : 0);
        summary.put("api_errors", apiErrors != null ? apiErrors.count() : 0);

        return ResponseEntity.ok(summary);
    }
}

