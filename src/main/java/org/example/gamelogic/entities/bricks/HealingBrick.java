package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ParticleManager;
import org.example.gamelogic.events.BrickDestroyedEvent;

/**
 * Quản lý đối tượng Gạch Hồi Máu (HealingBrick).
 * <p>
 * Lớp này kế thừa từ {@link AbstractBrick}.
 * Gạch này có 2 trạng thái: {@code IDLE} và {@code DAMAGED}.
 * Khi bị va chạm lần 1, nó chuyển sang {@code DAMAGED}
 * và bắt đầu tự hồi phục ({@code healingTimer}).
 * Nếu bị va chạm lần 2 trong khi đang {@code DAMAGED},
 * nó sẽ bị phá hủy.
 * Nếu hết thời gian hồi phục, nó quay lại {@code IDLE}.
 */
public class HealingBrick extends AbstractBrick {
    /// type: R
    private enum State {
        IDLE,
        DAMAGED
    }

    private State currentState = State.IDLE;
    private double healingTimer = 0.0;
    private final double HEAL_TIME = 5.0; // Thời gian tự hồi phục

    private Image brickImage;
    private Color particleColor = Color.GREEN;

    /**
     * Khởi tạo một Gạch Hồi Máu (HealingBrick).
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractBrick}).
     * Tải ảnh ("healingBrick") từ {@link AssetManager}
     * và đặt trạng thái ban đầu là {@code IDLE}.
     * <p>
     * <b>Expected:</b> Một đối tượng gạch được tạo,
     * sẵn sàng để render và xử lý va chạm.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public HealingBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.isActive = true;
        this.brickImage = AssetManager.getInstance().getImage("healingBrick");
    }

    /**
     * Xử lý khi gạch nhận sát thương (va chạm).
     * <p>
     * <b>Định nghĩa:</b> Xử lý logic 2 lần va chạm.
     * <ul>
     * <li>Nếu đang {@code IDLE}: Chuyển sang {@code DAMAGED}
     * và bắt đầu đếm ngược {@code healingTimer}.</li>
     * <li>Nếu đang {@code DAMAGED}: Phá hủy gạch
     * (tạo hiệu ứng vỡ, đặt {@code isActive = false},
     * phát {@link BrickDestroyedEvent}).</li>
     * </ul>
     * <p>
     * <b>Expected:</b> Trạng thái gạch được cập nhật
     * (chuyển sang {@code DAMAGED})
     * hoặc gạch bị phá hủy (nếu va chạm lần 2).
     *
     * @param damage Lượng sát thương nhận (bị bỏ qua,
     * chỉ tính số lần va chạm).
     */
    @Override
    public void takeDamage(double damage) {
        if (currentState == State.IDLE) {
            currentState = State.DAMAGED;
            healingTimer = HEAL_TIME;
        } else if (currentState == State.DAMAGED) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, this.particleColor);
            this.isActive = false;
            EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        }
    }

    /**
     * Kiểm tra xem gạch đã bị phá hủy hay chưa.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè (override)
     * phương thức của lớp cha.
     * Trả về trạng thái ngược của {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code true} nếu gạch
     * không hoạt động, ngược lại {@code false}.
     *
     * @return boolean Trạng thái đã bị hủy.
     */
    @Override
    public boolean isDestroyed() {
        return !this.isActive;
    }

    /**
     * Kiểm tra xem gạch có thể bị phá vỡ hay không.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè (override)
     * phương thức của lớp cha.
     * <p>
     * <b>Expected:</b> Luôn trả về {@code true}
     * (vì gạch này có thể bị phá hủy).
     *
     * @return {@code true}.
     */
    @Override
    public boolean isBreakable() {
        return true;
    }

    /**
     * Cập nhật logic của gạch (hồi phục).
     * <p>
     * <b>Định nghĩa:</b> Nếu trạng thái là {@code DAMAGED},
     * giảm {@code healingTimer} theo {@code deltaTime}.
     * <p>
     * <b>Expected:</b> Nếu {@code healingTimer} <= 0,
     * trạng thái ({@code currentState})
     * được đặt lại thành {@code IDLE}.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        if (currentState == State.DAMAGED) {
            healingTimer -= deltaTime;
            if (healingTimer <= 0) {
                currentState = State.IDLE;
                healingTimer = 0;
            }
        }
    }

    /**
     * Vẽ (render) gạch lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ ảnh gạch ({@code brickImage}).
     * Nếu trạng thái là {@code DAMAGED},
     * vẽ thêm một lớp phủ màu trắng nhấp nháy
     * (pulse effect) dựa trên {@code healingTimer}.
     * <p>
     * <b>Expected:</b> Gạch được vẽ lên {@code gc}.
     * Nếu đang {@code DAMAGED},
     * gạch sẽ nhấp nháy màu trắng.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        gc.drawImage(brickImage, this.x, this.y, this.width, this.height);

        if (currentState == State.DAMAGED) {
            // Tạo hiệu ứng nhấp nháy (pulse)
            // bằng hàm sin dựa trên thời gian
            double pulseAlpha = (Math.sin(healingTimer * 10) + 1) / 2.0;
            gc.save();
            try {
                gc.setGlobalAlpha(pulseAlpha * 0.7);
                gc.setFill(Color.WHITE);
                gc.fillRect(x, y, width, height);
            } finally {
                gc.restore();
            }
        }
    }

    /**
     * Tạo một bản sao (clone) của đối tượng HealingBrick.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code HealingBrick} mới
     * với kích thước (width, height) của mẫu.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Brick} mới
     * (là instance của {@code HealingBrick})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của HealingBrick.
     */
    @Override
    public Brick clone() {
        return new HealingBrick(0, 0, this.width, this.height);
    }
}