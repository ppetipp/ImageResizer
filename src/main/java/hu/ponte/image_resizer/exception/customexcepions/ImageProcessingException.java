package hu.ponte.image_resizer.exception.customexcepions;

import java.io.IOException;

public class ImageProcessingException extends RuntimeException {

    private final String message;

    public ImageProcessingException(String errorMessage, IOException e) {
        super(errorMessage, e);
        this.message = errorMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
