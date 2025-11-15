package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.graphics.TextRenderer;

public class Button extends AbstractButton {
    private Image normalImage;
    private Image hoverImage;
    private static final double FONT_SCALE_RATIO = 0.5;
    private static final String DEFAULT_FONT_FAMILY = "Anxel";

    public Button(double x, double y, double width, double height,
                  Image normalImage, Image hoverImage, String text) {
        super(x, y, width, height, text);
        this.normalImage = normalImage;
        this.hoverImage = (hoverImage != null) ? hoverImage : normalImage;

        double dynamicFontSize = height * FONT_SCALE_RATIO;

        this.font = new Font(DEFAULT_FONT_FAMILY, dynamicFontSize);
        this.textColor = Color.WHITE;
    }

    public Button(double x, double y, Image normalImage, Image hoverImage, String text) {
        this(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT,
                normalImage, hoverImage, text);
    }

    @Override
    public void renderDefault(GraphicsContext gc) {
        if (gc == null) return;
        Image imgToDraw = isHovered ? hoverImage : normalImage;

        TextAlignment previousAlignment = gc.getTextAlign();
        try {
            gc.drawImage(imgToDraw, x, y, width, height);

            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(
                    gc,
                    text,
                    x + width / 2 + 2,
                    y + height / 2 + 9,
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
