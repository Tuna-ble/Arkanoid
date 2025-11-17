package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;

/**
 * Quản lý một thanh trượt (Slider) UI cho phép người dùng chọn một giá trị (0.0 - 1.0).
 * <p>
 * Lớp này kế thừa {@link AbstractUIElement}
 * và xử lý logic kéo (dragging)
 * của con trỏ (handle) để thay đổi giá trị {@code value}.
 */
public class Slider extends AbstractUIElement {
    private double value;
    private boolean isDragging = false;

    private Image frameImage;
    private Image fillImage;
    private Image handleImage;

    private double trackX;
    private double trackY;
    private double trackWidth;
    private double trackHeight;
    private double handleX;
    private double handleWidth = height / 2.0;
    private double handleHeight = height * 1.4;

    private Rectangle bounds;

    /**
     * Khởi tạo một thanh trượt (Slider).
     * <p>
     * <b>Định nghĩa:</b> Tải tài nguyên (ảnh khung, fill, handle).
     * Tính toán kích thước của "track" (phần bên trong)
     * và đặt giá trị ban đầu ({@code initialValue}).
     * <p>
     * <b>Expected:</b> Thanh trượt được tạo
     * và con trỏ (handle) được đặt đúng vị trí
     * tương ứng với {@code initialValue}.
     *
     * @param x            Tọa độ X.
     * @param y            Tọa độ Y.
     * @param width        Chiều rộng.
     * @param height       Chiều cao.
     * @param initialValue Giá trị ban đầu (từ 0.0 đến 1.0).
     */
    public Slider(double x, double y, double width, double height, double initialValue) {
        super(x, y, width, height);

        AssetManager am = AssetManager.getInstance();
        this.frameImage = am.getImage("barFrame");
        this.fillImage = am.getImage("barFill");
        this.handleImage = am.getImage("barHandle");

        this.bounds = new Rectangle(x, y, width, height);
        double PADDING = 2;
        this.trackX = this.x + PADDING;
        this.trackY = this.y + PADDING;
        this.trackWidth = this.width - (2 * PADDING);
        this.trackHeight = this.height - (2 * PADDING);

        setValue(initialValue);
    }

    /**
     * Đặt giá trị cho thanh trượt.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật giá trị {@code value}
     * (giới hạn trong khoảng [0.0, 1.0])
     * và tính toán lại vị trí {@code handleX} (tọa độ X của con trỏ).
     * <p>
     * <b>Expected:</b> {@code value} được cập nhật.
     * {@code handleX} được di chuyển đến vị trí
     * tương ứng với giá trị mới.
     *
     * @param value Giá trị mới (từ 0.0 đến 1.0).
     */
    public void setValue(double value) {
        this.value = Math.max(0.0, Math.min(1.0, value));

        this.handleX = this.trackX + (this.trackWidth * this.value);
    }

    /**
     * Lấy giá trị hiện tại của thanh trượt.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị {@code value} đã lưu.
     * <p>
     * <b>Expected:</b> Trả về một số (double)
     * trong khoảng [0.0, 1.0].
     *
     * @return Giá trị hiện tại.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Xử lý input (kéo, thả chuột) cho thanh trượt.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra nếu chuột được nhấn
     * ({@code isMousePressed})
     * bên trong {@code bounds} để bắt đầu kéo ({@code isDragging}).
     * Khi đang kéo, cập nhật {@code value} và {@code handleX}
     * dựa trên vị trí chuột.
     * <p>
     * <b>Expected:</b> {@code value} và {@code handleX}
     * thay đổi theo thao tác kéo chuột của người dùng.
     * {@code isDragging} là false khi chuột được thả.
     *
     * @param input Nguồn cung cấp input (phím, chuột).
     */
    public void handleInput(I_InputProvider input) {
        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        boolean mousePressed = input.isMousePressed();

        if (mousePressed) {
            if (!isDragging && bounds.contains(mouseX, mouseY)) {
                isDragging = true;
            }

            if (isDragging) {
                double newHandleX = Math.max(this.trackX, Math.min(mouseX, this.trackX + this.trackWidth));

                this.value = (newHandleX - this.trackX) / this.trackWidth;
                this.handleX = newHandleX;
            }
        } else {
            isDragging = false;
        }
    }

    /**
     * Vẽ (render) thanh trượt (khung, phần tô, con trỏ).
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code frameImage} (khung).
     * Vẽ {@code fillImage} (phần tô) dựa trên {@code value}.
     * Vẽ {@code handleImage} (con trỏ) tại vị trí {@code handleX}.
     * <p>
     * <b>Expected:</b> Thanh trượt được vẽ lên {@code gc}
     * phản ánh đúng giá trị {@code value} hiện tại.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public void renderDefault(GraphicsContext gc) {
        gc.drawImage(frameImage, x, y, width, height);

        if (value > 0 && fillImage != null) {
            double dw = this.trackWidth * value;
            double dh = this.trackHeight;

            double sw = this.fillImage.getWidth() * value;
            double sh = this.fillImage.getHeight();

            gc.drawImage(
                    fillImage,
                    0, 0, sw, sh,
                    trackX, trackY, dw, dh
            );
        }

        if (handleImage != null) {
            double drawHandleX = this.handleX - (this.handleWidth / 2.0);
            double drawHandleY = this.y + (this.height / 2.0) - (this.handleHeight / 2.0);

            gc.drawImage(handleImage, drawHandleX, drawHandleY, handleWidth, handleHeight);
        }
    }
}