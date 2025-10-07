package FCJLaurels.awsrek.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoConnectionTest {

    @Bean
    CommandLineRunner testMongoConnection(MongoTemplate mongoTemplate) {
        return args -> {
            try {
                System.out.println("Database Name: " + mongoTemplate.getDb().getName());
                System.out.println("Collections: " + mongoTemplate.getDb().listCollectionNames().into(new java.util.ArrayList<>()));
            } catch (Exception e) {
                System.err.println("MongoDB connection failed: " + e.getMessage());
            }
        };
    }
}

