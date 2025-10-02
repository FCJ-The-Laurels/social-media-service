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
@Document(collation = "image_upload")
public class image {
    @Id
    @Builder.Default
    private String id= UUID.randomUUID().toString();
    private String name;
    private String url;
    private String type;
    private LocalDateTime creationDate=LocalDateTime.now();
}