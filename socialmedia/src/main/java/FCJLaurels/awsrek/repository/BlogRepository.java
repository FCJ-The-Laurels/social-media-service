package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends MongoRepository<blog, String> {
    List<blog> findAllByOrderByCreationDateDesc(Pageable pageable);

    // Fixed to accept UUID instead of String
    List<blog> findByAuthor(UUID author);

    List<blog> findByTitleContainingIgnoreCase(String title);

    @Tailable
    @Query("{}")
    List<blog> streamAllBy();

    // Cursor-based pagination methods for infinite scrolling
    List<blog> findByCreationDateLessThanOrderByCreationDateDesc(LocalDateTime cursor, Pageable pageable);

    List<blog> findByCreationDateGreaterThanOrderByCreationDateAsc(LocalDateTime cursor, Pageable pageable);

    @Query("{ 'creationDate': { $lt: ?0 }, 'id': { $lt: ?1 } }")
    List<blog> findByCreationDateAndIdLessThan(LocalDateTime creationDate, String id, Pageable pageable);

    long countByCreationDateLessThan(LocalDateTime cursor);
    // keep count() from MongoRepository for total count if needed
}