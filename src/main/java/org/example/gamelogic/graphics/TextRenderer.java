package org.example.gamelogic.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public final class TextRenderer {
    private TextRenderer() {}

    public static void drawOutlinedText(GraphicsContext gc,
                                        String text,
                                        double x,
                                        double y,
                                        Font font,
                                        Paint fillPaint,
                                        Paint strokePaint,
                                        double strokeWidth,
                                        DropShadow shadow) {
        Font previousFont = gc.getFont();
        Paint previousFill = gc.getFill();
        Paint previousStroke = gc.getStroke();
        double previousLineWidth = gc.getLineWidth();

        try {
            gc.setFont(font);
            if (shadow != null) {
                gc.setEffect(shadow);
            }

            String[] lines = text.split("\n");

            double lineHeight = font.getSize();
            double currentY = y;

            for (String line : lines) {
                if (strokePaint != null && strokeWidth > 0) {
                    gc.setStroke(strokePaint);
                    gc.setLineWidth(strokeWidth);
                    gc.strokeText(line, x, currentY);
                }

                gc.setFill(fillPaint);
                gc.fillText(line, x, currentY);

                currentY += lineHeight + 10;
            }
        } finally {
            gc.setFont(previousFont);
            gc.setEffect(null);
            gc.setFill(previousFill);
            gc.setStroke(previousStroke);
            gc.setLineWidth(previousLineWidth);
        }
    }
}


