package FCJLaurels.awsrek.repository.blogging;

import FCJLaurels.awsrek.model.blog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends MongoRepository<blog, String> {
    List<blog> findByAuthor(String author);
    List<blog> findByTitleContainingIgnoreCase(String title);
}