package hu.ponte.image_resizer.service;

import hu.ponte.image_resizer.dto.ImageQuality;
import hu.ponte.image_resizer.exception.customexcepions.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@Slf4j
public class ImageMagickResizer implements ImageResizer {

    private final IMOperation baseOperation;
    private final ConvertCmd convertCommand;

    @Value("${app.image.quality:92}")
    private int defaultQuality;

    @Value("${app.imagemagick.temp-dir:/tmp}")
    private String tempDir;

    @Autowired
    public ImageMagickResizer(IMOperation baseOperation, ConvertCmd convertCommand) {
        this.convertCommand = convertCommand;
        this.baseOperation = baseOperation;
        this.baseOperation.quality((double) defaultQuality);
        this.baseOperation.autoOrient();
        this.baseOperation.colorspace("sRGB");
        this.baseOperation.strip();
    }

    @Override
    public byte[] resize(byte[] imageData, int width, int height, String formatName) throws IOException {
        // Alapértelmezett beállításokkal hívjuk meg a részletes metódust
        return resize(imageData, width, height, true, ImageQuality.MEDIUM);
    }

    public byte[] resize(byte[] imageData, int targetWidth, int targetHeight,
                         boolean maintainAspectRatio, ImageQuality quality) throws IOException {

        File tempInput = null;
        File tempOutput = null;

        try {
            // Create temporary files
            tempInput = File.createTempFile("input_", ".tmp", new File(tempDir));
            tempOutput = File.createTempFile("output_", ".tmp", new File(tempDir));

            // Write input data to temp file
            Files.write(tempInput.toPath(), imageData);

            // Add input file
            baseOperation.addImage(tempInput.getPath());

            // Apply base operations (strip metadata, auto-orient)
            baseOperation.strip();
            baseOperation.autoOrient();

            // Set resize options
            if (maintainAspectRatio) {
                baseOperation.resize(targetWidth, targetHeight, ">");
            } else {
                baseOperation.resize(targetWidth, targetHeight, "!");
            }

            // Set quality based on the requested quality level
            int qualityValue = switch (quality) {
                case LOW -> 60;
                case MEDIUM -> 80;
                case HIGH -> 95;
                default -> defaultQuality;
            };
            baseOperation.quality((double) qualityValue);

            // Add output file
            baseOperation.addImage(tempOutput.getPath());

            // Execute the command
            convertCommand.run(baseOperation);

            // Read the output
            return Files.readAllBytes(tempOutput.toPath());

        } catch (IOException | InterruptedException | IM4JavaException e) {
            log.error("Error processing image with ImageMagick", e);
            throw new ImageProcessingException("Failed to process image with ImageMagick", e);
        } finally {
            // Clean up temp files
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
            if (tempOutput != null && tempOutput.exists()) {
                tempOutput.delete();
            }
        }
    }
}
