package org.example.gamelogic.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Lớp tiện ích (utility class) tĩnh để sửa đổi hình ảnh.
 * <p>
 * Cung cấp các phương thức để xử lý
 * và tạo ra các phiên bản mới của đối tượng {@link Image}.
 */
public final class ImageModifier {

    /**
     * Constructor riêng tư (private).
     * <p>
     * <b>Định nghĩa:</b> Ngăn chặn việc khởi tạo
     * đối tượng từ lớp tiện ích này.
     * <p>
     * <b>Expected:</b> Lỗi biên dịch nếu cố gắng
     * gọi {@code new ImageModifier()}.
     */
    private ImageModifier() {

    }

    /**
     * Tạo một bản sao của ảnh với một tông màu (tint) mới.
     * <p>
     * <b>Định nghĩa:</b> Lặp qua từng pixel của ảnh nguồn {@code src}.
     * Giữ lại độ sáng (brightness) và độ trong suốt (opacity)
     * của pixel gốc, nhưng áp dụng màu {@code tint} vào.
     * Thường dùng để tô màu cho một ảnh xám (grayscale).
     * <p>
     * <b>Expected:</b> Trả về một {@link WritableImage} mới
     * có cùng kích thước với ảnh nguồn, nhưng đã được tô màu
     * (tinted). Các pixel trong suốt vẫn được giữ nguyên.
     *
     * @param src  Ảnh nguồn ({@link Image}) để tô màu.
     * @param tint Màu ({@link Color}) sẽ được áp dụng.
     * @return Một {@code WritableImage} mới đã được tô màu.
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
                    // Nhân tông màu (hue) của tint
                    // với độ sáng (brightness) của pixel xám
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