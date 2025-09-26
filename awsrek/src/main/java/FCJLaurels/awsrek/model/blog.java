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
    @Builder.Default
    private LocalDateTime creationDate=LocalDateTime.now();
    private String imageUrl;

    @Builder.Default
    private long likeCount = 0;

    @Builder.Default
    private long commentCount = 0;
}
