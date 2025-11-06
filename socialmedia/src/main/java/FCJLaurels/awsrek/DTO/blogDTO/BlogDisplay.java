package FCJLaurels.awsrek.DTO.blogDTO;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class BlogDisplay {
    private String authorName;
    private String authorAvatar;
    private String title;
    private String imageURL;
    private String content;
    private LocalDateTime creationDate;
}
