package FCJLaurels.awsrek.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

/**
 * Configuration class for Prometheus metrics
 * Provides custom metrics for monitoring application performance
 */
@Configuration
public class PrometheusMetricsConfig {

    /**
     * Customizes the meter registry with application-specific tags
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", "socialmedia-api")
                .commonTags("environment", "production");
    }

    /**
     * Counter for blog post creation
     */
    @Bean
    public Counter blogCreationCounter(MeterRegistry registry) {
        return Counter.builder("blog.posts.created")
                .description("Total number of blog posts created")
                .tag("type", "blog")
                .register(registry);
    }

    /**
     * Counter for blog post deletion
     */
    @Bean
    public Counter blogDeletionCounter(MeterRegistry registry) {
        return Counter.builder("blog.posts.deleted")
                .description("Total number of blog posts deleted")
                .tag("type", "blog")
                .register(registry);
    }

    /**
     * Counter for comments created
     */
    @Bean
    public Counter commentCreationCounter(MeterRegistry registry) {
        return Counter.builder("comments.created")
                .description("Total number of comments created")
                .tag("type", "comment")
                .register(registry);
    }

    /**
     * Counter for likes added
     */
    @Bean
    public Counter likeCounter(MeterRegistry registry) {
        return Counter.builder("likes.added")
                .description("Total number of likes added")
                .tag("type", "like")
                .register(registry);
    }

    /**
     * Counter for image uploads
     */
    @Bean
    public Counter imageUploadCounter(MeterRegistry registry) {
        return Counter.builder("images.uploaded")
                .description("Total number of images uploaded")
                .tag("type", "image")
                .register(registry);
    }

    /**
     * Timer for blog retrieval operations
     */
    @Bean
    public Timer blogRetrievalTimer(MeterRegistry registry) {
        return Timer.builder("blog.retrieval.time")
                .description("Time taken to retrieve blog posts")
                .tag("operation", "retrieval")
                .register(registry);
    }

    /**
     * Timer for database operations
     */
    @Bean
    public Timer databaseOperationTimer(MeterRegistry registry) {
        return Timer.builder("database.operation.time")
                .description("Time taken for database operations")
                .tag("operation", "database")
                .register(registry);
    }

    /**
     * Counter for API errors
     */
    @Bean
    public Counter apiErrorCounter(MeterRegistry registry) {
        return Counter.builder("api.errors")
                .description("Total number of API errors")
                .tag("type", "error")
                .register(registry);
    }

    /**
     * Counter for authentication attempts
     */
    @Bean
    public Counter authenticationCounter(MeterRegistry registry) {
        return Counter.builder("authentication.attempts")
                .description("Total number of authentication attempts")
                .tag("type", "authentication")
                .register(registry);
    }

    /**
     * Counter for authentication failures
     */
    @Bean
    public Counter authenticationFailureCounter(MeterRegistry registry) {
        return Counter.builder("authentication.failures")
                .description("Total number of failed authentication attempts")
                .tag("type", "authentication")
                .tag("status", "failed")
                .register(registry);
    }
}

