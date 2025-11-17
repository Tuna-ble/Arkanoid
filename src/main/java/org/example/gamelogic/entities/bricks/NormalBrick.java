package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ParticleManager;
import org.example.gamelogic.events.BrickDestroyedEvent;

/**
 * Quản lý đối tượng Gạch Thường (NormalBrick).
 * <p>
 * Lớp này kế thừa từ {@link AbstractBrick}.
 * Đây là loại gạch cơ bản nhất,
 * có độ bền ({@code durability})
 * và bị phá hủy khi độ bền về 0.
 */
public class NormalBrick extends AbstractBrick {
    // type: N
    private double durability;
    private Color color = Color.CYAN;
    private final Image brickImage;

    /**
     * Khởi tạo một Gạch Thường (NormalBrick).
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractBrick}).
     * Đặt độ bền ({@code durability}) mặc định
     * từ {@link GameConstants}
     * và tải ảnh ("normalBrick").
     * <p>
     * <b>Expected:</b> Một đối tượng gạch được tạo
     * với độ bền mặc định
     * và sẵn sàng để render.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.durability = GameConstants.BRICK_DURABILITY;
        this.brickImage = AssetManager.getInstance().getImage("normalBrick");
    }

    /**
     * Xử lý khi gạch nhận sát thương.
     * <p>
     * <b>Định nghĩa:</b> Giảm {@code durability}
     * dựa trên {@code damage}.
     * Nếu {@code durability} <= 0,
     * tạo hiệu ứng vỡ ({@code ParticleManager.spawnBrickDebris}),
     * đặt {@code isActive = false},
     * và phát sự kiện (publish) {@link BrickDestroyedEvent}.
     * <p>
     * <b>Expected:</b> Độ bền của gạch giảm.
     * Gạch bị phá hủy ({@code isActive = false})
     * nếu hết độ bền.
     *
     * @param damage Lượng sát thương nhận.
     */
    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }

        this.durability -= damage;
        if (this.durability <= 0) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, this.color);
            this.isActive = false;
            EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        }
    }

    /**
     * Lấy giá trị điểm (score) khi phá hủy gạch này.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị điểm
     * (hiện tại là 0 cho gạch thường).
     * <p>
     * <b>Expected:</b> Trả về 0.
     *
     * @return 0.
     */
    public int getScore() {
        return 0;
    }

    /**
     * Cập nhật logic của gạch (để trống).
     * <p>
     * <b>Định nghĩa:</b> Phương thức này được cố tình để trống
     * vì Gạch Thường (NormalBrick) là một đối tượng tĩnh,
     * không cần logic update mỗi frame.
     * <p>
     * <b>Expected:</b> Không có gì xảy ra.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
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
     * Tạo một bản sao (clone) của đối tượng NormalBrick.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code NormalBrick} mới
     * với kích thước (width, height) của mẫu.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Brick} mới
     * (là instance của {@code NormalBrick})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của NormalBrick.
     */
    @Override
    public Brick clone() {
        return new NormalBrick(0, 0, this.width, this.height);
    }
}