package hu.ponte.image_resizer.exception.customexcepions;

public class ImageProcessingException extends RuntimeException {
    public ImageProcessingException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }
}
