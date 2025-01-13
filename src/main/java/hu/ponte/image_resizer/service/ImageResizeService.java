package hu.ponte.image_resizer.service;

import hu.ponte.image_resizer.domain.Image;
import hu.ponte.image_resizer.domain.ProcessingStatus;
import hu.ponte.image_resizer.dto.incoming.ResizeRequest;
import hu.ponte.image_resizer.dto.outgoing.ImageDTO;
import hu.ponte.image_resizer.dto.outgoing.ImageDownloadDTO;
import hu.ponte.image_resizer.exception.customexcepions.ImageProcessingException;
import hu.ponte.image_resizer.exception.customexcepions.ImageValidationException;
import hu.ponte.image_resizer.exception.customexcepions.ResourceNotFoundException;
import hu.ponte.image_resizer.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@Transactional
public class ImageResizeService {
    private final ImageResizer imageResizer;
    private final ImageRepository imageRepository;

    @Value("${app.image.max-size:2097152}")
    private long maxImageSize;

    @Autowired
    public ImageResizeService(@Qualifier("graphics2DResizer") ImageResizer imageResizer, ImageRepository imageRepository) {
        this.imageResizer = imageResizer;
        this.imageRepository = imageRepository;
    }

    public ImageDTO uploadImage(MultipartFile file) {
        validateImage(file);
        if (file.getContentType() == null) {
            throw new ImageValidationException("File type is null");
        }

        try {
            // Eredeti fájl tartalmának beolvasása
            byte[] originalContent = file.getBytes();

            Image image = Image.builder()
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .status(ProcessingStatus.PENDING)
                    .fileSize(file.getSize())
                    .originalContent(originalContent)  // Eredeti tartalom mentése
                    .build();

            // Képméret információk kinyerése
            if (file.getContentType().startsWith("image/")) {
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                image.setOriginalWidth(bufferedImage.getWidth());
                image.setOriginalHeight(bufferedImage.getHeight());
            }

            image = imageRepository.save(image);
            return ImageDTO.fromEntity(image);

        } catch (IOException e) {
            throw new ImageProcessingException("Failed to process uploaded image", e);
        } catch (NullPointerException e) {
            throw new ImageProcessingException("Failed to process uploaded image", e);
        }
    }

    public ImageDownloadDTO getOriginalImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        return ImageDownloadDTO.builder()
                .fileName(image.getOriginalFileName())
                .contentType(image.getContentType())
                .content(image.getOriginalContent())
                .build();
    }

    public ImageDownloadDTO getProcessedImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (image.getProcessedContent() == null) {
            throw new ImageProcessingException("Image has not been processed yet");
        }

        return ImageDownloadDTO.builder()
                .fileName("resized_" + image.getOriginalFileName())
                .contentType(image.getContentType())
                .content(image.getProcessedContent())
                .build();
    }

    public ImageDTO resizeImage(Long id, ResizeRequest request) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        try {
            image.setStatus(ProcessingStatus.PROCESSING);
            image = imageRepository.save(image);

            Image finalImage = image;
            CompletableFuture.runAsync(() -> processImageResize(finalImage, request))
                    .exceptionally(throwable -> {
                        handleResizeError(finalImage, throwable);
                        return null;
                    });

            return ImageDTO.fromEntity(finalImage);

        } catch (Exception e) {
            throw new ImageProcessingException("Failed to initiate image resize", e);
        }
    }

    private void processImageResize(Image image, ResizeRequest request) {
        try {
            byte[] resizedImage = imageResizer.resize(
                    image.getOriginalContent(),
                    request.getWidth(),
                    request.getHeight(),
                    image.getContentType().split("/")[1]
            );

            image.setProcessedContent(resizedImage);
            image.setNewWidth(request.getWidth());
            image.setNewHeight(request.getHeight());
            image.setStatus(ProcessingStatus.COMPLETED);
            image.setProcessedAt(LocalDateTime.now());

            imageRepository.save(image);

        } catch (Exception e) {
            handleResizeError(image, e);
        }
    }

    private void handleResizeError(Image image, Throwable error) {
        log.error("Error processing image: {}", error.getMessage(), error);

        image.setStatus(ProcessingStatus.FAILED);
        image.setErrorMessage(error.getMessage());
        imageRepository.save(image);
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ImageValidationException("File is empty");
        }

        if (file.getSize() > maxImageSize) {
            throw new ImageValidationException(
                    String.format("File size exceeds maximum allowed size of %d bytes", maxImageSize));
        }

        String contentType = file.getContentType();
        if (!isAllowedContentType(contentType)) {
            throw new ImageValidationException("Unsupported file type: " + contentType);
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return Arrays.asList(
                "image/jpg",
                "image/jpeg",
                "image/png",
                "application/pdf"
        ).contains(contentType);
    }
}
