package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.BulletType;
import org.example.gamelogic.events.EnemyDestroyedEvent;
import org.example.gamelogic.strategy.movement.DashMovementStrategy;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;
import org.example.gamelogic.strategy.movement.LRMovementStrategy;

/**
 * Quản lý một loại Enemy (Kẻ thù) cụ thể - BossMinion (đệ tử của Boss).
 * <p>
 * Lớp này kế thừa từ {@link AbstractEnemy},
 * sử dụng {@link DashMovementStrategy} (lao nhanh),
 * và có khả năng tự động bắn đạn ({@code shootTimer}) thẳng xuống.
 * Kẻ thù này bị phá hủy ngay khi trúng đạn (1 HP).
 */
public class BossMinion extends AbstractEnemy {
    private boolean isShooting;
    private double shootTimer;
    private final double SHOOT_COOLDOWN = 2.5;

    private Image idleImage;
    private Image shootImage;

    /**
     * Khởi tạo một đối tượng BossMinion mới.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractEnemy})
     * và truyền vào một {@link DashMovementStrategy} cố định.
     * Đặt {@code hasEnteredScreen = true} (vào màn hình ngay lập tức)
     * và tải ảnh. Khởi tạo {@code shootTimer} ngẫu nhiên
     * để tránh bắn đồng loạt.
     * <p>
     * <b>Expected:</b> Một đối tượng BossMinion được tạo,
     * sẵn sàng di chuyển và bắn đạn.
     *
     * @param x      Tọa độ X ban đầu.
     * @param y      Tọa độ Y ban đầu.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param dx     Vận tốc X (do DashMovementStrategy quản lý).
     * @param dy     Vận tốc Y (do DashMovementStrategy quản lý).
     */
    public BossMinion(double x, double y, double width, double height,
                      double dx, double dy) {
        super(x, y, width, height, dx, dy, new DashMovementStrategy());
        this.shootTimer = Math.random() * SHOOT_COOLDOWN;
        this.hasEnteredScreen = true;
        AssetManager am = AssetManager.getInstance();
        this.idleImage = am.getImage("minion");
        this.shootImage = am.getImage("minionShoot");
    }

    /**
     * Tạo một bản sao (clone) của đối tượng BossMinion.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code BossMinion} mới
     * với các thuộc tính cơ bản (width, height, dx, dy).
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Enemy} mới
     * (là instance của {@code BossMinion})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của BossMinion.
     */
    @Override
    public Enemy clone() {
        return new BossMinion(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    /**
     * Cập nhật logic của BossMinion (di chuyển và bắn đạn).
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code super.update(deltaTime)}
     * (để chạy movementStrategy và animation nổ).
     * Tăng {@code shootTimer} và bắn ra một viên đạn
     * ({@code LaserManager.createBullet})
     * khi hết thời gian hồi (cooldown).
     * <p>
     * <b>Expected:</b> Vị trí của Enemy được cập nhật.
     * Một viên đạn mới được tạo nếu
     * {@code shootTimer} > {@code SHOOT_COOLDOWN}.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        shootTimer += deltaTime;

        if (shootTimer >= SHOOT_COOLDOWN) {
            shootTimer = 0.0;

            double bulletX = this.x + (this.width / 2.0) - 2;
            double bulletY = this.y + this.height;

            LaserManager.getInstance().createBullet(
                    bulletX,
                    bulletY,
                    0,
                    300,
                    BulletType.ENEMY_LASER,
                    BulletFrom.ENEMY
            );
        }
    }

    /**
     * Vẽ (render) BossMinion lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code explosionAnim}
     * nếu {@code lifeState} là {@code DYING}.
     * Ngược lại, vẽ {@code idleImage}
     * (ảnh tĩnh, không dùng {@code shootImage}).
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
        gc.drawImage(idleImage, this.x, this.y, this.width, this.height);
    }

    /**
     * Xử lý khi BossMinion nhận sát thương.
     * <p>
     * <b>Định nghĩa:</b> (BossMinion chết ngay lập tức)
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
     * Xử lý logic khi "đi vào" màn hình (ghi đè, để trống).
     * <p>
     * <b>Định nghĩa:</b> Phương thức này được cố tình để trống.
     * Trạng thái {@code hasEnteredScreen}
     * được đặt thành {@code true} ngay trong constructor.
     * <p>
     * <b>Expected:</b> Không có hành động gì xảy ra.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void handleEntry(double deltaTime) {

    }

    /**
     * Lấy loại (type) của Enemy.
     * <p>
     * <b>Định nghĩa:</b> Trả về mã định danh (key)
     * của Enemy này.
     * <p>
     * <b>Expected:</b> Trả về chuỗi (String) "MINION".
     *
     * @return Mã loại "MINION".
     */
    @Override
    public String getType() {
        return "MINION";
    }
}