package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.data.AssetManager;
import org.example.presentation.SpriteAnimation;

/**
 * Quản lý đối tượng Gạch không thể phá hủy (UnbreakableBrick).
 * <p>
 * Lớp này kế thừa từ {@link AbstractBrick}.
 * Gạch này không thể bị phá hủy ({@code isBreakable()}
 * trả về {@code false}),
 * nhưng sẽ chạy một hoạt ảnh ({@code hitAnimation})
 * khi bị va chạm.
 */
public class UnbreakableBrick extends AbstractBrick {
    /// type: U

    private final Image brickImage;
    private SpriteAnimation hitAnimation;

    private boolean isAnimating = false;

    /**
     * Khởi tạo một Gạch không thể phá hủy (UnbreakableBrick).
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractBrick}).
     * Tải ảnh tĩnh ("unbreakableBrick")
     * và spritesheet hoạt ảnh ("unbreakableBrickHit")
     * từ {@link AssetManager}.
     * <p>
     * <b>Expected:</b> Một đối tượng gạch được tạo,
     * sẵn sàng để render và chạy animation khi va chạm.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        AssetManager am = AssetManager.getInstance();
        this.brickImage = am.getImage("unbreakableBrick");
        Image animationSheet = am.getImage("unbreakableBrickHit");
        if (animationSheet != null) {
            int frameCount = 5;
            int columns = 5;
            double duration = 0.5;
            boolean loops = false;
            this.hitAnimation = new SpriteAnimation(
                    animationSheet, frameCount, columns, duration, loops
            );
        }
    }

    /**
     * Xử lý khi gạch nhận sát thương (va chạm).
     * <p>
     * <b>Định nghĩa:</b> Không làm gì (gạch không bị phá hủy),
     * chỉ kích hoạt trạng thái {@code isAnimating = true}
     * và reset {@code hitAnimation}
     * để bắt đầu chạy hoạt ảnh va chạm.
     * <p>
     * <b>Expected:</b> {@code isAnimating} được đặt thành {@code true}.
     * Gạch không bị phá hủy.
     *
     * @param damage Lượng sát thương nhận (bị bỏ qua).
     */
    @Override
    public void takeDamage(double damage) {
        if (!isAnimating && hitAnimation != null) {
            isAnimating = true;
            hitAnimation.reset();
        }
    }

    /**
     * Kiểm tra xem gạch có thể bị phá vỡ hay không.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè (override)
     * phương thức của lớp cha.
     * <p>
     * <b>Expected:</b> Luôn trả về {@code false}.
     *
     * @return {@code false} (vì gạch này không thể bị phá hủy).
     */
    @Override
    public boolean isBreakable() {
        return false;
    }

    /**
     * Cập nhật logic của gạch (chủ yếu là animation).
     * <p>
     * <b>Định nghĩa:</b> Nếu đang ở trạng thái {@code isAnimating},
     * cập nhật (update) {@code hitAnimation}.
     * Đặt lại {@code isAnimating = false}
     * khi animation kết thúc.
     * <p>
     * <b>Expected:</b> {@code hitAnimation} được cập nhật (nếu đang chạy)
     * và trạng thái {@code isAnimating} được reset khi cần.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        if (isAnimating) {
            hitAnimation.update(deltaTime);
            if (hitAnimation.isFinished()) {
                isAnimating = false;
            }
        }
    }

    /**
     * Vẽ (render) gạch lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Nếu {@code isAnimating}
     * và {@code hitAnimation} tồn tại,
     * vẽ frame hiện tại của animation.
     * Ngược lại, vẽ ảnh tĩnh ({@code brickImage}).
     * <p>
     * <b>Expected:</b> Gạch được vẽ lên {@code gc}
     * (ảnh tĩnh hoặc animation)
     * nếu {@code !isDestroyed()}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            if (isAnimating && hitAnimation != null) {
                hitAnimation.render(gc, this.x, this.y, this.width, this.height);
            } else {
                gc.drawImage(brickImage, x, y, width, height);
            }
        }
    }

    /**
     * Tạo một bản sao (clone) của đối tượng UnbreakableBrick.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code UnbreakableBrick} mới
     * với kích thước (width, height) của mẫu.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Brick} mới
     * (là instance của {@code UnbreakableBrick})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của UnbreakableBrick.
     */
    @Override
    public Brick clone() {
        return new UnbreakableBrick(0, 0, this.width, this.height);
    }
}