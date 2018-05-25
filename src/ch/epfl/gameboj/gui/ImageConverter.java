package ch.epfl.gameboj.gui;

import ch.epfl.gameboj.component.lcd.LcdImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Convertisseur d'image Game Boy en image JavaFX
 * @author Riand Andre
 * @author Michel François
 */
public class ImageConverter {
    LcdImage li;
    private static final int[] COLOR_MAP = new int[] {
            0xFF_FF_FF_FF, 0xFF_D3_D3_D3, 0xFF_A9_A9_A9, 0xFF_00_00_00
    };
    
    private ImageConverter() {};

    /**
     * Convertit une image de Game Boy en image JavaFX
     * @param li : image à convertir
     * @return une image de type javaFX
     */
    public static Image convert(LcdImage li) {
        final WritableImage wImage = new WritableImage(li.width(), li.height());
        final PixelWriter pixelWriter = wImage.getPixelWriter();
        for (int y = 0; y < li.height(); ++y)
            for (int x = 0; x < li.width(); ++x)
                pixelWriter.setArgb(x, y, COLOR_MAP[li.get(x, y)]);
        return wImage;
    }
}
