package FCJLaurels.awsrek.DTO.blogDTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogPageResponse {
    private List<BlogDisplay> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private String nextCursor;
    private String previousCursor;
}

