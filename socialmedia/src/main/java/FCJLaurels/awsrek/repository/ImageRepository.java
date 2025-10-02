package FCJLaurels.awsrek.repository;

import FCJLaurels.awsrek.model.image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends MongoRepository<image, String> {
    List<image> findByName(String name);
    List<image> findByType(String type);
}

