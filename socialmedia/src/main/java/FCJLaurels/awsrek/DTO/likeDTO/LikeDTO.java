package FCJLaurels.awsrek.DTO.likeDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeDTO {
    private String id;
    private String blogId;
    private String userId;
    private LocalDateTime creationDate;
}
