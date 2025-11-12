package org.example.gamelogic.graphics.Buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.TextRenderer;

public class Button extends AbstractButton {
    private Image normalImage;
    private Image hoverImage;

    public Button(double x, double y, double width, double height,
                  Image normalImage, Image hoverImage, String text) {
        super(x, y, width, height, text);
        this.normalImage = normalImage;
        this.hoverImage = (hoverImage != null) ? hoverImage : normalImage;
    }

    public Button(double x, double y, Image normalImage, Image hoverImage, String text) {
        this(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT,
                normalImage, hoverImage, text);
    }

    @Override
    public void render(GraphicsContext gc) {
        if (gc == null) return;
        Image imgToDraw = isHovered ? hoverImage : normalImage;

        TextAlignment previousAlignment = gc.getTextAlign();
        try {
            gc.drawImage(imgToDraw, x, y, width, height);

            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(
                    gc,
                    text,
                    x + width / 2,
                    y + height / 2 + 8,
                    font,
                    textColor,
                    Color.color(0, 0, 0, 0.85),
                    1.5,
                    new DropShadow(6, Color.color(0, 0, 0, 0.6))
            );
        } finally {
            gc.setTextAlign(previousAlignment);
        }
    }
}
