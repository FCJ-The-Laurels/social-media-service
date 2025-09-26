package FCJLaurels.awsrek.DTO.commentDTO;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCreationDTO {
    @NotBlank(message = "Blog ID is required")
    private String blogId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Content is required")
    private String content;
}
