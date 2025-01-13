package hu.ponte.image_resizer.domain;

public enum ProcessingStatus {
    PENDING,    // Initial state when image is uploaded
    PROCESSING, // Currently being resized
    COMPLETED,  // Successfully processed
    FAILED      // Error occurred during processing
}
