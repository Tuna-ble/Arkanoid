package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.graphics.TextRenderer;

public class TextLabel extends AbstractButton {

    public TextLabel(double x, double y, double width, double height, String text) {
        super(x, y, width, height, text);

        this.font = AssetManager.getInstance().getFont("Anxel", 20);
        this.textColor = Color.WHITE;
    }

    @Override
    public void handleInput(I_InputProvider input) {
    }

    @Override
    public void renderDefault(GraphicsContext gc) {
        if (gc == null || text == null) return;

        TextAlignment previousAlignment = gc.getTextAlign();
        try {
            gc.setTextAlign(TextAlignment.CENTER);

            double textY = y + height / 2 + (font.getSize() * 0.35);

            TextRenderer.drawOutlinedText(
                    gc,
                    text,
                    x + width / 2,
                    textY,
                    font,
                    textColor,
                    Color.color(0, 0, 0, 0.85),
                    1.5,
                    null
            );
        } finally {
            gc.setTextAlign(previousAlignment);
        }
    }
}