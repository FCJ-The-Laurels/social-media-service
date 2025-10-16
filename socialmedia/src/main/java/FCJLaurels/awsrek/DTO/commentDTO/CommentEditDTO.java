package FCJLaurels.awsrek.DTO.commentDTO;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEditDTO {
    @NotBlank(message = "Content cannot be empty")
    private String content;
}
