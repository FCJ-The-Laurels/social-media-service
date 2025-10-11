package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BlogRepository extends ReactiveMongoRepository<blog, String> {
    Flux<blog> findAllByOrderByCreationDateDesc(Pageable pageable);
    Flux<blog> findByAuthor(String author);
    Flux<blog> findByTitleContainingIgnoreCase(String title);
    @Tailable
    @Query("{}")
    Flux<blog> streamAllBy();
}