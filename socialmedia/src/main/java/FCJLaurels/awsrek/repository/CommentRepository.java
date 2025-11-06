package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<comment, String> {
    List<comment> findByBlogId(String blogId);
    List<comment> findByUserId(String userId);
    long countByBlogId(String blogId);
    long deleteByBlogId(String blogId);
}
