package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.events.ExplosiveBrickEvent;
import org.example.gamelogic.core.ParticleManager;

/**
 * Quản lý đối tượng Gạch Nổ (ExplosiveBrick).
 * <p>
 * Lớp này kế thừa từ {@link AbstractBrick}.
 * Gạch này bị phá hủy ngay lập tức khi trúng đạn
 * và phát ra sự kiện {@link ExplosiveBrickEvent}
 * để gây sát thương cho các gạch xung quanh.
 */
public class ExplosiveBrick extends AbstractBrick {
    /// type: E
    private Color color = Color.RED;
    private Image brickImage;

    /**
     * Khởi tạo một Gạch Nổ (ExplosiveBrick).
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractBrick}).
     * Tải ảnh ("explosiveBrick") từ {@link AssetManager}.
     * <p>
     * <b>Expected:</b> Một đối tượng gạch được tạo,
     * sẵn sàng để nổ khi va chạm.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public ExplosiveBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.brickImage = AssetManager.getInstance().getImage("explosiveBrick");
    }

    /**
     * Xử lý khi gạch nhận sát thương.
     * <p>
     * <b>Định nghĩa:</b> Phá hủy gạch ngay lập tức
     * (tạo hiệu ứng vỡ, đặt {@code isActive = false}).
     * Phát hai sự kiện:
     * {@link BrickDestroyedEvent} (để tính điểm)
     * và {@link ExplosiveBrickEvent} (để kích hoạt vụ nổ).
     * <p>
     * <b>Expected:</b> Gạch bị phá hủy ({@code isActive = false})
     * và sự kiện nổ được phát ra.
     *
     * @param damage Lượng sát thương nhận (bị bỏ qua, vì luôn nổ).
     */
    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, this.color);
        this.isActive = false;
        EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        EventManager.getInstance().publish(new ExplosiveBrickEvent(this));
    }

    /**
     * Cập nhật logic của gạch (để trống).
     * <p>
     * <b>Định nghĩa:</b> Phương thức này được cố tình để trống
     * vì Gạch Nổ (ExplosiveBrick) là một đối tượng tĩnh.
     * <p>
     * <b>Expected:</b> Không có gì xảy ra.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {

    }

    /**
     * Vẽ (render) gạch lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code brickImage}
     * tại vị trí (x, y)
     * nếu gạch chưa bị phá hủy ({@code !isDestroyed()}).
     * <p>
     * <b>Expected:</b> Gạch được vẽ lên {@code gc}
     * (nếu đang {@code isActive}).
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            gc.drawImage(brickImage, this.x, this.y, this.width, this.height);
        }
    }

    /**
     * Tạo một bản sao (clone) của đối tượng ExplosiveBrick.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code ExplosiveBrick} mới
     * với kích thước (width, height) của mẫu.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Brick} mới
     * (là instance của {@code ExplosiveBrick})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của ExplosiveBrick.
     */
    @Override
    public Brick clone() {
        return new ExplosiveBrick(0, 0, this.width, this.height);
    }
}