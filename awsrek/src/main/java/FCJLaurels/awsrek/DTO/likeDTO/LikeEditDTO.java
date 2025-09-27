package FCJLaurels.awsrek.DTO.likeDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEditDTO {
    // For likes, typically you wouldn't edit much after creation
    // But we'll include the basic fields that might be editable
    private String blogId;
    private String userId;
}
