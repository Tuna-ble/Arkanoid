package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;

/**
 * Lớp cơ sở (abstract class) cho tất cả các đối tượng game có khả năng di chuyển.
 * <p>
 * Lớp này kế thừa từ {@link GameObject} và bổ sung thêm
 * các thuộc tính vận tốc ({@code dx}, {@code dy})
 * và các phương thức (abstract) {@code update}, {@code render}.
 */
public abstract class MovableObject extends GameObject {
    protected double dx, dy; // Vận tốc theo trục X và Y

    /**
     * Khởi tạo một đối tượng có thể di chuyển (MovableObject).
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của {@link GameObject} (lớp cha)
     * và thiết lập vận tốc ban đầu ({@code dx}, {@code dy}).
     * <p>
     * <b>Expected:</b> Một đối tượng được tạo với vị trí,
     * kích thước, và vận tốc đã chỉ định.
     *
     * @param x      Tọa độ X ban đầu.
     * @param y      Tọa độ Y ban đầu.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param dx     Vận tốc X ban đầu (pixel/giây).
     * @param dy     Vận tốc Y ban đầu (pixel/giây).
     */
    public MovableObject(double x, double y, double width,
                         double height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * (Abstract) Cập nhật logic (ví dụ: vị trí) của đối tượng.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con (subclass) phải implement
     * để cập nhật trạng thái của chúng
     * (thường là di chuyển) dựa trên {@code deltaTime}.
     * <p>
     * <b>Expected:</b> Trạng thái (ví dụ: x, y, dx, dy)
     * của đối tượng được cập nhật.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    public abstract void update(double deltaTime);

    /**
     * (Abstract) Vẽ (render) đối tượng lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con (subclass) phải implement
     * để tự vẽ mình lên {@link GraphicsContext}.
     * <p>
     * <b>Expected:</b> Đối tượng được vẽ lên {@code gc}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public abstract void render(GraphicsContext gc);

    /**
     * Lấy vận tốc theo trục X (dx).
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code dx}.
     * <p>
     * <b>Expected:</b> Giá trị (double) của vận tốc X.
     *
     * @return Vận tốc X (dx).
     */
    public double getDx() {
        return dx;
    }

    /**
     * Lấy vận tốc theo trục Y (dy).
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code dy}.
     * <p>
     * <b>Expected:</b> Giá trị (double) của vận tốc Y.
     *
     * @return Vận tốc Y (dy).
     */
    public double getDy() {
        return dy;
    }

    /**
     * Đặt vận tốc theo trục X (dx).
     * <p>
     * <b>Định nghĩa:</b> Cập nhật giá trị của {@code dx}.
     * <p>
     * <b>Expected:</b> {@code dx} được cập nhật.
     *
     * @param dx Vận tốc X mới.
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * Đặt vận tốc theo trục Y (dy).
     * <p>
     * <b>Định nghĩa:</b> Cập nhật giá trị của {@code dy}.
     * <p>
     * <b>Expected:</b> {@code dy} được cập nhật.
     *
     * @param dy Vận tốc Y mới.
     */
    public void setDy(double dy) {
        this.dy = dy;
    }
}