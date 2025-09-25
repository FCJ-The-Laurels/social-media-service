package FCJLaurels.awsrek.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collation = "Blog")
public class blog {
    @Id
    @Builder.Default
    private String id= UUID.randomUUID().toString();
    private String title;
    private String content;
    private String author;
    private LocalDateTime creationDate;
    private String imageUrl;
    private int likes;
    private comment comment;
}
