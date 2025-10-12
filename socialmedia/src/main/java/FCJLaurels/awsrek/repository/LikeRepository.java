package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.like;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LikeRepository extends ReactiveMongoRepository<like, String> {
    Flux<like> findByUserId(String userId);
    Flux<like> findByBlogId(String blogId);
    Mono<Long> countByBlogId(String blogId);
    Mono<Long> countByUserId(String userId);
    Mono<like> findByUserIdAndBlogId(String userId, String blogId);
    Mono<Boolean> existsByUserIdAndBlogId(String userId, String blogId);
    Mono<Void> deleteByUserIdAndBlogId(String userId, String blogId);
    Mono<Long> deleteByBlogId(String blogId);
}
