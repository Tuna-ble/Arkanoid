package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.entities.MovableObject;
import org.example.gamelogic.events.*;
import org.example.gamelogic.strategy.movement.EnemyMovementStrategy;
import org.example.presentation.SpriteAnimation;

/**
 * Lớp cơ sở (abstract class) cho tất cả các đối tượng Kẻ thù (Enemy).
 * <p>
 * Lớp này kế thừa từ {@link MovableObject} và implement {@link Enemy}.
 * Nó quản lý logic chung bao gồm máu (health), điểm (score),
 * trạng thái sống ({@code LifeState}), animation nổ,
 * chiến lược di chuyển ({@link EnemyMovementStrategy}),
 * và tự động đăng ký các sự kiện (va chạm, nhận sát thương).
 */
public abstract class AbstractEnemy extends MovableObject implements Enemy {
    private boolean isHit = false;
    protected boolean outOfBounds = false;
    protected double health;
    protected double scoreValue;
    protected boolean hasEnteredScreen;
    protected final Image enemySprites;

    protected SpriteAnimation explosionAnim;
    protected enum LifeState {
        ALIVE,
        DYING
    }
    protected LifeState lifeState = LifeState.ALIVE;

    protected EnemyMovementStrategy movementStrategy;

    /**
     * Khởi tạo một đối tượng Kẻ thù (AbstractEnemy) cơ sở.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super` ({@link MovableObject}),
     * gán chiến lược di chuyển ({@code initialMovementStrategy}),
     * tải animation nổ (explosionAnim),
     * và gọi {@link #subscribeToEvents()}
     * để lắng nghe sự kiện.
     * <p>
     * <b>Expected:</b> Đối tượng Enemy được tạo,
     * {@code isActive = true}, {@code hasEnteredScreen = false},
     * và sẵn sàng lắng nghe các sự kiện va chạm.
     *
     * @param x                       Tọa độ X ban đầu.
     * @param y                       Tọa độ Y ban đầu.
     * @param width                   Chiều rộng.
     * @param height                  Chiều cao.
     * @param dx                      Vận tốc X ban đầu.
     * @param dy                      Vận tốc Y ban đầu.
     * @param initialMovementStrategy Chiến lược di chuyển ban đầu.
     */
    public AbstractEnemy(double x, double y, double width, double height,
                         double dx, double dy, EnemyMovementStrategy initialMovementStrategy) {
        super(x, y, width, height, dx, dy);
        this.isActive = true;
        this.hasEnteredScreen = false;
        this.movementStrategy = initialMovementStrategy;
        this.enemySprites = AssetManager.getInstance().getImage("enemies");

        Image sheet = AssetManager.getInstance().getImage("enemyExplode");
        if (sheet != null) {
            this.explosionAnim = new SpriteAnimation(sheet, 7, 7, 0.5, false);
        }
        subscribeToEvents();
    }

    /**
     * (Helper) Đăng ký các hàm xử lý sự kiện (event handler)
     * của Enemy này vào {@link EventManager}.
     * <p>
     * <b>Định nghĩa:</b> Đăng ký `onHit`
     * (cho {@link BallHitEnemyEvent})
     * và `onDamaged` (cho {@link EnemyDamagedEvent}).
     * <p>
     * <b>Expected:</b> Instance Enemy này
     * sẽ tự động phản ứng
     * khi các sự kiện tương ứng được phát ra.
     */
    private void subscribeToEvents() {
        EventManager.getInstance().subscribe(
                BallHitEnemyEvent.class,
                this::onHit
        );
        EventManager.getInstance().subscribe(
                EnemyDamagedEvent.class,
                this::onDamaged
        );
    }

    /**
     * (Event Handler) Xử lý khi nhận sự kiện {@link EnemyDamagedEvent}.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra xem sự kiện
     * có phải dành cho Enemy này không.
     * Nếu đúng, gọi {@link #takeDamage(double)}
     * dựa trên nguồn sát thương (vd: LaserBullet).
     * <p>
     * <b>Expected:</b> Enemy nhận sát thương
     * (gọi {@code takeDamage})
     * nếu là mục tiêu của sự kiện.
     *
     * @param event Sự kiện sát thương (EnemyDamagedEvent).
     */
    private void onDamaged(EnemyDamagedEvent event) {
        if (event.getDamagedEnemy()==this && !isDestroyed()) {
            GameObject damageSource=event.getDamageSource();
            if (damageSource instanceof LaserBullet) {
                takeDamage(GameConstants.LASER_BULLET_DAMAGE);
            }
        }
    }

    /**
     * (Event Handler) Xử lý khi nhận sự kiện {@link BallHitEnemyEvent}.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra xem sự kiện
     * có phải dành cho Enemy này không.
     * Nếu đúng, gọi {@link #takeDamage(double)}
     * với sát thương mặc định của bóng
     * ({@code GameConstants.BALL_DAMAGE}).
     * <p>
     * <b>Expected:</b> Enemy nhận sát thương
     * (gọi {@code takeDamage})
     * nếu là mục tiêu của sự kiện.
     *
     * @param event Sự kiện va chạm với Bóng (BallHitEnemyEvent).
     */
    private void onHit(BallHitEnemyEvent event) {
        if (event.getEnemy() == this && !isDestroyed()) {
            takeDamage(GameConstants.BALL_DAMAGE);
        }
    }

    /**
     * (Abstract - Prototype Pattern)
     * Tạo một bản sao (clone) của đối tượng Enemy này.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc
     * (abstract) mà các lớp con phải implement
     * để hỗ trợ (Prototype Pattern) (dùng bởi Factory).
     * <p>
     * <b>Expected:</b> Lớp con sẽ trả về
     * một instance mới của chính nó.
     *
     * @return Một đối tượng {@link Enemy} mới (clone).
     */
    @Override
    public abstract Enemy clone();

    /**
     * Cập nhật logic của Enemy (Template Method).
     * <p>
     * <b>Định nghĩa:</b> Quản lý trạng thái {@code LifeState}.
     * Nếu là {@code DYING}, chạy {@code explosionAnim}
     * và tự hủy ({@code isActive = false}) khi xong.
     * Nếu là {@code ALIVE}, gọi {@link #handleEntry(double)}
     * (nếu chưa vào màn hình)
     * hoặc {@code movementStrategy.move()} (nếu đã vào).
     * <p>
     * <b>Expected:</b> Vị trí Enemy được cập nhật
     * bởi strategy.
     * {@code isActive} được đặt thành {@code false}
     * khi animation nổ kết thúc
     * hoặc khi Enemy bay ra khỏi đáy màn hình.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        if (lifeState == LifeState.DYING) {
            if (explosionAnim != null) {
                explosionAnim.update(deltaTime);
                if (explosionAnim.isFinished()) {
                    this.isActive = false;
                }
            } else {
                this.isActive = false;
            }
            return;
        }

        if (!this.hasEnteredScreen) {
            handleEntry(deltaTime);
        } else {
            if (this.movementStrategy != null) {
                this.movementStrategy.move(this, deltaTime);
            }
        }
        if (this.y > GameConstants.SCREEN_HEIGHT) {
            this.setActive(false);
        }
    }

    /**
     * (Abstract) Vẽ (render) Enemy lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con phải implement
     * để vẽ trạng thái trực quan (ảnh tĩnh, animation)
     * của chúng.
     * <p>
     * <b>Expected:</b> Lớp con sẽ vẽ
     * hình ảnh/animation của nó lên {@code gc}.
     * (Logic vẽ {@code explosionAnim}
     * thường được xử lý trong implementation
     * của lớp con).
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public abstract void render(GraphicsContext gc);

    /**
     * (Abstract) Xử lý logic khi Enemy "đi vào" màn hình.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con phải implement
     * để định nghĩa hành vi di chuyển
     * ban đầu (trước khi {@code hasEnteredScreen = true}).
     * <p>
     * <b>Expected:</b> Lớp con sẽ di chuyển Enemy
     * và đặt {@code hasEnteredScreen = true}
     * khi logic "đi vào" hoàn tất.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    public abstract void handleEntry(double deltaTime);

    /**
     * Đặt vị trí (x, y) của Enemy một cách trực tiếp.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code x} và {@code y}.
     * <p>
     * <b>Expected:</b> Vị trí của Enemy được thay đổi.
     *
     * @param x Tọa độ X mới.
     * @param y Tọa độ Y mới.
     */
    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Đặt trạng thái "đã vào màn hình" (hasEnteredScreen).
     * <p>
     * <b>Định nghĩa:</b> Cập nhật cờ (flag)
     * {@code hasEnteredScreen}.
     * <p>
     * <b>Expected:</b> {@code hasEnteredScreen}
     * được đặt thành giá trị {@code hasEnteredScreen}.
     *
     * @param hasEnteredScreen True nếu Enemy
     * đã hoàn tất logic "đi vào".
     */
    @Override
    public void setHasEnteredScreen(boolean hasEnteredScreen) {
        this.hasEnteredScreen = hasEnteredScreen;
    }

    /**
     * Đặt (thay đổi) chiến lược di chuyển (Movement Strategy)
     * của Enemy.
     * <p>
     * <b>Định nghĩa:</b> Gán một
     * {@link EnemyMovementStrategy}
     * mới cho Enemy.
     * <p>
     * <b>Expected:</b> {@code movementStrategy}
     * được cập nhật. Logic di chuyển
     * của Enemy sẽ thay đổi ở lần {@code update} tiếp theo.
     *
     * @param newStrategy Chiến lược di chuyển mới.
     */
    public void setMovementStrategy(EnemyMovementStrategy newStrategy) {
        this.movementStrategy = newStrategy;
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
        return width;
    }

    /**
     * Lấy chiều cao hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code height}.
     * <p>
     * <b>Expected:</b> Chiều cao (double).
     *
     * @return Chiều cao.
     */
    @Override
    public double getHeight() {
        return height;
    }

    /**
     * Kiểm tra xem Enemy có ở ngoài ranh giới (bounds) không
     * (dùng trong logic cũ, hiện không được cập nhật).
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code outOfBounds}.
     * <p>
     * <b>Expected:</b> Trạng thái out-of-bounds (boolean).
     *
     * @return boolean Trạng thái out-of-bounds.
     */
    public boolean isOutOfBounds() {
        return outOfBounds;
    }

    /**
     * Kiểm tra xem đối tượng có đang hoạt động hay không.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè (override)
     * {@link GameObject#isActive()}.
     * Trả về giá trị của {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code true} nếu Enemy đang hoạt động,
     * ngược lại {@code false}.
     *
     * @return boolean Trạng thái hoạt động.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Đặt trạng thái hoạt động (active) của đối tượng.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè (override)
     * {@link GameObject#setActive(boolean)}.
     * Cập nhật {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code isActive} được cập nhật.
     *
     * @param active True để kích hoạt, false để vô hiệu hóa.
     */
    @Override
    public void setActive(boolean active) {
        this.isActive=active;
    }

    /**
     * Lấy chính đối tượng GameObject này.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè (override)
     * {@link GameObject#getGameObject()}.
     * Trả về tham chiếu {@code this}.
     * <p>
     * <b>Expected:</b> Trả về instance của chính đối tượng này.
     *
     * @return {@code this}.
     */
    @Override
    public GameObject getGameObject() {
        return this;
    }

    /**
     * Kiểm tra xem Enemy đã bị "hủy" (inactive) hay chưa.
     * <p>
     * <b>Định nghĩa:</b> Trả về trạng thái
     * ngược của {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code true} nếu Enemy
     * không hoạt động, ngược lại {@code false}.
     *
     * @return boolean Trạng thái đã bị hủy.
     */
    @Override
    public boolean isDestroyed() {
        return !isActive();
    }

    /**
     * Hủy (deactivate) Enemy.
     * <p>
     * <b>Định nghĩa:</b> Đặt {@code isActive = false}.
     * <p>
     * <b>Expected:</b> Enemy ngừng hoạt động
     * (sẽ bị xóa bởi EnemyManager).
     */
    public void destroy() {
        this.isActive = false;
    }

    /**
     * Đảo ngược vận tốc theo trục X.
     * <p>
     * <b>Định nghĩa:</b> {@code dx = -dx}.
     * <p>
     * <b>Expected:</b> Enemy đổi hướng di chuyển ngang.
     */
    public void reverseDirX() {
        this.dx = -this.dx;
    }

    /**
     * Đảo ngược vận tốc theo trục Y.
     * <p>
     * <b>Định nghĩa:</b> {@code dy = -dy}.
     * <p>
     * <b>Expected:</b> Enemy đổi hướng di chuyển dọc.
     */
    public void reverseDirY() {
        this.dy = -this.dy;
    }

    /**
     * Lấy trạng thái "đã vào màn hình" (hasEnteredScreen).
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của
     * cờ (flag) {@code hasEnteredScreen}.
     * <p>
     * <b>Expected:</b> {@code true} nếu Enemy
     * đã hoàn tất logic "đi vào", ngược lại {@code false}.
     *
     * @return boolean Trạng thái "đã vào màn hình".
     */
    public boolean getHasEnteredScreen() {
        return this.hasEnteredScreen;
    }

    /**
     * Lấy tọa độ X tại tâm của Enemy.
     * <p>
     * <b>Định nghĩa:</b> Tính toán {@code x + width / 2}.
     * <p>
     * <b>Expected:</b> Tọa độ X (double)
     * của điểm giữa Enemy.
     *
     * @return Tọa độ X ở tâm.
     */
    public double getCenterX() {
        return this.x + (this.width / 2);
    }

    /**
     * Lấy tọa độ Y tại tâm của Enemy.
     * <p>
     * <b>Định nghĩa:</b> Tính toán {@code y + height / 2}.
     * <p>
     * <b>Expected:</b> Tọa độ Y (double)
     * của điểm giữa Enemy.
     *
     * @return Tọa độ Y ở tâm.
     */
    public double getCenterY() {
        return this.y + (this.height / 2);
    }

    /**
     * Lấy máu (health) hiện tại của Enemy.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code health}.
     * <p>
     * <b>Expected:</b> Lượng máu (int) còn lại.
     *
     * @return Máu (health).
     */
    @Override
    public int getHealth() {
        return (int) this.health;
    }

    /**
     * Đặt máu (health) cho Enemy.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code health}.
     * <p>
     * <b>Expected:</b> {@code health} được cập nhật.
     *
     * @param health Máu mới.
     */
    @Override
    public void setHealth(int health) {
        this.health = health;
    }
}