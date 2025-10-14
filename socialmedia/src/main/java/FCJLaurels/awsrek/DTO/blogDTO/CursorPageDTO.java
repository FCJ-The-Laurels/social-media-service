package FCJLaurels.awsrek.DTO.blogDTO;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageDTO<T> {
    private List<T> data;
    private String nextCursor;
}
