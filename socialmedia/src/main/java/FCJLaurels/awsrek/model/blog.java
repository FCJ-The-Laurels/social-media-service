package FCJLaurels.awsrek.model;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "blog")
public class blog {
    @Id
    @Builder.Default
    private String id= UUID.randomUUID().toString();
    private String title;
    private String content;
    private UUID author;
    @Builder.Default
    private LocalDateTime creationDate=LocalDateTime.now();
    private String imageUrl;
}
