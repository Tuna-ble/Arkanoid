package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.EnemyDestroyedEvent;
import org.example.gamelogic.graphics.ImageModifier;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;

/**
 * Quản lý một loại Enemy (Kẻ thù) cụ thể - Enemy2.
 * <p>
 * Lớp này kế thừa từ {@link AbstractEnemy} và
 * sử dụng {@link DownMovementStrategy}
 * (di chuyển thẳng xuống) làm logic di chuyển mặc định.
 * Kẻ thù này bị phá hủy ngay khi trúng đạn (1 HP).
 */
public class Enemy2 extends AbstractEnemy {
    private Image enemyImage;
    private static final double ENEMY_SPRITE_WIDTH = 180;
    private static final double ENEMY_SPRITE_HEIGHT = 140;

    /**
     * Khởi tạo một đối tượng Enemy2 mới.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractEnemy})
     * và truyền vào một {@link DownMovementStrategy} cố định.
     * Tải hình ảnh ("enemy2") từ {@link AssetManager}.
     * <p>
     * <b>Expected:</b> Một đối tượng Enemy2 được tạo,
     * sẵn sàng di chuyển xuống dưới.
     *
     * @param x      Tọa độ X ban đầu.
     * @param y      Tọa độ Y ban đầu.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param dx     Vận tốc X (thường là 0 cho strategy này).
     * @param dy     Vận tốc Y (tốc độ di chuyển xuống).
     */
    public Enemy2(double x, double y, double width, double height,
                  double dx, double dy) {
        super(x, y, width, height, dx, dy, new DownMovementStrategy());

        AssetManager am = AssetManager.getInstance();
        this.enemyImage = am.getImage("enemy2");
    }

    /**
     * Tạo một bản sao (clone) của đối tượng Enemy2.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code Enemy2} mới
     * với các thuộc tính cơ bản (width, height, dx, dy).
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Enemy} mới
     * (là instance của {@code Enemy2})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của Enemy2.
     */
    @Override
    public Enemy clone() {
        return new Enemy2(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    /**
     * Cập nhật logic của Enemy2.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code super.update(deltaTime)}
     * (chủ yếu để chạy logic của movementStrategy
     * và cập nhật animation).
     * <p>
     * <b>Expected:</b> Vị trí của Enemy được cập nhật,
     * animation (nếu có) được cập nhật.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
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
     * Vẽ (render) Enemy2 lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code explosionAnim}
     * nếu {@code lifeState} là {@code DYING}.
     * Ngược lại, vẽ {@code enemyImage} (ảnh tĩnh)
     * tại vị trí (x, y).
     * <p>
     * <b>Expected:</b> Hình ảnh Enemy
     * hoặc hoạt ảnh nổ được vẽ lên {@code gc}.
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
        gc.drawImage(enemyImage, x, y, width, height);
    }

    /**
     * Xử lý khi Enemy nhận sát thương.
     * <p>
     * <b>Định nghĩa:</b> (Enemy2 chết ngay lập tức)
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
     * <b>Expected:</b> Trả về chuỗi (String) "E2".
     *
     * @return Mã loại "E2".
     */
    @Override
    public String getType() {
        return "E2";
    }
}