package FCJLaurels.awsrek.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Service for tracking custom metrics in the application
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    /**
     * Increment blog creation counter
     */
    public void incrementBlogCreated() {
        Counter.builder("blog.posts.created")
                .description("Total number of blog posts created")
                .tag("type", "blog")
                .register(meterRegistry)
                .increment();
        log.debug("Blog creation metric incremented");
    }

    /**
     * Increment blog deletion counter
     */
    public void incrementBlogDeleted() {
        Counter.builder("blog.posts.deleted")
                .description("Total number of blog posts deleted")
                .tag("type", "blog")
                .register(meterRegistry)
                .increment();
        log.debug("Blog deletion metric incremented");
    }

    /**
     * Increment comment creation counter
     */
    public void incrementCommentCreated() {
        Counter.builder("comments.created")
                .description("Total number of comments created")
                .tag("type", "comment")
                .register(meterRegistry)
                .increment();
        log.debug("Comment creation metric incremented");
    }

    /**
     * Increment comment deletion counter
     */
    public void incrementCommentDeleted() {
        Counter.builder("comments.deleted")
                .description("Total number of comments deleted")
                .tag("type", "comment")
                .register(meterRegistry)
                .increment();
        log.debug("Comment deletion metric incremented");
    }

    /**
     * Increment like counter
     */
    public void incrementLikeAdded() {
        Counter.builder("likes.added")
                .description("Total number of likes added")
                .tag("type", "like")
                .register(meterRegistry)
                .increment();
        log.debug("Like added metric incremented");
    }

    /**
     * Increment like removal counter
     */
    public void incrementLikeRemoved() {
        Counter.builder("likes.removed")
                .description("Total number of likes removed")
                .tag("type", "like")
                .register(meterRegistry)
                .increment();
        log.debug("Like removed metric incremented");
    }

    /**
     * Increment image upload counter
     */
    public void incrementImageUploaded() {
        Counter.builder("images.uploaded")
                .description("Total number of images uploaded")
                .tag("type", "image")
                .register(meterRegistry)
                .increment();
        log.debug("Image upload metric incremented");
    }

    /**
     * Increment image deletion counter
     */
    public void incrementImageDeleted() {
        Counter.builder("images.deleted")
                .description("Total number of images deleted")
                .tag("type", "image")
                .register(meterRegistry)
                .increment();
        log.debug("Image deletion metric incremented");
    }

    /**
     * Increment API error counter
     */
    public void incrementApiError(String errorType) {
        Counter.builder("api.errors")
                .description("Total number of API errors")
                .tag("type", "error")
                .tag("error_type", errorType)
                .register(meterRegistry)
                .increment();
        log.debug("API error metric incremented for type: {}", errorType);
    }

    /**
     * Increment authentication attempt counter
     */
    public void incrementAuthenticationAttempt() {
        Counter.builder("authentication.attempts")
                .description("Total number of authentication attempts")
                .tag("type", "authentication")
                .register(meterRegistry)
                .increment();
        log.debug("Authentication attempt metric incremented");
    }

    /**
     * Increment authentication failure counter
     */
    public void incrementAuthenticationFailure() {
        Counter.builder("authentication.failures")
                .description("Total number of failed authentication attempts")
                .tag("type", "authentication")
                .tag("status", "failed")
                .register(meterRegistry)
                .increment();
        log.debug("Authentication failure metric incremented");
    }

    /**
     * Track the execution time of an operation
     */
    public <T> T trackTime(String metricName, String description, Supplier<T> operation) {
        Timer timer = Timer.builder(metricName)
                .description(description)
                .register(meterRegistry);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = operation.get();
            sample.stop(timer);
            return result;
        } catch (Exception e) {
            sample.stop(timer);
            throw e;
        }
    }

    /**
     * Record a custom gauge value
     */
    public void recordGaugeValue(String metricName, String description, double value) {
        meterRegistry.gauge(metricName, value);
        log.debug("Gauge {} recorded with value: {}", metricName, value);
    }

    /**
     * Track database operation time
     */
    public <T> T trackDatabaseOperation(String operationType, Supplier<T> operation) {
        return trackTime(
                "database.operation.time",
                "Time taken for database operations",
                operation
        );
    }

    /**
     * Track API request time
     */
    public <T> T trackApiRequest(String endpoint, Supplier<T> operation) {
        Timer timer = Timer.builder("api.request.time")
                .description("Time taken for API requests")
                .tag("endpoint", endpoint)
                .register(meterRegistry);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = operation.get();
            sample.stop(timer);
            return result;
        } catch (Exception e) {
            sample.stop(timer);
            incrementApiError(e.getClass().getSimpleName());
            throw e;
        }
    }
}

