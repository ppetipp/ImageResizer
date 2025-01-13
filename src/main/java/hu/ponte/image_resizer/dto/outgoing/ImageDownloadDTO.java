package hu.ponte.image_resizer.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDownloadDTO {
    private String fileName;
    private String contentType;
    private byte[] content;
}
