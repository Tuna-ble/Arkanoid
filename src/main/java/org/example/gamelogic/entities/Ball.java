package org.example.gamelogic.entities;

import com.sun.glass.ui.SystemClipboard;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.bricks.Brick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Quản lý đối tượng Bóng (Ball) chính trong game.
 * <p>
 * Lớp này kế thừa từ {@link MovableObject} và implement {@link IBall}.
 * Nó xử lý logic di chuyển, va chạm, trạng thái (dính, xuyên thấu),
 * và render (vẽ bóng và vệt mờ - trail).
 */
public class Ball extends MovableObject implements IBall {
    private double radius;
    private double speed;
    private boolean attachedToPaddle;
    private Color currentColor;

    private int pierceLeft;
    private List<GameObject> piercingObjects;

    /**
     * Lớp nội (inner class)
     * lưu trữ "ảnh chụp" (snapshot) vị trí của bóng
     * để vẽ vệt mờ (trail).
     */
    private static class GhostSnapshot {
        double x, y, width, height;

        GhostSnapshot(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private final LinkedList<GhostSnapshot> trail = new LinkedList<>();
    private final int MAX_GHOSTS = 8;
    private double lastGhostX, lastGhostY;

    /**
     * Khởi tạo một đối tượng Ball mới.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí,
     * bán kính ({@code radius}),
     * và trạng thái ban đầu (dính vào paddle,
     * màu sắc mặc định, không xuyên thấu).
     * <p>
     * <b>Expected:</b> Một đối tượng Ball được tạo,
     * {@code attachedToPaddle} là {@code true},
     * và sẵn sàng để được {@code release()}.
     *
     * @param x      Tọa độ X ban đầu.
     * @param y      Tọa độ Y ban đầu.
     * @param radius Bán kính của bóng.
     */
    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2, 0, 0);
        this.radius = radius;
        this.speed = GameConstants.BALL_INITIAL_SPEED;
        this.attachedToPaddle = true;
        this.currentColor = GameConstants.NORMAL_BALL_COLOR;

        this.pierceLeft = 0;
        this.piercingObjects = new ArrayList<GameObject>();

        this.lastGhostX = x;
        this.lastGhostY = y;
    }

    /**
     * Đặt số lần bóng có thể xuyên (pierce) gạch.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code pierceLeft}.
     * <p>
     * <b>Expected:</b> {@code pierceLeft} được cập nhật.
     *
     * @param pierceLeft Số lần xuyên thấu còn lại.
     */
    public void setPierceLeft(int pierceLeft) {
        this.pierceLeft = pierceLeft;
    }

    /**
     * Lấy số lần bóng có thể xuyên (pierce) gạch còn lại.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code pierceLeft}.
     * <p>
     * <b>Expected:</b> Số lần xuyên thấu (int).
     *
     * @return Số lần xuyên thấu.
     */
    public int getPierceLeft() {
        return pierceLeft;
    }

    /**
     * Lấy danh sách các đối tượng mà bóng đang xuyên qua.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code piercingObjects}
     * (dùng để tránh va chạm lặp lại
     * với cùng 1 đối tượng trong 1 frame).
     * <p>
     * <b>Expected:</b> Danh sách (List)
     * các {@link GameObject} đang bị xuyên.
     *
     * @return Danh sách đối tượng đang bị xuyên.
     */
    public List<GameObject> getPiercingObjects() {
        return piercingObjects;
    }

    /**
     * Lấy tọa độ X tại tâm của Bóng.
     * <p>
     * <b>Định nghĩa:</b> Tính toán {@code x + width / 2}.
     * <p>
     * <b>Expected:</b> Tọa độ X (double)
     * của điểm giữa bóng.
     *
     * @return Tọa độ X ở tâm.
     */
    public double getCenterX() {
        return x + width / 2;
    }

    /**
     * Cập nhật logic của bóng (di chuyển, vệt mờ - trail).
     * <p>
     * <b>Định nghĩa:</b> Cập nhật màu sắc (dựa trên trạng thái pierce).
     * Nếu không dính (not attached),
     * di chuyển bóng (x, y) dựa trên vận tốc (dx, dy),
     * đảm bảo vận tốc tối thiểu/tối đa,
     * và cập nhật danh sách {@code trail}
     * (vị trí cũ) để tạo vệt mờ.
     * <p>
     * <b>Expected:</b> Vị trí bóng được cập nhật.
     * Danh sách {@code trail} được cập nhật
     * nếu bóng di chuyển đủ xa.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        if (isActive) {

            currentColor = (pierceLeft > 0 ?
                    GameConstants.PIERCING_BALL_COLOR :
                    GameConstants.NORMAL_BALL_COLOR);

            if (!attachedToPaddle) {
                ensureMinimumVelocity();
                limitMaximumSpeed();
                this.x += dx * deltaTime;
                this.y += dy * deltaTime;

                double distanceMoved = Math.hypot(x - lastGhostX, y - lastGhostY);

                if (distanceMoved > 2.0) {

                    trail.addFirst(new GhostSnapshot(x, y, width, height));
                    this.lastGhostX = x;
                    this.lastGhostY = y;

                    if (trail.size() > MAX_GHOSTS) {
                        trail.removeLast();
                    }
                }

            } else {
                trail.clear();
                this.lastGhostX = x;
                this.lastGhostY = y;
            }
        }
    }

    /**
     * Vẽ (render) bóng và vệt mờ (trail) lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Lặp qua danh sách {@code trail}
     * để vẽ các "bóng ma" (ghosts)
     * với độ mờ (alpha) và kích thước (scale) giảm dần.
     * Vẽ quả bóng chính (hình tròn)
     * bằng {@link RadialGradient} để tạo hiệu ứng 3D
     * và vẽ viền (stroke) cho rõ nét.
     * <p>
     * <b>Expected:</b> Bóng và vệt mờ
     * (nếu có) được vẽ lên {@code gc}.
     * Không vẽ gì nếu {@code isActive} là {@code false}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (isActive) {

            gc.save();
            try {
                for (int i = 0; i < trail.size(); i++) {
                    GhostSnapshot ghost = trail.get(i);

                    double progress = 1.0 - (double) i / MAX_GHOSTS;

                    double opacity = 0.5 * progress;

                    double scale = 0.5 + (0.5 * progress);

                    double ghostWidth = ghost.width * scale;
                    double ghostHeight = ghost.height * scale;

                    double ghostX = ghost.x + (ghost.width - ghostWidth) / 2.0;
                    double ghostY = ghost.y + (ghost.height - ghostHeight) / 2.0;

                    gc.setGlobalAlpha(opacity);
                    gc.setFill(currentColor);
                    gc.fillOval(ghostX, ghostY, ghostWidth, ghostHeight);
                }

            } finally {
                gc.restore();
            }

            RadialGradient gradient = new RadialGradient(
                    0, 0,
                    x + radius, y + radius,
                    radius,
                    false,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.WHITE),
                    new Stop(1, currentColor)
            );

            gc.setFill(gradient);
            gc.fillOval(x, y, radius * 2, radius * 2);

            gc.setStroke(Color.gray(0.2));
            gc.setLineWidth(1);
            gc.strokeOval(x, y, radius * 2, radius * 2);
        }
    }

    /**
     * Thả bóng ra khỏi paddle.
     * <p>
     * <b>Định nghĩa:</b> Nếu bóng đang dính (attached)
     * và hoạt động (active),
     * đặt {@code attachedToPaddle = false}
     * và gán vận tốc (dx, dy) ban đầu
     * dựa trên góc (angle)
     * và tốc độ ({@code speed}) mặc định.
     * <p>
     * <b>Expected:</b> Bóng bắt đầu di chuyển
     * độc lập khỏi paddle.
     */
    public void release() {
        if (attachedToPaddle && isActive) {
            attachedToPaddle = false;
            double angleVariation = Math.toRadians(
                    (Math.random() - 0.5) * 2 * GameConstants.BALL_INITIAL_ANGLE_RANDOM_RANGE
            );
            double baseAngle = Math.toRadians(-75);
            double angle = baseAngle + angleVariation;

            this.dx = speed * Math.cos(angle);
            this.dy = speed * Math.sin(angle);
        }
    }

    /**
     * Đặt lại (reset) trạng thái bóng
     * (thường khi mất mạng).
     * <p>
     * <b>Định nghĩa:</b> Đặt lại vị trí bóng
     * (dính vào paddle mới),
     * đặt lại vận tốc (dx, dy = 0),
     * đặt lại tốc độ (speed),
     * và xóa vệt mờ ({@code trail}).
     * <p>
     * <b>Expected:</b> Bóng quay trở lại
     * trạng thái ban đầu, dính vào paddle.
     *
     * @param paddleX     Tọa độ X của paddle.
     * @param paddleY     Tọa độ Y của paddle.
     * @param paddleWidth Chiều rộng của paddle.
     */
    public void reset(double paddleX, double paddleY, double paddleWidth) {
        this.attachedToPaddle = true;
        this.isActive = true;
        this.x = paddleX + (paddleWidth / 2.0) - (this.width / 2.0);
        this.y = paddleY - this.height;
        this.dx = 0;
        this.dy = 0;
        this.speed = GameConstants.BALL_INITIAL_SPEED;

        this.trail.clear();
        this.lastGhostX = this.x;
        this.lastGhostY = this.y;
    }

    /**
     * Xử lý va chạm với Paddle.
     * <p>
     * <b>Định nghĩa:</b> Đảo ngược vận tốc Y ({@code dy}).
     * Điều chỉnh vận tốc X ({@code dx})
     * dựa trên vận tốc của paddle ({@code paddle.getDx()})
     * và vị trí va chạm ({@code hitPositionRatio}).
     * Tăng tốc độ ({@code speed}) của bóng
     * và cập nhật lại vector vận tốc.
     * <p>
     * <b>Expected:</b> Bóng nảy lên (dy bị đảo ngược),
     * góc nảy (dx) bị ảnh hưởng
     * bởi vị trí va chạm và chuyển động của paddle.
     *
     * @param paddle           Đối tượng Paddle đã va chạm.
     * @param hitPositionRatio Tỷ lệ vị trí va chạm
     * (-1.0 ở mép trái, 1.0 ở mép phải).
     */
    @Override
    public void handlePaddleCollision(Paddle paddle, double hitPositionRatio) {
        if (!isActive() || attachedToPaddle) return;

        double overlapY = (this.y + this.height) - paddle.getY();
        if (overlapY > 0) {
            this.y -= overlapY;
        }

        if (dy > 0) {
            dy = -dy;
        } else if (dy == 0) {
            dy = -GameConstants.BALL_MIN_VY;
        }
        dy = -Math.abs(dy);

        dx += paddle.getDx() * GameConstants.PADDLE_MOVE_INFLUENCE;

        double angleInfluence = speed * hitPositionRatio * 0.1;
        dx += angleInfluence;

        double speedAfterCollision = Math.min(speed + GameConstants.BALL_SPEED_INCREMENT_PER_BRICK, GameConstants.BALL_MAX_SPEED);
        this.speed = speedAfterCollision;
        updateVelocityWithSpeed();
        ensureMinimumVelocity();
        limitMaximumSpeed();
    }

    /**
     * (Deprecated/Demo) Điều chỉnh góc va chạm với paddle.
     * <p>
     * <b>Định nghĩa:</b> Một logic xử lý
     * va chạm paddle cũ (hoặc demo).
     * <p>
     * <b>Expected:</b> (Tương tự {@code handlePaddleCollision}).
     *
     * @param hitPosition Vị trí va chạm.
     * @param paddledx    Vận tốc X của paddle.
     */
    public void adjustAngle(double hitPosition, double paddledx) {
        dx += paddledx * GameConstants.PADDLE_MOVE_INFLUENCE;
        dx += speed * hitPosition * 0.5;

        if (dy > 0) {
            dy = -Math.abs(dy);
        }

        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        double targetSpeed = speed * GameConstants.BALL_RESTITUTION;
        if (currentSpeed > 0) {
            double factor = targetSpeed / currentSpeed;
            dx *= factor;
            dy *= factor;
        }

        speed = Math.min(speed + GameConstants.BALL_SPEED_INCREMENT_PER_BRICK,
                GameConstants.BALL_MAX_SPEED);
    }

    /**
     * Lấy bán kính (radius) của bóng.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code radius}.
     * <p>
     * <b>Expected:</b> Bán kính (double).
     *
     * @return Bán kính.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Lấy tốc độ (speed) cơ sở của bóng.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code speed}.
     * <p>
     * <b>Expected:</b> Tốc độ (double).
     *
     * @return Tốc độ.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Tăng tốc độ (speed) của bóng
     * (thường khi phá gạch).
     * <p>
     * <b>Định nghĩa:</b> Tăng {@code speed}
     * một lượng ({@code BALL_SPEED_INCREMENT_PER_BRICK})
     * và gọi {@code updateVelocityWithSpeed()}.
     * <p>
     * <b>Expected:</b> Tốc độ của bóng tăng lên,
     * giới hạn bởi {@code BALL_MAX_SPEED}.
     */
    public void incrementSpeed() {
        speed = Math.min(speed + GameConstants.BALL_SPEED_INCREMENT_PER_BRICK, GameConstants.BALL_MAX_SPEED);
        updateVelocityWithSpeed();
    }

    /**
     * Nhân tốc độ (speed) của bóng
     * với một hệ số (factor).
     * <p>
     * <b>Định nghĩa:</b> Nhân {@code speed}
     * với {@code factor}
     * (dùng cho power-up Tăng/Giảm tốc).
     * <p>
     * <b>Expected:</b> Tốc độ của bóng thay đổi,
     * giới hạn trong khoảng
     * ({@code BALL_MIN_SPEED}, {@code BALL_MAX_SPEED}).
     *
     * @param factor Hệ số nhân.
     */
    @Override
    public void multiplySpeed(double factor) {
        double targetSpeed = Math.max(GameConstants.BALL_MIN_SPEED, Math.min(this.speed * factor, GameConstants.BALL_MAX_SPEED));
        this.speed = targetSpeed;
        updateVelocityWithSpeed();
    }

    /**
     * Tạo một bản sao (clone) của bóng.
     * <p>
     * <b>Định nghĩa:</b> Implement (ghi đè)
     * phương thức {@code clone()} (từ {@code IBall}).
     * Tạo một bóng mới ở trạng thái mặc định,
     * không dính (not attached).
     * <p>
     * <b>Expected:</b> Một đối tượng {@link IBall} mới
     * (dùng cho Prototype Pattern).
     *
     * @return Một {@code IBall} mới (clone).
     */
    public IBall clone() {
        Ball newBall = new Ball(0, 0, this.width / 2.0);
        newBall.attachedToPaddle = false;
        newBall.isActive = true;
        return newBall;
    }

    /**
     * Tạo một bản sao (duplicate)
     * của bóng với trạng thái hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Tạo một bóng mới
     * và sao chép các trạng thái
     * ({@code pierceLeft}, {@code speed})
     * từ bóng hiện tại.
     * <p>
     * <b>Expected:</b> Một {@link IBall} mới
     * có cùng thuộc tính với bóng gốc
     * (dùng cho power-up MultiBall).
     *
     * @return Một {@code IBall} mới (duplicate).
     */
    public IBall duplicate() {
        Ball newBall = new Ball(0, 0, this.width / 2.0);
        newBall.pierceLeft=pierceLeft;
        for (GameObject pierced : piercingObjects) {
            newBall.getPiercingObjects().add(pierced);
        }
        newBall.speed=speed;
        newBall.attachedToPaddle = false;
        newBall.isActive = true;
        return newBall;
    }

    /**
     * Kiểm tra xem bóng đã bị "hủy" (inactive) hay chưa.
     * <p>
     * <b>Định nghĩa:</b> Trả về trạng thái
     * ngược của {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code true} nếu bóng
     * không hoạt động, ngược lại {@code false}.
     *
     * @return boolean Trạng thái đã bị hủy.
     */
    public boolean isDestroyed() {
        return !isActive;
    }

    /**
     * Đặt vị trí (x, y) của bóng một cách trực tiếp.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code x} và {@code y}.
     * <p>
     * <b>Expected:</b> Vị trí của bóng được thay đổi.
     *
     * @param x Tọa độ X mới.
     * @param y Tọa độ Y mới.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Lấy chính đối tượng GameObject này.
     * <p>
     * <b>Định nghĩa:</b> Trả về tham chiếu {@code this}.
     * <p>
     * <b>Expected:</b> Trả về instance của chính đối tượng này.
     *
     * @return {@code this}.
     */
    public GameObject getGameObject() {
        return this;
    }

    /**
     * Đảo ngược vận tốc theo trục X.
     * <p>
     * <b>Định nghĩa:</b> {@code dx = -dx}.
     * <p>
     * <b>Expected:</b> Bóng đổi hướng di chuyển ngang.
     */
    public void reverseDirX() {
        this.dx = -this.dx;
    }

    /**
     * Đảo ngược vận tốc theo trục Y.
     * <p>
     * <b>Định nghĩa:</b> {@code dy = -dy}.
     * <p>
     * <b>Expected:</b> Bóng đổi hướng di chuyển dọc.
     */
    public void reverseDirY() {
        this.dy = -this.dy;
    }

    /**
     * Hủy (deactivate) bóng.
     * <p>
     * <b>Định nghĩa:</b> Đặt {@code isActive = false}.
     * <p>
     * <b>Expected:</b> Bóng ngừng hoạt động
     * (sẽ bị xóa bởi BallManager).
     */
    public void destroy() {
        this.isActive = false;
    }

    /**
     * Kiểm tra xem bóng có đang dính vào paddle không.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code attachedToPaddle}.
     * <p>
     * <b>Expected:</b> {@code true} nếu đang dính,
     * {@code false} nếu đang di chuyển tự do.
     *
     * @return boolean Trạng thái dính.
     */
    public boolean isAttachedToPaddle() {
        return attachedToPaddle;
    }

    /**
     * (Helper) Đảm bảo bóng không di chuyển quá chậm.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra và đảm bảo
     * vận tốc Y ({@code dy})
     * và tổng tốc độ (speed)
     * luôn lớn hơn hoặc bằng
     * giá trị tối thiểu (MIN_VY, MIN_SPEED).
     * <p>
     * <b>Expected:</b> Vận tốc (dx, dy)
     * được điều chỉnh nếu cần thiết
     * để duy trì tốc độ tối thiểu.
     */
    private void ensureMinimumVelocity() {
        if (Math.abs(dy) < GameConstants.BALL_MIN_VY) {
            dy = Math.copySign(GameConstants.BALL_MIN_VY, dy);
        }

        double currentSpeedSq = dx * dx + dy * dy;
        double minSpeedSq = GameConstants.BALL_MIN_SPEED * GameConstants.BALL_MIN_SPEED;

        if (currentSpeedSq < minSpeedSq) {
            double currentSpeed = Math.sqrt(currentSpeedSq);
            if (currentSpeed > 0) {
                double factor = GameConstants.BALL_MIN_SPEED / currentSpeed;
                dx *= factor;
                dy *= factor;
            } else if (speed > 0) {

                double baseAngle = Math.toRadians(-75);
                dx = speed * Math.cos(baseAngle);
                dy = speed * Math.sin(baseAngle);
            }
        }
    }

    /**
     * (Helper) Giới hạn tốc độ tối đa của bóng.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra nếu tổng tốc độ
     * (currentSpeed) vượt quá {@code BALL_MAX_SPEED}.
     * <p>
     * <b>Expected:</b> Vận tốc (dx, dy)
     * được điều chỉnh (giảm)
     * nếu bóng vượt quá tốc độ tối đa,
     * và {@code speed} được cập nhật.
     */
    private void limitMaximumSpeed() {
        double currentSpeedSq = dx * dx + dy * dy;
        double maxSpeedSq = GameConstants.BALL_MAX_SPEED * GameConstants.BALL_MAX_SPEED;

        if (currentSpeedSq > maxSpeedSq) {
            double currentSpeed = Math.sqrt(currentSpeedSq);
            double factor = GameConstants.BALL_MAX_SPEED / currentSpeed;
            dx *= factor;
            dy *= factor;
            speed = GameConstants.BALL_MAX_SPEED;
        }
    }

    /**
     * (Helper) Cập nhật vector vận tốc (dx, dy)
     * để khớp với tốc độ ({@code speed}) cơ sở.
     * <p>
     * <b>Định nghĩa:</b> Tính toán lại (normalize và scale)
     * {@code dx} và {@code dy}
     * dựa trên giá trị {@code speed} hiện tại.
     * <p>
     * <b>Expected:</b> Hướng di chuyển (vector) được giữ nguyên,
     * nhưng độ lớn (magnitude) của vector
     * bằng với {@code speed}.
     */
    private void updateVelocityWithSpeed() {
        if (!attachedToPaddle) {
            double currentSpeed = Math.sqrt(dx * dx + dy * dy);
            if (currentSpeed > 0) {
                double factor = this.speed / currentSpeed;
                dx *= factor;
                dy *= factor;
            } else if (speed > 0) {

            }
        }
    }
}