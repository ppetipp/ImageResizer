package hu.ponte.image_resizer.exception.customexcepions;

public class ImageValidationException extends RuntimeException {

    private final String message;

    public ImageValidationException(String errorMessage) {
        super(errorMessage);
        this.message = errorMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
