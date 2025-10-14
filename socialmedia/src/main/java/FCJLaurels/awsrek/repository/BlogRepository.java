package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface BlogRepository extends ReactiveMongoRepository<blog, String> {
    Flux<blog> findAllByOrderByCreationDateDesc(Pageable pageable);
    Flux<blog> findByAuthor(String author);
    Flux<blog> findByTitleContainingIgnoreCase(String title);

    @Tailable
    @Query("{}")
    Flux<blog> streamAllBy();

    // Cursor-based pagination methods for infinite scrolling
    Flux<blog> findByCreationDateLessThanOrderByCreationDateDesc(LocalDateTime cursor, Pageable pageable);

    Flux<blog> findByCreationDateGreaterThanOrderByCreationDateAsc(LocalDateTime cursor, Pageable pageable);

    @Query("{ 'creationDate': { $lt: ?0 }, 'id': { $lt: ?1 } }")
    Flux<blog> findByCreationDateAndIdLessThan(LocalDateTime creationDate, String id, Pageable pageable);

    Mono<Long> countByCreationDateLessThan(LocalDateTime cursor);
}