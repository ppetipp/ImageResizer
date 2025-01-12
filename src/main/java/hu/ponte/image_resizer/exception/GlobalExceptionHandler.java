package hu.ponte.image_resizer.exception;

import com.fasterxml.jackson.core.JsonParseException;
import hu.ponte.image_resizer.exception.customexcepions.ImageProcessingException;
import hu.ponte.image_resizer.exception.customexcepions.ImageUploadFailedException;
import hu.ponte.image_resizer.exception.customexcepions.ImageValidationException;
import hu.ponte.image_resizer.exception.customexcepions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.error("A validation error occurred: ", ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return new ResponseEntity<>(processFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
    }

    private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
        ValidationError validationError = new ValidationError();

        for (FieldError fieldError : fieldErrors) {
            validationError.addFieldError(fieldError.getField(), messageSource.getMessage(fieldError, Locale.getDefault()));
        }

        return validationError;
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException ex) {
        logger.error("Request JSON could no be parsed: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError(ERROR_CODE.JSON_PARSE_ERROR.name(), "The request could not be parsed as a valid JSON.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError(ERROR_CODE.ILLEGAL_ARGUMENT_ERROR.name(), "An illegal argument has been passed to the method.", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException ex) {
        logger.error("IllegalStateException occurred: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError(ERROR_CODE.ILLEGAL_STATE_EXCEPTION.name(), "An illegal state has occurred.", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex) {
        logger.error("No such element: ", ex);
        HttpStatus status = HttpStatus.NOT_FOUND;

        ApiError body = new ApiError(ERROR_CODE.NO_SUCH_ELEMENT.name(), "There is no such element", ex.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> defaultErrorHandler(Throwable t) {

        logger.error("An unexpected error occurred: ", t);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (t instanceof Error) {
            System.exit(1);
        }
        ApiError body = new ApiError(ERROR_CODE.UNCLASSIFIED_ERROR.name(), "Oh, snap! Something really unexpected occurred.", t.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> roomNotFoundExceptionHandler(HttpMessageNotReadableException ex) {
        logger.error("Room not found: ", ex);
        ApiError body = new ApiError(ERROR_CODE.ILLEGAL_ARGUMENT_ERROR.name(), "Error: input data missing.", "");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImageUploadFailedException.class)
    public ResponseEntity<ApiError> ImageUploadFailedExceptionHandler(ImageUploadFailedException e) {
        logger.error("Image upload failed: ", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiError body = new ApiError(ERROR_CODE.IMAGE_UPLOAD_FAILED.name(), "Error: image upload failed.", e.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> ResourceNotFoundExceptionHandler(ResourceNotFoundException e) {
        logger.error("Resource not found: ", e);

        ApiError body = new ApiError(ERROR_CODE.IMAGE_UPLOAD_FAILED.name(), "Error: resource not found.", e.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImageValidationException.class)
    public ResponseEntity<ApiError> ImageValidationExceptionHandler(ImageValidationException e) {
        logger.error("Image validation failed: ", e);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError(ERROR_CODE.IMAGE_VALIDATION_ERROR.name(), "Image validation failed.", e.getMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<ApiError> ImageProcessingExceptionHandler(ImageProcessingException e) {
        logger.error("Image processing failed: ", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiError body = new ApiError(ERROR_CODE.IMAGE_PROCESSING_ERROR.name(), "An error occurred during image processing.", e.getMessage());

        return new ResponseEntity<>(body, status);
    }
}

