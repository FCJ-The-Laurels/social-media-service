package FCJLaurels.awsrek.DTO.blogDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogDTO {
    private String id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime creationDate;
    private String imageUrl;
    private long likeCount;
    private long commentCount;


}
