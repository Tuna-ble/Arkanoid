package org.example.gamelogic.graphics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * Lớp tiện ích (utility class) tĩnh để vẽ văn bản (text) lên canvas.
 * <p>
 * Cung cấp các phương thức để render văn bản với các hiệu ứng
 * như viền (outline) và bóng (shadow).
 */
public final class TextRenderer {

    /**
     * Constructor riêng tư (private) để ngăn chặn việc khởi tạo
     * đối tượng từ lớp tiện ích này.
     */
    private TextRenderer() {}

    /**
     * Vẽ văn bản (có thể nhiều dòng) với viền và bóng.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập {@link GraphicsContext} (gc)
     * với font, màu sắc, và hiệu ứng (shadow) được cung cấp.
     * Vẽ viền (stroke) trước, sau đó vẽ tô (fill) lên trên.
     * Hỗ trợ ngắt dòng (dựa trên ký tự "\n").
     * <p>
     * <b>Expected:</b> Văn bản {@code text} được vẽ tại (x, y)
     * với các style đã chỉ định. {@code gc} được khôi phục
     * về trạng thái ban đầu sau khi vẽ xong.
     *
     * @param gc          Context (bút vẽ) của canvas.
     * @param text        Chuỗi văn bản cần vẽ (có thể chứa "\n").
     * @param x           Tọa độ X.
     * @param y           Tọa độ Y (của dòng đầu tiên).
     * @param font        Font chữ.
     * @param fillPaint   Màu tô (fill) bên trong chữ.
     * @param strokePaint Màu viền (stroke) bên ngoài.
     * @param strokeWidth Độ dày của viền.
     * @param shadow      Hiệu ứng bóng ({@link DropShadow})
     * (có thể là {@code null}).
     */
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