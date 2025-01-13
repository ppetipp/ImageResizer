package hu.ponte.image_resizer.dto.outgoing;

import hu.ponte.image_resizer.domain.Image;
import hu.ponte.image_resizer.domain.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {

    private Long id;
    private String originalName;
    private String contentType;
    private ProcessingStatus status;
    private String errorMessage;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    private Integer originalWidth;
    private Integer originalHeight;
    private Integer newWidth;
    private Integer newHeight;
    private Long fileSize;

    // Static factory method to convert Entity to DTO
    public static ImageDTO fromEntity(Image entity) {
        return ImageDTO.builder()
                .id(entity.getId())
                .originalName(entity.getOriginalFileName())
                .contentType(entity.getContentType())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .uploadedAt(entity.getUploadedAt())
                .processedAt(entity.getProcessedAt())
                .originalWidth(entity.getOriginalWidth())
                .originalHeight(entity.getOriginalHeight())
                .newWidth(entity.getNewWidth())
                .newHeight(entity.getNewHeight())
                .fileSize(entity.getFileSize())
                .build();
    }
}
