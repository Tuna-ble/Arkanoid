package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.strategy.transition.button.IUIElementTransitionStrategy;

/**
 * Lớp cơ sở (abstract class) cho tất cả các thành phần giao diện người dùng (UI).
 * <p>
 * Quản lý các thuộc tính chung như vị trí, kích thước,
 * trạng thái "vô hiệu hóa" (disabled), và logic
 * cho hiệu ứng chuyển cảnh (transition).
 * Sử dụng Template Method Pattern cho {@code render()}.
 */
public abstract class AbstractUIElement {
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    protected IUIElementTransitionStrategy transition;
    protected boolean transitionStarted = false;
    protected boolean transitionFinished;
    protected boolean isDisabled = false;

    /**
     * Khởi tạo một thành phần UI cơ sở.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí (x, y) và kích thước
     * (width, height).
     * <p>
     * <b>Expected:</b> Đối tượng được tạo với các
     * thuộc tính đã gán,
     * {@code transitionFinished} mặc định là {@code false}.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public AbstractUIElement(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.transitionFinished = false;
    }

    /**
     * Gán một chiến lược (strategy) hiệu ứng chuyển cảnh (transition).
     * <p>
     * <b>Định nghĩa:</b> Đặt chiến lược transition
     * cho thành phần UI này.
     * <p>
     * <b>Expected:</b> {@code transition} được lưu.
     * Nếu transition được gán là {@code null},
     * {@code transitionFinished} được đặt thành {@code true}
     * (vì không có gì để chạy).
     *
     * @param transition Chiến lược transition
     * (hoặc {@code null} nếu không có).
     */
    public void setTransition(IUIElementTransitionStrategy transition) {
        this.transition = transition;
        if (transition == null) {
            this.transitionFinished = true;
        }
    }

    /**
     * Bắt đầu chạy hiệu ứng transition.
     * <p>
     * <b>Định nghĩa:</b> Kích hoạt transition (nếu có)
     * và đánh dấu {@code transitionStarted} là true.
     * <p>
     * <b>Expected:</b> {@code transition.start()} được gọi.
     * Nếu không có transition, {@code transitionFinished}
     * được đặt ngay lập tức thành {@code true}.
     */
    public void startTransition() {
        if (transition != null && !transitionStarted) {
            transition.start();
            transitionStarted = true;
        } else if (transition == null) {
            transitionFinished = true;
        }
    }

    /**
     * Cập nhật logic của hiệu ứng transition (nếu đang chạy).
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code transition.update(deltaTime)}
     * và kiểm tra nếu transition đã hoàn tất.
     * <p>
     * <b>Expected:</b> Trạng thái của transition được cập nhật.
     * {@code transitionFinished} được đặt thành {@code true}
     * khi hiệu ứng kết thúc.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    public void updateTransition(double deltaTime) {
        if (transition != null && transitionStarted && !transitionFinished) {
            transition.update(deltaTime);
            if (transition.isFinished()) {
                transitionFinished = true;
            }
        }
    }

    /**
     * Phương thức render chính (Template Method).
     * <p>
     * <b>Định nghĩa:</b> Xử lý logic render chung:
     * áp dụng hiệu ứng mờ (alpha) nếu {@code isDisabled}.
     * Ủy quyền (delegate) việc vẽ cho
     * {@code transition.renderElement()} (nếu đang chạy)
     * hoặc {@link #renderDefault(GraphicsContext)} (nếu đã xong).
     * <p>
     * <b>Expected:</b> Thành phần UI được vẽ lên {@code gc},
     * có hiệu ứng mờ (disabled) hoặc hiệu ứng transition.
     * {@code gc} được khôi phục (restore) sau khi vẽ.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public void render(GraphicsContext gc) {
        gc.save();
        try {
            if (this.isDisabled) {
                gc.setGlobalAlpha(0.5);
            }
            if (transition != null && !transitionFinished) {
                transition.renderElement(gc, this);
            }
            else {
                renderDefault(gc);
            }
        } finally {
            gc.restore();
        }

    }

    /**
     * (Abstract) Vẽ trạng thái mặc định (tĩnh) của thành phần UI.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con (subclass) phải implement
     * để vẽ hình ảnh/văn bản/hình dạng của chúng.
     * <p>
     * <b>Expected:</b> Lớp con sẽ vẽ
     * trạng thái trực quan của nó lên {@code gc}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public abstract void renderDefault(GraphicsContext gc);

    /**
     * (Abstract) Xử lý input (hover, click) cho thành phần UI.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con phải implement để xử lý tương tác.
     * <p>
     * <b>Expected:</b> Lớp con sẽ cập nhật trạng thái
     * (ví dụ: `isHovered`, `isClicked`)
     * dựa trên {@code input}.
     *
     * @param input Nguồn cung cấp input (phím, chuột).
     */
    public abstract void handleInput(I_InputProvider input);

    /**
     * Kiểm tra xem hiệu ứng transition đã hoàn tất chưa.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code true}
     * nếu transition là {@code null}
     * hoặc {@code transitionFinished} là {@code true}.
     * <p>
     * <b>Expected:</b> {@code true} nếu không có
     * hiệu ứng nào đang chạy,
     * ngược lại {@code false}.
     *
     * @return boolean Trạng thái hoàn tất của transition.
     */
    public boolean isTransitionFinished() {
        return (transition == null) || transitionFinished;
    }

    /**
     * Đặt trạng thái "vô hiệu hóa" (disabled) cho thành phần UI.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật biến {@code isDisabled}.
     * <p>
     * <b>Expected:</b> {@code isDisabled} được đặt thành
     * giá trị {@code disabled}.
     *
     * @param disabled True để vô hiệu hóa, false để kích hoạt.
     */
    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
    }

    /**
     * Lấy trạng thái "vô hiệu hóa" (disabled) hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code isDisabled}.
     * <p>
     * <b>Expected:</b> {@code true} nếu element bị vô hiệu hóa,
     * ngược lại {@code false}.
     *
     * @return boolean Trạng thái vô hiệu hóa.
     */
    public boolean isDisabled() {
        return this.isDisabled;
    }

    /**
     * Lấy tọa độ X.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code x}.
     * <p>
     * <b>Expected:</b> Tọa độ X (double).
     *
     * @return Tọa độ X.
     */
    public double getX() {
        return x;
    }

    /**
     * Đặt tọa độ X.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code x}.
     * <p>
     * <b>Expected:</b> {@code x} được cập nhật.
     *
     * @param x Tọa độ X mới.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Lấy tọa độ Y.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code y}.
     * <p>
     * <b>Expected:</b> Tọa độ Y (double).
     *
     * @return Tọa độ Y.
     */
    public double getY() {
        return y;
    }

    /**
     * Đặt tọa độ Y.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code y}.
     * <p>
     * <b>Expected:</b> {@code y} được cập nhật.
     *
     * @param y Tọa độ Y mới.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Lấy chiều rộng.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code width}.
     * <p>
     * <b>Expected:</b> Chiều rộng (double).
     *
     * @return Chiều rộng.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Đặt chiều rộng.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code width}.
     * <p>
     * <b>Expected:</b> {@code width} được cập nhật.
     *
     * @param width Chiều rộng mới.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Lấy chiều cao.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code height}.
     * <p>
     * <b>Expected:</b> Chiều cao (double).
     *
     * @return Chiều cao.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Đặt chiều cao.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code height}.
     * <p>
     * <b>Expected:</b> {@code height} được cập nhật.
     *
     * @param height Chiều cao mới.
     */
    public void setHeight(double height) {
        this.height = height;
    }
}