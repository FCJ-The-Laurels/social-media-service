package FCJLaurels.awsrek.DTO.blogDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogCursorResponse {
    private List<BlogDisplay> content;
    private String nextCursor;
    private boolean hasMore;
    private int size;
}

