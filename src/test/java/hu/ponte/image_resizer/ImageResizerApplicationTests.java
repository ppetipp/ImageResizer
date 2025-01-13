package hu.ponte.image_resizer;

import hu.ponte.image_resizer.controller.ImageController;
import hu.ponte.image_resizer.dto.incoming.ResizeRequest;
import hu.ponte.image_resizer.dto.outgoing.ImageDTO;
import hu.ponte.image_resizer.dto.outgoing.ImageDownloadDTO;
import hu.ponte.image_resizer.service.ImageResizeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageControllerTest {

    @Mock
    private ImageResizeService imageResizeService;

    @InjectMocks
    private ImageController imageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadSingleImage_shouldReturnOkStatus() {
        MultipartFile file = mock(MultipartFile.class);
        ImageDTO imageDTO = new ImageDTO();
        when(imageResizeService.uploadImage(any(MultipartFile.class))).thenReturn(imageDTO);

        ResponseEntity<ImageDTO> response = imageController.uploadSingleImage(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(imageDTO, response.getBody());
    }

    @Test
    void uploadMultipleImages_shouldReturnOkStatus() {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2);
        ImageDTO imageDTO1 = new ImageDTO();
        ImageDTO imageDTO2 = new ImageDTO();
        when(imageResizeService.uploadImage(any(MultipartFile.class))).thenReturn(imageDTO1, imageDTO2);

        ResponseEntity<List<ImageDTO>> response = imageController.uploadMultipleImages(files);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(imageDTO1, imageDTO2), response.getBody());
    }

    @Test
    void resizeImage_shouldReturnOkStatus() {
        Long id = 1L;
        ResizeRequest resizeRequest = new ResizeRequest();
        ImageDTO imageDTO = new ImageDTO();
        when(imageResizeService.resizeImage(id, resizeRequest)).thenReturn(imageDTO);

        ResponseEntity<ImageDTO> response = imageController.resizeImage(id, resizeRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(imageDTO, response.getBody());
    }

    @Test
    void downloadOriginalImage_shouldReturnOkStatus() {
        Long id = 1L;
        ImageDownloadDTO downloadDTO = new ImageDownloadDTO();
        downloadDTO.setContentType("image/jpeg");
        downloadDTO.setFileName("original.jpg");
        downloadDTO.setContent(new byte[0]);
        when(imageResizeService.getOriginalImage(id)).thenReturn(downloadDTO);

        ResponseEntity<byte[]> response = imageController.downloadOriginalImage(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(downloadDTO.getContent(), response.getBody());
    }

    @Test
    void downloadProcessedImage_shouldReturnOkStatus() {
        Long id = 1L;
        ImageDownloadDTO downloadDTO = new ImageDownloadDTO();
        downloadDTO.setContentType("image/jpeg");
        downloadDTO.setFileName("processed.jpg");
        downloadDTO.setContent(new byte[0]);
        when(imageResizeService.getProcessedImage(id)).thenReturn(downloadDTO);

        ResponseEntity<byte[]> response = imageController.downloadProcessedImage(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(downloadDTO.getContent(), response.getBody());
    }
}