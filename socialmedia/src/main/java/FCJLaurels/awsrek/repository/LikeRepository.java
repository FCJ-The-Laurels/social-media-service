package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<like, String> {
    List<like> findByUserId(String userId);
    List<like> findByBlogId(String blogId);
    long countByBlogId(String blogId);
    long countByUserId(String userId);
    Optional<like> findByUserIdAndBlogId(String userId, String blogId);
    boolean existsByUserIdAndBlogId(String userId, String blogId);
    void deleteByUserIdAndBlogId(String userId, String blogId);
    long deleteByBlogId(String blogId);
}
