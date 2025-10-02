package FCJLaurels.awsrek.DTO.commentDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private String id;
    private String blogId;
    private String userId;
    private LocalDateTime creationDate;
    private String content;
}
