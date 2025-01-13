package hu.ponte.image_resizer.service;

import java.io.IOException;

public interface ImageResizer {
    byte[] resize(byte[] imageData, int width, int height, String formatName) throws IOException;
}
