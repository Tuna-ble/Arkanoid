package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Rectangle2D;

/**
 * Lớp cơ sở (abstract class) cho tất cả các đối tượng trong game.
 * <p>
 * Lớp này định nghĩa các thuộc tính cơ bản nhất mà mọi đối tượng
 * trong game đều có: vị trí (x, y), kích thước (width, height),
 * và trạng thái ({@code isActive}).
 */
public abstract class GameObject {
    protected double x, y, width, height;
    protected boolean isActive;

    /**
     * Khởi tạo một đối tượng game (GameObject) cơ sở.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí (x, y)
     * và kích thước (width, height).
     * Đặt {@code isActive} mặc định là {@code true}.
     * <p>
     * <b>Expected:</b> Một đối tượng GameObject được tạo
     * với các thuộc tính đã gán và đang hoạt động.
     *
     * @param x      Tọa độ X ban đầu.
     * @param y      Tọa độ Y ban đầu.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isActive = true;
    }

    /**
     * (Abstract) Cập nhật logic của đối tượng.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con (subclass) phải implement
     * để cập nhật trạng thái của chúng dựa trên {@code deltaTime}.
     * <p>
     * <b>Expected:</b> Trạng thái của đối tượng
     * được cập nhật (ngay cả khi là đối tượng tĩnh).
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
     * Kiểm tra xem đối tượng có đang hoạt động hay không.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code true} nếu đối tượng đang hoạt động
     * (cần update/render/va chạm), ngược lại {@code false}.
     *
     * @return boolean Trạng thái hoạt động.
     */
    public boolean isActive() {
        return isActive;
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
    public double getX() { return x; }

    /**
     * Đặt tọa độ X.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code x}.
     * <p>
     * <b>Expected:</b> {@code x} được cập nhật.
     *
     * @param x Tọa độ X mới.
     */
    public void setX(double x) { this.x = x; }

    /**
     * Lấy tọa độ Y.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code y}.
     * <p>
     * <b>Expected:</b> Tọa độ Y (double).
     *
     * @return Tọa độ Y.
     */
    public double getY() { return y; }

    /**
     * Đặt tọa độ Y.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code y}.
     * <p>
     * <b>Expected:</b> {@code y} được cập nhật.
     *
     * @param y Tọa độ Y mới.
     */
    public void setY(double y) { this.y = y; }

    /**
     * Lấy chiều rộng.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code width}.
     * <p>
     * <b>Expected:</b> Chiều rộng (double).
     *
     * @return Chiều rộng.
     */
    public double getWidth() { return width; }

    /**
     * Đặt chiều rộng.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code width}.
     * <p>
     * <b>Expected:</b> {@code width} được cập nhật.
     *
     * @param width Chiều rộng mới.
     */
    public void setWidth(double width) { this.width = width; }

    /**
     * Lấy chiều cao.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code height}.
     * <p>
     * <b>Expected:</b> Chiều cao (double).
     *
     * @return Chiều cao.
     */
    public double getHeight() { return height; }

    /**
     * Đặt chiều cao.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code height}.
     * <p>
     * <b>Expected:</b> {@code height} được cập nhật.
     *
     * @param height Chiều cao mới.
     */
    public void setHeight(double height) { this.height = height; }

    /**
     * Đặt trạng thái hoạt động (active) của đối tượng.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code isActive} được đặt thành giá trị
     * {@code active} (thường dùng để "hủy" đối tượng).
     *
     * @param active True để kích hoạt, false để vô hiệu hóa.
     */
    public void setActive(boolean active) { isActive = active; }

    /**
     * Kiểm tra va chạm (intersection)
     * với một {@link GameObject} khác.
     * <p>
     * <b>Định nghĩa:</b> Sử dụng thuật toán AABB
     * (Axis-Aligned Bounding Box)
     * để kiểm tra xem hai hình chữ nhật
     * (của đối tượng này và {@code other})
     * có giao nhau hay không.
     * <p>
     * <b>Expected:</b> {@code true} nếu có va chạm,
     * {@code false} nếu không va chạm
     * hoặc nếu {@code other} là {@code null}.
     *
     * @param other Đối tượng GameObject khác cần kiểm tra.
     * @return boolean Trạng thái va chạm.
     */
    public boolean intersects(GameObject other) {
        if (other == null) {
            return false;
        }
        return this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y;
    }

    /**
     * Lấy chính đối tượng GameObject này.
     * <p>
     * <b>Định nghĩa:</b> Trả về tham chiếu {@code this}.
     * <p>
     * <b>Expected:</b> Trả về instance của chính đối tượng này.
     * (Thường dùng trong các lớp con
     * implement Interface và cần trả về GameObject).
     *
     * @return {@code this} (chính đối tượng này).
     */
    public GameObject getGameObject() {
        return this;
    }

}