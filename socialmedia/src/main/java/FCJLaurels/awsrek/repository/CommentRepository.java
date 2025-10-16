package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.comment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<comment, String> {
    Flux<comment> findByBlogId(String blogId);
    Flux<comment> findByUserId(String userId);
    Mono<Long> countByBlogId(String blogId);
    Mono<Long> deleteByBlogId(String blogId);
}
