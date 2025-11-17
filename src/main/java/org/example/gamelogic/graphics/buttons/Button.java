package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.graphics.TextRenderer;

/**
 * Quản lý một nút bấm (Button) tiêu chuẩn có hình ảnh và văn bản.
 * <p>
 * Lớp này kế thừa {@link AbstractButton} và xử lý việc
 * vẽ (render) hình ảnh nền (normal/hover) và văn bản (text) ở giữa.
 */
public class Button extends AbstractButton {
    private Image normalImage;
    private Image hoverImage;
    private static final double FONT_SCALE_RATIO = 0.5;
    private static final String DEFAULT_FONT_FAMILY = "Anxel";

    /**
     * Khởi tạo Button với kích thước tùy chỉnh.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí, kích thước, ảnh (normal/hover),
     * và văn bản. Tự động tính toán kích thước font ({@code dynamicFontSize})
     * dựa trên chiều cao ({@code height}) của nút.
     * <p>
     * <b>Expected:</b> Nút bấm được tạo với kích thước
     * và font chữ tùy chỉnh.
     *
     * @param x           Tọa độ X.
     * @param y           Tọa độ Y.
     * @param width       Chiều rộng.
     * @param height      Chiều cao.
     * @param normalImage Ảnh nền khi ở trạng thái bình thường.
     * @param hoverImage  Ảnh nền khi được hover (di chuột qua).
     * @param text        Văn bản hiển thị trên nút.
     */
    public Button(double x, double y, double width, double height,
                  Image normalImage, Image hoverImage, String text) {
        super(x, y, width, height, text);
        this.normalImage = normalImage;
        this.hoverImage = (hoverImage != null) ? hoverImage : normalImage;

        double dynamicFontSize = height * FONT_SCALE_RATIO;

        this.font = new Font(DEFAULT_FONT_FAMILY, dynamicFontSize);
        this.textColor = Color.LIGHTBLUE;
    }

    /**
     * Khởi tạo Button với kích thước mặc định
     * từ {@link GameConstants}.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor chính với kích thước
     * ({@code UI_BUTTON_WIDTH}, {@code UI_BUTTON_HEIGHT}) mặc định.
     * <p>
     * <b>Expected:</b> Nút bấm được tạo với kích thước mặc định.
     *
     * @param x           Tọa độ X.
     * @param y           Tọa độ Y.
     * @param normalImage Ảnh nền khi ở trạng thái bình thường.
     * @param hoverImage  Ảnh nền khi được hover (di chuột qua).
     * @param text        Văn bản hiển thị trên nút.
     */
    public Button(double x, double y, Image normalImage, Image hoverImage, String text) {
        this(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT,
                normalImage, hoverImage, text);
    }

    /**
     * Vẽ (render) trạng thái mặc định (hoặc hover) của nút.
     * <p>
     * <b>Định nghĩa:</b> Vẽ hình ảnh nền ({@code hoverImage}
     * nếu {@code isHovered}, ngược lại là {@code normalImage}).
     * Sau đó, vẽ {@code text} (đã căn giữa)
     * lên trên ảnh bằng {@link TextRenderer}.
     * <p>
     * <b>Expected:</b> Nút bấm (ảnh nền và văn bản)
     * được vẽ lên {@code gc}
     * tại vị trí (x, y).
     *
     * @param gc Context (bút vẽ) của canvas.
     */
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