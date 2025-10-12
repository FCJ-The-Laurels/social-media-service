package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.image;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ImageRepository extends ReactiveMongoRepository<image, String> {
    Flux<image> findByName(String name);
    Flux<image> findByType(String type);
}
