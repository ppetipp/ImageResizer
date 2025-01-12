package hu.ponte.image_resizer.exception.customexcepions;

import java.io.IOException;

public class ImageUploadFailedException extends RuntimeException {
    public ImageUploadFailedException(String errorMessage, IOException e) {
        super(errorMessage, e);
    }
}
