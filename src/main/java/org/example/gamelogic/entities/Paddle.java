package org.example.gamelogic.entities;

import java.lang.Math;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;
import org.example.data.AssetManager;

/**
 * Quản lý đối tượng Paddle (thanh đỡ) của người chơi.
 * <p>
 * Lớp này kế thừa từ {@link MovableObject},
 * xử lý logic di chuyển ngang,
 * giới hạn di chuyển trong khu vực chơi, và render hình ảnh.
 */
public class Paddle extends MovableObject {
    private Image paddleImage;
    private double speed;
    private double minX;
    private double maxX;

    /**
     * Khởi tạo đối tượng Paddle.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí, kích thước,
     * tốc độ ({@code speed}) mặc định,
     * và tính toán ranh giới di chuyển
     * ({@code minX}, {@code maxX})
     * dựa trên {@code GameConstants}. Tải ảnh {@code paddleImage}.
     * <p>
     * <b>Expected:</b> Một đối tượng Paddle được tạo,
     * sẵn sàng để di chuyển
     * trong khu vực chơi (play area).
     *
     * @param x      Tọa độ X ban đầu.
     * @param y      Tọa độ Y ban đầu.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param dx     Vận tốc X ban đầu (thường là 0).
     * @param dy     Vận tốc Y ban đầu (thường là 0).
     */
    public Paddle(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height, dx, dy);
        this.speed = GameConstants.PADDLE_SPEED;
        this.minX = GameConstants.PLAY_AREA_X;
        this.maxX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - this.width;

        this.paddleImage = AssetManager.getInstance().getImage("paddle");
    }

    /**
     * Lấy tọa độ X tại tâm của Paddle.
     * <p>
     * <b>Định nghĩa:</b> Tính toán
     * {@code x + width / 2.0}.
     * <p>
     * <b>Expected:</b> Trả về (double) tọa độ X
     * của điểm giữa Paddle.
     *
     * @return Tọa độ X ở tâm.
     */
    public double getCenterX() {
        return x + width / 2.0;
    }

    /**
     * Cập nhật vị trí của Paddle.
     * <p>
     * <b>Định nghĩa:</b> Di chuyển Paddle
     * dựa trên vận tốc {@code dx} và {@code deltaTime}.
     * Đảm bảo Paddle không di chuyển ra ngoài
     * ranh giới ({@code minX}, {@code maxX}).
     * <p>
     * <b>Expected:</b> Vị trí {@code x} của Paddle được cập nhật.
     * Paddle sẽ dừng lại ({@code dx = 0})
     * nếu chạm vào biên trái hoặc biên phải.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        x += dx * deltaTime;

        if (x < minX) {
            x = minX;
            dx = 0;
        }
        if (x > maxX) {
            x = maxX;
            dx = 0;
        }
    }

    /**
     * Đặt vận tốc (dx) để di chuyển sang trái.
     * <p>
     * <b>Định nghĩa:</b> Gán {@code dx = -speed}.
     * <p>
     * <b>Expected:</b> Paddle sẽ di chuyển sang trái
     * ở lần {@code update} tiếp theo.
     */
    public void moveLeft() {
        dx = -speed;
    }

    /**
     * Đặt vận tốc (dx) để di chuyển sang phải.
     * <p>
     * <b>Định nghĩa:</b> Gán {@code dx = speed}.
     * <p>
     * <b>Expected:</b> Paddle sẽ di chuyển sang phải
     * ở lần {@code update} tiếp theo.
     */
    public void moveRight() {
        dx = speed;
    }

    /**
     * Dừng di chuyển Paddle.
     * <p>
     * <b>Định nghĩa:</b> Gán {@code dx = 0}.
     * <p>
     * <b>Expected:</b> Paddle ngừng di chuyển
     * theo chiều ngang.
     */
    public void stop() {
        dx = 0;
    }

    /**
     * Đặt ranh giới di chuyển ngang cho Paddle.
     * <p>
     * <b>Định nghĩa:</b> Tính toán lại {@code minX} và {@code maxX}
     * dựa trên {@code GameConstants} và chiều rộng (width) hiện tại.
     * (Lưu ý: Tham số {@code minX}, {@code maxX}
     * hiện không được sử dụng trong thân hàm).
     * <p>
     * <b>Expected:</b> {@code minX} và {@code maxX}
     * của đối tượng được cập nhật.
     *
     * @param minX Ranh giới X tối thiểu (không được sử dụng).
     * @param maxX Ranh giới X tối đa (không được sử dụng).
     */
    public void setBounds(double minX, double maxX) {
        this.minX = GameConstants.PLAY_AREA_X;
        this.maxX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - this.width;
    }

    /**
     * Lấy tốc độ di chuyển của Paddle.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code speed}.
     * <p>
     * <b>Expected:</b> Tốc độ (double) của Paddle.
     *
     * @return Tốc độ (speed).
     */
    public double getSpeed() { return speed; }

    /**
     * Đặt tốc độ di chuyển của Paddle.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật giá trị của {@code speed}.
     * <p>
     * <b>Expected:</b> {@code speed} được cập nhật.
     *
     * @param speed Tốc độ mới.
     */
    public void setSpeed(double speed) { this.speed = speed; }

    /**
     * Vẽ (render) Paddle lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code paddleImage}
     * tại vị trí (x, y) với kích thước (width, height).
     * <p>
     * <b>Expected:</b> Hình ảnh Paddle được vẽ lên {@code gc}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(paddleImage, x, y, width, height);
    }

    /**
     * Đặt vận tốc ngang (dx) và dọc (dy) cho Paddle.
     * <p>
     * <b>Định nghĩa:</b> Gán giá trị cho {@code dx} và {@code dy}.
     * <p>
     * <b>Expected:</b> {@code dx} và {@code dy} được cập nhật.
     *
     * @param v Vận tốc X mới (dx).
     * @param i Vận tốc Y mới (dy).
     */
    public void setVelocity(double v, int i) {
        this.dx = v;
        this.dy = i;
    }

    /**
     * Đặt chiều rộng (width) cho Paddle.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè (override)
     * phương thức của lớp cha.
     * Cập nhật {@code width} và tính toán lại
     * ranh giới {@code maxX}.
     * <p>
     * <b>Expected:</b> {@code width} và {@code maxX}
     * được cập nhật.
     *
     * @param width Chiều rộng mới.
     */
    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        this.maxX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - this.width;
    }

    /**
     * Lấy tọa độ X hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code x}.
     * <p>
     * <b>Expected:</b> Tọa độ X (double).
     *
     * @return Tọa độ X.
     */
    @Override
    public double getX() {
        return this.x;
    }

    /**
     * Lấy tọa độ Y hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code y}.
     * <p>
     * <b>Expected:</b> Tọa độ Y (double).
     *
     * @return Tọa độ Y.
     */
    @Override
    public double getY() {
        return this.y;
    }

    /**
     * Lấy chiều rộng hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code width}.
     * <p>
     * <b>Expected:</b> Chiều rộng (double).
     *
     * @return Chiều rộng.
     */
    @Override
    public double getWidth() {
        return this.width;
    }

    /**
     * Đặt vị trí (x, y) của Paddle một cách trực tiếp.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code x} và {@code y}.
     * <p>
     * <b>Expected:</b> Vị trí của Paddle được thay đổi.
     *
     * @param x Tọa độ X mới.
     * @param y Tọa độ Y mới.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}