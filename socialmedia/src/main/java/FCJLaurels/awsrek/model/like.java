package FCJLaurels.awsrek.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "like")
public class like {
    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String blogId;    // reference to Blog
    private String userId;    // reference to User (if you have one)
    @Builder.Default
    private LocalDateTime creationDate=LocalDateTime.now();
}
