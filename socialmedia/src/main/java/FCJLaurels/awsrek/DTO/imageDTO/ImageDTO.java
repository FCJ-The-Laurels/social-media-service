package FCJLaurels.awsrek.DTO.imageDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDTO {
    private String id;
    private String name;
    private String url;
    private String type;
    private LocalDateTime creationDate;
}
