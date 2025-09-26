package FCJLaurels.awsrek.DTO.likeDTO;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeCreationDTO {
    @NotBlank(message = "Blog ID is required")
    private String blogId;

    @NotBlank(message = "User ID is required")
    private String userId;
}
