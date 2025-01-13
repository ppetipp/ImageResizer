package hu.ponte.image_resizer.service;

import hu.ponte.image_resizer.exception.customexcepions.ImageProcessingException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class Graphics2DResizer implements ImageResizer {
    @Override
    public byte[] resize(byte[] imageData, int width, int height, String formatName) throws IOException {
        try {
            // Kép betöltése a byte tömbből
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            BufferedImage originalImage = ImageIO.read(bais);

            // Új BufferedImage létrehozása a kívánt méretben
            BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());

            // Átméretezés
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();

            // Visszaalakítás byte tömbbé
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, formatName, baos);

            return baos.toByteArray();
        } catch (Exception e) {
            throw new ImageProcessingException("Hiba történt a kép átméretezése során: " + e.getMessage());
        }
    }
}
