package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends MongoRepository<like, String> {
    List<like> findByUserId(String userId);
    List<like> findByBlogId(String blogId);
    long countByBlogId(String blogId);
}

