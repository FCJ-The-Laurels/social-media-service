package FCJLaurels.awsrek.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom health indicator for MongoDB connection
 */
@Component
@RequiredArgsConstructor
public class MongoHealthIndicator implements HealthIndicator {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Health health() {
        try {
            // Try to execute a simple command to check MongoDB connectivity
            Mono<String> dbName = mongoTemplate.getMongoDatabase()
                    .map(db -> db.getName());

            String databaseName = dbName.block();

            if (databaseName != null) {
                return Health.up()
                        .withDetail("database", databaseName)
                        .withDetail("status", "Connected")
                        .build();
            } else {
                return Health.down()
                        .withDetail("error", "Unable to retrieve database name")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}

