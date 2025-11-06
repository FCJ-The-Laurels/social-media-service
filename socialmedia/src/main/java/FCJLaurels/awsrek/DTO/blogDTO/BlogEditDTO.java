package FCJLaurels.awsrek.DTO.blogDTO;

import lombok.*;

import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogEditDTO {
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    private String content;

    private String imageUrl;
}
