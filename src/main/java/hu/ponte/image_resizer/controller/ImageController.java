package hu.ponte.image_resizer.controller;

import hu.ponte.image_resizer.dto.incoming.ResizeRequest;
import hu.ponte.image_resizer.dto.outgoing.ImageDTO;
import hu.ponte.image_resizer.dto.outgoing.ImageDownloadDTO;
import hu.ponte.image_resizer.service.ImageResizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@Tag(name = "Image Operations", description = "API endpoints for image processing")
@Slf4j
public class ImageController {
    private final ImageResizeService imageResizeService;

    @Autowired
    public ImageController(ImageResizeService imageResizeService) {
        this.imageResizeService = imageResizeService;
    }

    @PostMapping("/upload/single")
    @Operation(summary = "Upload single image")
    public ResponseEntity<ImageDTO> uploadSingleImage(
            @RequestParam("file") MultipartFile file) {
        log.info("Uploading single file: {}", file.getOriginalFilename());

        ImageDTO uploadedImage = imageResizeService.uploadImage(file);
        return new ResponseEntity<>(uploadedImage, HttpStatus.OK);
    }

    @PostMapping("/upload/multiple")
    @Operation(summary = "Upload multiple images")
    public ResponseEntity<List<ImageDTO>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files) {
        log.info("Uploading {} files", files.size());

        List<ImageDTO> uploadedImages = files.stream()
                .map(imageResizeService::uploadImage)
                .toList();

        return ResponseEntity.ok(uploadedImages);
    }

    @PostMapping("/{id}/resize")
    @Operation(summary = "Resize specific image")
    @ApiResponse(responseCode = "200", description = "Image resized successfully")
    @ApiResponse(responseCode = "404", description = "Image not found")
    public ResponseEntity<ImageDTO> resizeImage(
            @PathVariable Long id,
            @Valid @RequestBody ResizeRequest resizeRequest) {
        log.info("Resizing image {} to {}x{}",
                id, resizeRequest.getWidth(), resizeRequest.getHeight());

        ImageDTO resizedImage = imageResizeService.resizeImage(id, resizeRequest);
        return ResponseEntity.ok(resizedImage);
    }

    @GetMapping("/{id}/original")
    @Operation(summary = "Download original image")
    public ResponseEntity<byte[]> downloadOriginalImage(@PathVariable Long id) {
        ImageDownloadDTO downloadDTO = imageResizeService.getOriginalImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadDTO.getFileName() + "\"")
                .body(downloadDTO.getContent());
    }

    @GetMapping("/{id}/processed")
    @Operation(summary = "Download processed image")
    public ResponseEntity<byte[]> downloadProcessedImage(@PathVariable Long id) {
        ImageDownloadDTO downloadDTO = imageResizeService.getProcessedImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadDTO.getFileName() + "\"")
                .body(downloadDTO.getContent());
    }
}
