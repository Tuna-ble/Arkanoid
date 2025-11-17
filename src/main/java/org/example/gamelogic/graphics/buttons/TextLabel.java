package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.graphics.TextRenderer;

/**
 * Quản lý một nhãn (Label) văn bản tĩnh.
 * <p>
 * Lớp này kế thừa {@link AbstractButton} (để có thể được
 * quản lý bởi {@code Window} và có hiệu ứng transition),
 * nhưng nó không xử lý input (click/hover)
 * mà chỉ dùng để hiển thị văn bản ({@code text}).
 */
public class TextLabel extends AbstractButton {

    /**
     * Khởi tạo một nhãn văn bản (TextLabel).
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí, kích thước, và văn bản.
     * Tải font chữ và màu sắc mặc định.
     * <p>
     * <b>Expected:</b> Một nhãn (Label) được tạo,
     * sẵn sàng để render văn bản.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param text   Văn bản (String) để hiển thị.
     */
    public TextLabel(double x, double y, double width, double height, String text) {
        super(x, y, width, height, text);

        this.font = AssetManager.getInstance().getFont("Anxel", 20);
        this.textColor = Color.WHITE;
    }

    /**
     * Xử lý input (ghi đè, để trống).
     * <p>
     * <b>Định nghĩa:</b> Phương thức này được cố tình để trống
     * vì TextLabel là một thành phần tĩnh, không tương tác.
     * <p>
     * <b>Expected:</b> Không có gì xảy ra khi người dùng
     * tương tác (hover, click) với nhãn này.
     *
     * @param input Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider input) {
    }

    /**
     * Vẽ (render) văn bản của nhãn.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code text} (đã được căn giữa)
     * tại vị trí của nhãn, sử dụng {@link TextRenderer}
     * để thêm viền (outline).
     * <p>
     * <b>Expected:</b> Văn bản được vẽ lên {@code gc}
     * với viền đen và màu tô trắng (mặc định).
     *
     * @param gc Context (bút vẽ) của canvas.
     */
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