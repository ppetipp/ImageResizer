package hu.ponte.image_resizer.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "The id of the image", example = "1")
    private Long id;

    @Column(name = "original_file_name", nullable = false)
    @Schema(description = "The original file name of the image", example = "image.jpg")
    private String originalFileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingStatus status;

    @Column
    private String errorMessage;

    @Column(name = "uploaded_at", nullable = false)
    @Schema(description = "The upload date of the image", example = "2021-09-01T12:00:00")
    private LocalDateTime uploadedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column
    @Schema(description = "The width of the original image", example = "1920")
    private Integer originalWidth;

    @Column
    @Schema(description = "The height of the original image", example = "1080")
    private Integer originalHeight;

    @Column
    private Integer newWidth;

    @Column
    private Integer newHeight;

    @Column
    @Schema(description = "The size of the image in bytes", example = "1000000")
    private Long fileSize;

    @Lob
    @Column(name = "original_content", nullable = false)
    private byte[] originalContent;

    @Lob
    @Column(name = "processed_content")
    private byte[] processedContent;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
