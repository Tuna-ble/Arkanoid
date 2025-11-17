package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.core.ParticleManager;


/**
 * Quản lý đối tượng Gạch Cứng (HardBrick).
 * <p>
 * Lớp này kế thừa từ {@link AbstractBrick}.
 * Gạch này có nhiều "máu" ({@code durability})
 * và thay đổi hình ảnh ({@code brickImage})
 * khi bị mất máu,
 * trước khi bị phá hủy.
 */
public class HardBrick extends AbstractBrick {
    /// type: H
    private double durability;
    private Image brickImage;

    /**
     * Khởi tạo một Gạch Cứng (HardBrick).
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractBrick}).
     * Đặt độ bền ({@code durability}) mặc định
     * từ {@link GameConstants}
     * và tải ảnh ban đầu ("hardBrick1").
     * <p>
     * <b>Expected:</b> Một đối tượng gạch được tạo
     * với độ bền cao
     * và hình ảnh "chưa bị hỏng".
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public HardBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.durability = GameConstants.HARD_BRICK_DURABILITY;
        this.brickImage = AssetManager.getInstance().getImage("hardBrick1");
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
     * nếu hết độ bền. (Hình ảnh sẽ được cập nhật
     * trong hàm {@code render}).
     *
     * @param damage Lượng sát thương nhận.
     */
    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }

        this.durability -= damage;
        if (this.durability <= 0) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, Color.DARKGREY);
            this.isActive = false;
            EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        }
    }

    /**
     * Cập nhật logic của gạch (để trống).
     * <p>
     * <b>Định nghĩa:</b> Phương thức này được cố tình để trống
     * vì Gạch Cứng (HardBrick) là một đối tượng tĩnh,
     * không cần logic update mỗi frame
     * (việc thay đổi hình ảnh được xử lý trong {@code render}).
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
     * <b>Định nghĩa:</b> Kiểm tra {@code durability} hiện tại
     * để chọn đúng hình ảnh
     * ("hardBrick1", "hardBrick2", "hardBrick3")
     * từ {@link AssetManager} trước khi vẽ.
     * <p>
     * <b>Expected:</b> Gạch được vẽ lên {@code gc}
     * với hình ảnh (texture)
     * tương ứng với độ bền còn lại.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) {
            return;
        }
        AssetManager am = AssetManager.getInstance();
        if (this.durability > 2) {

        } else if (this.durability <= 2 && this.durability > 1) {
            this.brickImage = am.getImage("hardBrick2");
        } else {
            this.brickImage = am.getImage("hardBrick3");
        }
        gc.drawImage(brickImage, this.x, this.y, this.width, this.height);
    }

    /**
     * Tạo một bản sao (clone) của đối tượng HardBrick.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code HardBrick} mới
     * với kích thước (width, height) của mẫu.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Brick} mới
     * (là instance của {@code HardBrick})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của HardBrick.
     */
    @Override
    public Brick clone() {
        return new HardBrick(0, 0, this.width, this.height);
    }
}