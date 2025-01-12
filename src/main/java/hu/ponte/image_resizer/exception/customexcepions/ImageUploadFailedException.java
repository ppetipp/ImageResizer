package hu.ponte.image_resizer.exception.customexcepions;

import java.io.IOException;

public class ImageUploadFailedException extends RuntimeException {

    private final String message;

    public ImageUploadFailedException(String errorMessage, IOException e) {
        super(errorMessage, e);
        this.message = errorMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
