package FCJLaurels.awsrek.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collation = "Likes")
public class like {
    @Builder.Default
    private LocalDateTime creationDate=LocalDateTime.now();
}
