package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.EnemyDestroyedEvent;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;
import org.example.presentation.SpriteAnimation;

/**
 * Quản lý một loại Enemy (Kẻ thù) cụ thể - Enemy1.
 * <p>
 * Lớp này kế thừa từ {@link AbstractEnemy} và
 * sử dụng {@link DownMovementStrategy} (di chuyển thẳng xuống).
 * Kẻ thù này sử dụng {@link SpriteAnimation} (idleAnimation)
 * để render và bị phá hủy ngay khi trúng đạn (1 HP).
 */
public class Enemy1 extends AbstractEnemy {
    private Image enemyImage;
    private SpriteAnimation idleAnimation;

    /**
     * Khởi tạo một đối tượng Enemy1 mới.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractEnemy})
     * và truyền vào một {@link DownMovementStrategy} cố định.
     * Tải hình ảnh spritesheet ("enemy1")
     * và khởi tạo {@link SpriteAnimation} (idleAnimation).
     * <p>
     * <b>Expected:</b> Một đối tượng Enemy1 được tạo,
     * sẵn sàng di chuyển xuống dưới và
     * hiển thị hoạt ảnh (animation).
     *
     * @param x      Tọa độ X ban đầu.
     * @param y      Tọa độ Y ban đầu.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param dx     Vận tốc X (thường là 0).
     * @param dy     Vận tốc Y (tốc độ di chuyển xuống).
     */
    public Enemy1(double x, double y, double width, double height,
                  double dx, double dy) {
        super(x, y, width, height, dx, dy, new DownMovementStrategy());
        this.health = 1;
        this.scoreValue = 100;
        AssetManager am = AssetManager.getInstance();
        this.enemyImage = am.getImage("enemy1");
        if (enemyImage != null) {
            int frameCount = 5;
            int columns = 8;
            double duration = 2;
            boolean loops = true;
            this.idleAnimation = new SpriteAnimation(
                    enemyImage, frameCount, columns, duration, loops
            );
        }
    }

    /**
     * Tạo một bản sao (clone) của đối tượng Enemy1.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code Enemy1} mới
     * với các thuộc tính cơ bản (width, height, dx, dy).
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Enemy} mới
     * (là instance của {@code Enemy1})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của Enemy1.
     */
    @Override
    public Enemy clone() {
        return new Enemy1(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    /**
     * Cập nhật logic của Enemy1.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code super.update(deltaTime)}
     * (để xử lý di chuyển và animation nổ)
     * và cập nhật {@code idleAnimation} (hoạt ảnh tĩnh).
     * <p>
     * <b>Expected:</b> Vị trí của Enemy được cập nhật
     * và {@code idleAnimation}
     * được chuyển sang frame tiếp theo.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (idleAnimation != null) {
            idleAnimation.update(deltaTime);
        }
    }

    /**
     * Xử lý logic khi "đi vào" màn hình.
     * <p>
     * <b>Định nghĩa:</b> Di chuyển Enemy
     * theo trục Y ({@code this.dy})
     * cho đến khi nó đi vào khu vực chơi ({@code PLAY_AREA_Y}).
     * <p>
     * <b>Expected:</b> {@code hasEnteredScreen}
     * được đặt thành {@code true}
     * khi {@code this.y} vượt qua ngưỡng vào màn hình.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void handleEntry(double deltaTime) {
        this.y += this.dy * deltaTime;

        if (this.y > GameConstants.PLAY_AREA_Y) {
            this.hasEnteredScreen = true;
        }
    }

    /**
     * Vẽ (render) Enemy1 lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code explosionAnim}
     * nếu {@code lifeState} là {@code DYING}.
     * Ngược lại, vẽ frame hiện tại
     * của {@code idleAnimation}.
     * <p>
     * <b>Expected:</b> Hình ảnh hoạt ảnh
     * (idle hoặc nổ) được vẽ lên {@code gc}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (lifeState == LifeState.DYING) {
            if (explosionAnim != null) {
                explosionAnim.render(gc, x, y, width, height);
            }
            return;
        }
        idleAnimation.render(gc, x, y, width, height);
    }

    /**
     * Xử lý khi Enemy nhận sát thương.
     * <p>
     * <b>Định nghĩa:</b> (Enemy1 chết ngay lập tức)
     * Đặt {@code lifeState} thành {@code DYING},
     * reset hoạt ảnh nổ ({@code explosionAnim}),
     * và dừng di chuyển (dx, dy = 0).
     * <p>
     * <b>Expected:</b> Enemy chuyển sang trạng thái "đang chết"
     * và bắt đầu chạy animation nổ.
     *
     * @param damage Lượng sát thương nhận
     * (không sử dụng, vì chết ngay).
     */
    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        this.lifeState = LifeState.DYING;
        EventManager.getInstance().publish(new EnemyDestroyedEvent(this));
        if (explosionAnim != null) {
            explosionAnim.reset();
        }
        this.setDx(0);
        this.setDy(0);
    }

    /**
     * Lấy loại (type) của Enemy.
     * <p>
     * <b>Định nghĩa:</b> Trả về mã định danh (key)
     * của Enemy này.
     * <p>
     * <b>Expected:</b> Trả về chuỗi (String) "E1".
     *
     * @return Mã loại "E1".
     */
    @Override
    public String getType() {
        return "E1";
    }
}