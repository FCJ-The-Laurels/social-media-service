package FCJLaurels.awsrek.DTO.imageDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageEditDTO {
    private String name;
    private String url;
    private String type;
}
