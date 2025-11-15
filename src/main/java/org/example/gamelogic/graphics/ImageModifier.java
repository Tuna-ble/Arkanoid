package org.example.gamelogic.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public final class ImageModifier {
    private ImageModifier() {

    }

    /**
     * Creates a tinted copy of the given grayscale image.
     */
    public static Image tintImage(Image src, Color tint) {
        int w = (int) src.getWidth();
        int h = (int) src.getHeight();
        WritableImage tinted = new WritableImage(w, h);
        PixelReader pr = src.getPixelReader();
        PixelWriter pw = tinted.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = pr.getColor(x, y);
                if (c.getOpacity() > 0.0) {
                    // multiply tint hue with brightness of grayscale pixel
                    double brightness = c.getBrightness();
                    Color newColor = tint.deriveColor(0, 1, brightness, c.getOpacity());
                    pw.setColor(x, y, newColor);
                } else {
                    pw.setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
        return tinted;
    }
}
