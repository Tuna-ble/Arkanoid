package org.example.gamelogic.entities.bricks;

import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.events.BrickDamagedEvent;
import org.example.gamelogic.events.BallHitBrickEvent;

/**
 * Lớp cơ sở (abstract class) cho tất cả các đối tượng Gạch (Brick).
 * <p>
 * Lớp này kế thừa từ {@link GameObject} và implement {@link Brick}.
 * Nó quản lý logic chung cho gạch, bao gồm:
 * đăng ký sự kiện va chạm (từ bóng và đạn),
 * trạng thái bị phá hủy (isDestroyed),
 * và kiểm tra phạm vi (withinRangeOf) cho các sự kiện nổ.
 */
public abstract class AbstractBrick extends GameObject implements Brick {

    protected int health;
    private int id;

    /**
     * Khởi tạo một Gạch (AbstractBrick) cơ sở.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của {@link GameObject}
     * và gọi {@link #subscribeToBrickEvents()}
     * để đăng ký lắng nghe sự kiện va chạm.
     * <p>
     * <b>Expected:</b> Đối tượng gạch được tạo,
     * có vị trí/kích thước, và sẵn sàng nhận sự kiện.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     */
    public AbstractBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        subscribeToBrickEvents();
    }

    /**
     * (Helper) Đăng ký các hàm xử lý sự kiện (event handler)
     * của Gạch này vào {@link EventManager}.
     * <p>
     * <b>Định nghĩa:</b> Đăng ký `onHit` (cho va chạm bóng)
     * và `onDamaged` (cho sát thương từ đạn/nổ).
     * <p>
     * <b>Expected:</b> Instance Brick này sẽ tự động phản ứng
     * khi có va chạm hoặc sát thương từ các nguồn khác nhau.
     */
    private void subscribeToBrickEvents() {
        EventManager.getInstance().subscribe(
                BallHitBrickEvent.class,
                this::onHit
        );
        EventManager.getInstance().subscribe(
                BrickDamagedEvent.class,
                this::onDamaged
        );
    }

    /**
     * (Event Handler) Xử lý khi nhận sự kiện {@link BallHitBrickEvent}.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra nếu sự kiện
     * dành cho gạch này và gạch chưa bị phá hủy,
     * gọi {@link #takeDamage(double)} với sát thương từ bóng.
     * <p>
     * <b>Expected:</b> Gạch nhận sát thương
     * nếu là mục tiêu của sự kiện va chạm bóng.
     *
     * @param event Sự kiện va chạm bóng (BallHitBrickEvent).
     */
    private void onHit(BallHitBrickEvent event) {
        if (event.getBrick() == this && !isDestroyed()) {
            takeDamage(GameConstants.BALL_DAMAGE);
        }
    }

    /**
     * (Event Handler) Xử lý khi nhận sự kiện {@link BrickDamagedEvent}.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra nếu sự kiện dành cho gạch này,
     * và phân loại nguồn sát thương ({@code ExplosiveBrick}, {@code LaserBullet})
     * để gọi {@link #takeDamage(double)} với lượng sát thương tương ứng.
     * <p>
     * <b>Expected:</b> Gạch nhận sát thương
     * từ các nguồn không phải là bóng.
     *
     * @param event Sự kiện sát thương (BrickDamagedEvent).
     */
    private void onDamaged(BrickDamagedEvent event) {
        if (event.getDamagedBrick()==this && !isDestroyed()) {
            GameObject damageSource=event.getDamageSource();
            if (damageSource instanceof ExplosiveBrick) {
                takeDamage(GameConstants.EXPLOSIVE_BRICK_DAMAGE);
            }
            else if (damageSource instanceof LaserBullet) {
                takeDamage(GameConstants.LASER_BULLET_DAMAGE);
            }
        }
    }

    /**
     * Kiểm tra xem gạch đã bị phá hủy hay chưa.
     * <p>
     * <b>Định nghĩa:</b> Trả về trạng thái ngược của {@code isActive()} (từ lớp cha).
     * <p>
     * <b>Expected:</b> {@code true} nếu gạch không hoạt động, ngược lại {@code false}.
     *
     * @return boolean Trạng thái đã bị hủy.
     */
    @Override
    public boolean isDestroyed() {
        return !this.isActive();
    }

    /**
     * Kiểm tra xem gạch có thể bị phá vỡ hay không.
     * <p>
     * <b>Định nghĩa:</b> Phương thức mặc định trả về {@code true}.
     * Các lớp con (như UnbreakableBrick) sẽ ghi đè (override)
     * để thay đổi hành vi này.
     * <p>
     * <b>Expected:</b> {@code true}.
     *
     * @return boolean True nếu gạch có thể bị phá hủy.
     */
    @Override
    public boolean isBreakable() {
        return true;
    }

    /**
     * (Abstract - Prototype Pattern)
     * Tạo một bản sao (clone) của đối tượng Brick này.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con phải implement để hỗ trợ Factory Pattern.
     * <p>
     * <b>Expected:</b> Lớp con sẽ trả về một instance mới của chính nó.
     *
     * @return Một đối tượng {@link Brick} mới (clone).
     */
    @Override
    public abstract Brick clone();

    /**
     * Kiểm tra xem gạch này có nằm trong phạm vi (range) của một gạch khác không.
     * <p>
     * <b>Định nghĩa:</b> Thực hiện kiểm tra phạm vi mở rộng
     * (lớn hơn kích thước gạch + padding)
     * để xác định các gạch lân cận cho sự kiện nổ.
     * <p>
     * <b>Expected:</b> {@code true} nếu gạch {@code other}
     * nằm đủ gần gạch này.
     *
     * @param other Gạch khác cần kiểm tra.
     * @return boolean True nếu gạch nằm trong phạm vi.
     */
    @Override
    public boolean withinRangeOf(Brick other) {
        if (other==null) {
            return false;
        }
        return this.x - GameConstants.BRICK_WIDTH - GameConstants.PADDING - 1 < other.getX() &&
                this.x + GameConstants.BRICK_WIDTH + GameConstants.PADDING + 1 > other.getX() &&
                this.y - GameConstants.BRICK_HEIGHT - GameConstants.PADDING - 1 < other.getY() &&
                this.y + GameConstants.BRICK_HEIGHT + GameConstants.PADDING + 1 > other.getY();
    }

    /**
     * Đặt vị trí (x, y) của gạch.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code x} và {@code y}.
     * <p>
     * <b>Expected:</b> Vị trí của gạch được thay đổi.
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
     * Lấy tọa độ X hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code x}.
     * <p>
     * <b>Expected:</b> Tọa độ X (double).
     *
     * @return Tọa độ X.
     */
    public double getX() {
        return x;
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
    public double getY() {
        return y;
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
    public double getHeight() {
        return height;
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
     * Lấy ID duy nhất của gạch.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code id}.
     * <p>
     * <b>Expected:</b> ID (int) của gạch.
     *
     * @return ID của gạch.
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Đặt ID duy nhất cho gạch.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code id}.
     * <p>
     * <b>Expected:</b> {@code id} được cập nhật.
     *
     * @param id ID mới.
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Lấy máu (health) hiện tại của gạch.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code health}.
     * <p>
     * <b>Expected:</b> Lượng máu (int) còn lại.
     *
     * @return Máu (health).
     */
    @Override
    public int getHealth() {
        return this.health;
    }

    /**
     * Đặt máu (health) cho gạch.
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

    /**
     * Đặt trạng thái bị phá hủy (destroyed) cho gạch.
     * <p>
     * <b>Định nghĩa:</b> Đặt {@code isActive} (từ lớp cha)
     * thành trạng thái ngược của {@code destroyed}.
     * <p>
     * <b>Expected:</b> Gạch bị vô hiệu hóa
     * (nếu {@code destroyed = true}).
     *
     * @param destroyed True nếu gạch đã bị phá hủy.
     */
    @Override
    public void setDestroyed(boolean destroyed) {
        this.isActive = !destroyed;
    }
}