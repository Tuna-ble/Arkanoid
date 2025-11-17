package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.BulletType;
import org.example.gamelogic.strategy.bossbehavior.*;
import org.example.gamelogic.strategy.movement.StaticMovementStrategy;
import org.example.presentation.SpriteAnimation;

/**
 * Quản lý đối tượng Boss (trùm) cuối màn.
 * <p>
 * Lớp này sử dụng (State Pattern)
 * {@link BossBehaviorStrategy}
 * và một máy trạng thái animation ({@code AnimState})
 * để điều khiển các hành vi, đòn tấn công,
 * và các giai đoạn (phase) phức tạp.
 */
public class Boss extends AbstractEnemy {
    private BossBehaviorStrategy currentStrategy;
    private double startX;
    private Image image;

    private enum AnimState {
        IDLE,
        PREPARING_TO_SHOOT,
        HIT_REACTION
    }

    private AnimState animState = AnimState.IDLE;

    private Image idle, enraged;
    private SpriteAnimation shootAnimP1, shootAnimP2;
    private SpriteAnimation hitAnimP1, hitAnimP2;

    private BulletType bulletToFire_Type;
    private double bulletToFire_velX, bulletToFire_velY;

    /**
     * Khởi tạo đối tượng Boss.
     * <p>
     * <b>Định nghĩa:</b> Gọi `super` với
     * {@link StaticMovementStrategy} (vì Boss tự quản lý di chuyển).
     * Tải tất cả các tài nguyên (ảnh, animation)
     * cho các trạng thái (idle, enraged, hit, shoot).
     * Đặt chiến lược (strategy) ban đầu là {@link BossEntryStrategy}.
     * <p>
     * <b>Expected:</b> Đối tượng Boss được tạo,
     * máu (health) và điểm (score) được đặt.
     * Tất cả animation đã được tải.
     * Boss bắt đầu ở trạng thái "đi vào" (Entry).
     *
     * @param x  Tọa độ X ban đầu.
     * @param y  Tọa độ Y ban đầu.
     * @param dx Vận tốc X (không dùng,
     * do strategy quản lý).
     * @param dy Vận tốc Y (không dùng,
     * do strategy quản lý).
     */
    public Boss(double x, double y, double dx, double dy) {
        super(x, y, GameConstants.BOSS_WIDTH, GameConstants.BOSS_HEIGHT,
                dx, dy, new StaticMovementStrategy());

        this.health = GameConstants.BOSS_HEALTH;
        this.scoreValue = 1000;
        this.startX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH / 2 - GameConstants.BOSS_WIDTH / 2;
        this.image = AssetManager.getInstance().getImage("boss");

        AssetManager am = AssetManager.getInstance();

        this.idle = am.getImage("boss");
        this.enraged = am.getImage("bossEnraged");

        this.shootAnimP1 = new SpriteAnimation(am.getImage("bossShoot"), 3, 3, 0.5, false);
        this.shootAnimP2 = new SpriteAnimation(am.getImage("bossEnragedShoot"), 3, 3, 0.5, false);

        this.hitAnimP1 = new SpriteAnimation(am.getImage("bossHit"), 8, 8, 0.3, false);
        this.hitAnimP2 = new SpriteAnimation(am.getImage("bossEnragedHit"), 8, 8, 0.3, false);

        this.currentStrategy = new BossEntryStrategy();
    }

    /**
     * Cập nhật logic chính của Boss.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code super.update()}
     * (để xử lý animation nổ).
     * Ủy quyền logic hành vi cho {@code currentStrategy}.
     * Quản lý máy trạng thái animation ({@code animState})
     * để chạy animation (shoot, hit) và
     * gọi {@link #fireBullet()} khi animation bắn xong.
     * Kiểm tra {@link #checkPhaseChange()} (chuyển giai đoạn).
     * <p>
     * <b>Expected:</b> Trạng thái, vị trí,
     * và animation của Boss được cập nhật.
     * Đạn được bắn (nếu có yêu cầu)
     * sau khi animation bắn kết thúc.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (currentStrategy instanceof BossDyingStrategy) {
            currentStrategy.update(this, deltaTime);
            return;
        }

        if (animState == AnimState.IDLE && currentStrategy != null) {
            currentStrategy.update(this, deltaTime);
        }

        if (animState == AnimState.PREPARING_TO_SHOOT) {
            SpriteAnimation anim = isPhase2() ? shootAnimP2 : shootAnimP1;
            if (anim != null) {
                anim.update(deltaTime);
                if (anim.isFinished()) {
                    fireBullet();
                    animState = AnimState.IDLE;
                }
            } else {
                fireBullet();
                animState = AnimState.IDLE;
            }
        } else if (animState == AnimState.HIT_REACTION) {
            SpriteAnimation anim = isPhase2() ? hitAnimP2 : hitAnimP1;
            if (anim != null) {
                anim.update(deltaTime);
                if (anim.isFinished()) {
                    animState = AnimState.IDLE;
                }
            } else {
                animState = AnimState.IDLE;
            }
        }

        if (!(currentStrategy instanceof BossDyingStrategy)) {
            checkPhaseChange();
        }
    }

    /**
     * (Helper) Kiểm tra và chuyển giai đoạn (Phase) của Boss.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra máu (health) của Boss.
     * <p>
     * <b>Expected:</b> Nếu máu <= 50%
     * và Boss không ở trong giai đoạn 2
     * (hoặc đang chuyển giai đoạn),
     * đặt chiến lược (strategy) thành {@link BossEnrageStrategy}.
     */
    private void checkPhaseChange() {
        if (currentStrategy instanceof BossEntryStrategy) {
            return;
        }
        if (this.health <= GameConstants.BOSS_HEALTH / 2.0
                && !(currentStrategy instanceof BossEnrageStrategy || currentStrategy instanceof BossPhase2Strategy)) {
            setStrategy(new BossEnrageStrategy(startX));
        }
    }

    /**
     * Đặt chiến lược (strategy) hành vi mới cho Boss.
     * <p>
     * <b>Định nghĩa:</b> Gán một
     * {@link BossBehaviorStrategy}
     * mới cho Boss.
     * <p>
     * <b>Expected:</b> {@code currentStrategy}
     * được cập nhật thành {@code newStrategy}.
     *
     * @param newStrategy Chiến lược hành vi mới.
     */
    public void setStrategy(BossBehaviorStrategy newStrategy) {
        this.currentStrategy = newStrategy;
    }

    /**
     * Vẽ (render) Boss và thanh máu lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ (render) Boss dựa trên trạng thái.
     * Ưu tiên vẽ animation (nếu đang
     * {@code PREPARING_TO_SHOOT} hoặc {@code HIT_REACTION}).
     * Nếu không, vẽ ảnh tĩnh ({@code idle} hoặc {@code enraged})
     * dựa trên {@link #isPhase2()}.
     * Vẽ thanh máu (health bar) ở trên cùng.
     * <p>
     * <b>Expected:</b> Hình ảnh/animation
     * của Boss và thanh máu được vẽ lên {@code gc}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (currentStrategy instanceof BossDyingStrategy) {
            gc.drawImage(enraged, x, y, width, height);
            return;
        }

        if (animState == AnimState.PREPARING_TO_SHOOT) {
            SpriteAnimation anim = isPhase2() ? shootAnimP2 : shootAnimP1;
            if (anim != null) anim.render(gc, x, y, width, height);

        } else if (animState == AnimState.HIT_REACTION) {
            SpriteAnimation anim = isPhase2() ? hitAnimP2 : hitAnimP1;
            if (anim != null) anim.render(gc, x, y, width, height);

        } else {
            Image idleImg = isPhase2() ? enraged : idle;
            gc.drawImage(idleImg, x, y, width, height);
        }

        gc.setFill(Color.BLACK);
        gc.fillRect(x, y - 10, width, 8);
        gc.setFill(Color.GREEN);
        gc.fillRect(x, y - 10, width * (this.health / GameConstants.BOSS_HEALTH), 8);
    }

    /**
     * Tạo một bản sao (clone) của đối tượng Boss.
     * <p>
     * <b>Định nghĩa:</b> (Prototype Pattern)
     * Tạo một instance {@code Boss} mới
     * với vận tốc (dx, dy) ban đầu.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Enemy} mới
     * (là instance của {@code Boss})
     * với vị trí (0,0).
     *
     * @return Một bản sao (clone) của Boss.
     */
    @Override
    public Enemy clone() {
        return new Boss(0, 0, this.dx, this.dy);
    }

    /**
     * Xử lý khi Boss nhận sát thương.
     * <p>
     * <b>Định nghĩa:</b> Giảm {@code health}.
     * Đặt {@code animState} thành {@code HIT_REACTION}
     * và reset animation "hit".
     * Chặn sát thương nếu đang trong trạng thái "hit"
     * hoặc "dying".
     * <p>
     * <b>Expected:</b> Máu (health) giảm.
     * Boss chạy animation "hit".
     * Nếu máu <= 0, chiến lược (strategy)
     * được đổi thành {@link BossDyingStrategy}.
     *
     * @param damage Lượng sát thương nhận.
     */
    @Override
    public void takeDamage(double damage) {
        if (isDestroyed() || animState == AnimState.HIT_REACTION || currentStrategy instanceof BossDyingStrategy) {
            return;
        }

        health -= damage;

        if (health <= 0) {
            setStrategy(new BossDyingStrategy());
        } else {
            this.animState = AnimState.HIT_REACTION;

            SpriteAnimation currentHitAnim = (currentStrategy instanceof BossPhase1Strategy) ? hitAnimP1 : hitAnimP2;
            if (currentHitAnim != null) {
                currentHitAnim.reset();
            }
        }
    }

    /**
     * Xử lý logic khi "đi vào" màn hình (ghi đè, để trống).
     * <p>
     * <b>Định nghĩa:</b> Phương thức này được cố tình để trống.
     * Logic "đi vào" (entry) được xử lý
     * bởi {@link BossEntryStrategy}.
     * <p>
     * <b>Expected:</b> Không có hành động gì xảy ra.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void handleEntry(double deltaTime) {

    }

    /**
     * (Helper) Bắn đạn (được gọi sau khi animation bắn kết thúc).
     * <p>
     * <b>Định nghĩa:</b> Được gọi bởi {@code update}
     * sau khi animation bắn (shootAnim) hoàn tất.
     * Sử dụng {@link LaserManager} để tạo viên đạn
     * đã được yêu cầu (lưu trong {@code bulletToFire_...}).
     * <p>
     * <b>Expected:</b> Một viên đạn mới được tạo
     * và bắn ra từ Boss.
     */
    private void fireBullet() {
        double x = this.getX() + this.getWidth() / 2 - 2;
        double y = this.getY() + this.getHeight() / 2 + 20;

        LaserManager.getInstance().createBullet(x, y,
                bulletToFire_velX, bulletToFire_velY,
                bulletToFire_Type, BulletFrom.ENEMY
        );
    }

    /**
     * Nhận "yêu cầu" bắn đạn từ một chiến lược (Strategy).
     * <p>
     * <b>Định nghĩa:</b> Nếu Boss đang {@code IDLE},
     * chuyển {@code animState} thành {@code PREPARING_TO_SHOOT}.
     * Lưu trữ thông tin (vận tốc, loại) của viên đạn
     * và reset/bắt đầu chạy animation bắn.
     * <p>
     * <b>Expected:</b> {@code animState}
     * chuyển thành {@code PREPARING_TO_SHOOT}.
     * Dữ liệu đạn được lưu trữ tạm thời.
     * Animation bắn bắt đầu chạy.
     *
     * @param velX Vận tốc X của đạn.
     * @param velY Vận tốc Y của đạn.
     * @param type Loại đạn ({@link BulletType}) cần bắn.
     */
    public void requestShoot(double velX, double velY, BulletType type) {
        if (animState == AnimState.IDLE) {
            this.animState = AnimState.PREPARING_TO_SHOOT;

            this.bulletToFire_velX = velX;
            this.bulletToFire_velY = velY;
            this.bulletToFire_Type = type;

            SpriteAnimation currentShootAnim = isPhase2() ? shootAnimP2 : shootAnimP1;
            if (currentShootAnim != null) {
                currentShootAnim.reset();
            }
        }
    }

    /**
     * (Helper) Kiểm tra xem Boss có đang ở Giai đoạn 2 (Phase 2) không.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra xem {@code currentStrategy}
     * có phải là {@code BossPhase2Strategy}
     * hoặc {@code BossEnrageStrategy} hay không.
     * <p>
     * <b>Expected:</b> {@code true} nếu Boss
     * đang ở giai đoạn 2, ngược lại {@code false}.
     *
     * @return boolean Trạng thái Giai đoạn 2.
     */
    private boolean isPhase2() {
        return (currentStrategy instanceof BossPhase2Strategy ||
                currentStrategy instanceof BossEnrageStrategy);
    }

    /**
     * Lấy chiến lược (strategy) hành vi hiện tại của Boss.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code currentStrategy}.
     * <p>
     * <b>Expected:</b> Trả về instance của
     * {@link BossBehaviorStrategy} hiện tại.
     *
     * @return Chiến lược hành vi hiện tại.
     */
    public BossBehaviorStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    /**
     * Lấy loại (type) của Enemy.
     * <p>
     * <b>Định nghĩa:</b> Trả về mã định danh (key)
     * của Enemy này.
     * <p>
     * <b>Expected:</b> Trả về chuỗi (String) "BOSS".
     *
     * @return Mã loại "BOSS".
     */
    @Override
    public String getType() {
        return "BOSS";
    }
}