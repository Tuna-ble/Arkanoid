package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.example.config.GameConstants;

public class Ball extends MovableObject implements IBall {
    private double radius;
    private double speed;
    private boolean attachedToPaddle;

    public Ball(double x, double y, double radius) {
        super(x, y, radius * 2, radius * 2, 0, 0);
        this.radius = radius;
        this.speed = GameConstants.BALL_INITIAL_SPEED;
        this.attachedToPaddle = true;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    // update bóng theo delta
    @Override
    public void update(double deltaTime) {
        if (isActive) { // bóng rời paddle

            if (!attachedToPaddle) {
                // Logic di chuyển tự do
                ensureMinimumVelocity();
                limitMaximumSpeed();
                this.x += dx * deltaTime;
                this.y += dy * deltaTime;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isActive) {
            // Tạo gradient nhẹ để bóng có cảm giác 3D
            RadialGradient gradient = new RadialGradient(
                    0, 0,                // focus angle, focus distance
                    x + radius, y + radius, // tâm gradient
                    radius,               // bán kính gradient
                    false,                // proportional = false -> dùng pixel
                    CycleMethod.NO_CYCLE,
                    new Stop(0, Color.WHITE),          // vùng sáng
                    new Stop(1, GameConstants.BALL_COLOR) // vùng tối
            );

            // tô hình tròn bằng gradient
            gc.setFill(gradient);
            gc.fillOval(x, y, radius * 2, radius * 2);

            // vẽ viền bóng (cho rõ nét)
            gc.setStroke(Color.gray(0.2));
            gc.setLineWidth(1);
            gc.strokeOval(x, y, radius * 2, radius * 2);
        }
    }

    // bắn bóng ra khỏi paddle
    public void release() {
        if (attachedToPaddle && isActive) {
            attachedToPaddle = false;
            // tạo độ lệch nhỏ so với baseAngle (75 độ)
            double angleVariation = Math.toRadians(
                    (Math.random() - 0.5) * 2 * GameConstants.BALL_INITIAL_ANGLE_RANDOM_RANGE
            );
            double baseAngle = Math.toRadians(-75);
            double angle = baseAngle + angleVariation;

            // tính toán vận tốc ban đầu
            this.dx = speed * Math.cos(angle);
            this.dy = speed * Math.sin(angle);
        }
    }

    public void reset(double paddleX, double paddleY, double paddleWidth) {
        this.attachedToPaddle = true;
        this.isActive = true;
        this.x = paddleX + (paddleWidth / 2.0) - (this.width / 2.0);
        this.y = paddleY - this.height;
        this.dx = 0;
        this.dy = 0;
        this.speed = GameConstants.BALL_INITIAL_SPEED;
    }

    @Override
    public void handlePaddleCollision(Paddle paddle, double hitPositionRatio) {
        if (!isActive() || attachedToPaddle) return;

        // 1. Đẩy bóng lên trên paddle (vẫn cần thiết để tránh kẹt)
        //    Bạn có thể làm nhẹ nhàng hơn: chỉ đẩy lên nếu bóng đã lún vào
        double overlapY = (this.y + this.height) - paddle.getY();
        if (overlapY > 0) {
            this.y -= overlapY; // Chỉ đẩy lên đúng bằng độ lún
        }
        // Hoặc giữ cách cũ nếu đơn giản:
        // this.y = paddle.getY() - this.height;

        // 2. Phản xạ: Đảo ngược thành phần Y
        if (dy > 0) { // Chỉ đảo ngược nếu đang đi xuống
            dy = -dy;
        } else if (dy == 0) { // Xử lý trường hợp bóng đi ngang
            dy = -GameConstants.BALL_MIN_VY; // Đẩy nhẹ lên
        }
        dy = -Math.abs(dy); // Đảm bảo dy luôn âm (đi lên)

        // 3. (TÙY CHỌN) Ảnh hưởng của chuyển động Paddle lên dx:
        //    Thêm một phần nhỏ vận tốc của paddle vào dx của bóng
        //    để tạo hiệu ứng "đẩy" bóng sang trái/phải khi paddle di chuyển.
        dx += paddle.getDx() * GameConstants.PADDLE_MOVE_INFLUENCE; // PADDLE_MOVE_INFLUENCE là hệ số nhỏ, ví dụ 0.2

        // 4. (TÙY CHỌN) Điều chỉnh dx nhẹ dựa trên vị trí va chạm:
        //    Làm bóng hơi lệch sang trái/phải nếu chạm vào rìa paddle.
        //    Dùng hitPositionRatio nhưng với ảnh hưởng nhỏ hơn nhiều so với cách cũ.
        double angleInfluence = speed * hitPositionRatio * 0.1; // Hệ số nhỏ, ví dụ 0.1
        dx += angleInfluence;

        // 5. Cập nhật tốc độ (nếu muốn tăng tốc sau va chạm) và giới hạn
        double speedAfterCollision = Math.min(speed + GameConstants.BALL_SPEED_INCREMENT_PER_BRICK, GameConstants.BALL_MAX_SPEED);
        this.speed = speedAfterCollision;
        updateVelocityWithSpeed(); // Điều chỉnh dx, dy để khớp với speed mới
        ensureMinimumVelocity(); // Đảm bảo không quá chậm
        limitMaximumSpeed(); // Đảm bảo không quá nhanh
    }

    /**
     * demo va chạm với paddle
     */
    public void adjustAngle(double hitPosition, double paddledx) {
        dx += paddledx * GameConstants.PADDLE_MOVE_INFLUENCE;
        dx += speed * hitPosition * 0.5;

        if (dy > 0) { dy = -Math.abs(dy); }

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

    // getter
    public double getRadius() { return radius; }
    public double getSpeed() { return speed; }
    
    public void incrementSpeed() {
        speed = Math.min(speed + GameConstants.BALL_SPEED_INCREMENT_PER_BRICK, GameConstants.BALL_MAX_SPEED);
        // Cập nhật lại dx, dy để phản ánh tốc độ mới
        updateVelocityWithSpeed();
    }

    public void multiplySpeed(double factor) {
        // Giữ tốc độ trong giới hạn min/max
        double targetSpeed = Math.max(GameConstants.BALL_MIN_SPEED, Math.min(this.speed * factor, GameConstants.BALL_MAX_SPEED));
        this.speed = targetSpeed;
        // Cập nhật lại dx, dy
        updateVelocityWithSpeed();
    }
    
    public IBall clone() {
        Ball newBall = new Ball(0, 0, this.width / 2.0);
        newBall.attachedToPaddle = false;
        newBall.isActive = true;
        return newBall;
    }

    public boolean isDestroyed() {
        return !isActive;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public GameObject getGameObject() {
        return this;
    }

    public void reverseDirX() {
        this.dx = -this.dx;
    }

    public void reverseDirY() {
        this.dy = -this.dy;
    }

    public void destroy() {
        this.isActive = false;
    }

    public boolean isAttachedToPaddle() {
        return attachedToPaddle;
    }

    private void ensureMinimumVelocity() {
        // Đảm bảo dy không quá gần 0
        if (Math.abs(dy) < GameConstants.BALL_MIN_VY) {
            // Giữ nguyên dấu của dy, nhưng đặt giá trị tuyệt đối bằng mức tối thiểu
            dy = Math.copySign(GameConstants.BALL_MIN_VY, dy);
        }

        // Đảm bảo tốc độ tổng thể không dưới mức tối thiểu
        double currentSpeedSq = dx * dx + dy * dy; // Tính bình phương tốc độ để tránh căn bậc hai
        double minSpeedSq = GameConstants.BALL_MIN_SPEED * GameConstants.BALL_MIN_SPEED;

        if (currentSpeedSq < minSpeedSq) {
            double currentSpeed = Math.sqrt(currentSpeedSq);
            if (currentSpeed > 0) { // Tránh chia cho 0
                // Tăng tỷ lệ dx và dy để đạt tốc độ tối thiểu
                double factor = GameConstants.BALL_MIN_SPEED / currentSpeed;
                dx *= factor;
                dy *= factor;
            } else if (speed > 0){ // Nếu đang đứng yên nhưng speed > 0 (ví dụ sau reset)
                // Có thể đặt lại một vận tốc ngẫu nhiên nhỏ hoặc theo hướng mặc định
                // Ví dụ: Đặt lại theo góc -75 độ
                double baseAngle = Math.toRadians(-75);
                dx = speed * Math.cos(baseAngle);
                dy = speed * Math.sin(baseAngle);
            }
        }
    }
    private void limitMaximumSpeed() {
        double currentSpeedSq = dx * dx + dy * dy;
        double maxSpeedSq = GameConstants.BALL_MAX_SPEED * GameConstants.BALL_MAX_SPEED;

        if (currentSpeedSq > maxSpeedSq) {
            double currentSpeed = Math.sqrt(currentSpeedSq);
            // Giảm tỷ lệ dx và dy để đưa về tốc độ tối đa
            double factor = GameConstants.BALL_MAX_SPEED / currentSpeed;
            dx *= factor;
            dy *= factor;
            // Cập nhật lại biến speed nội bộ cho nhất quán
            speed = GameConstants.BALL_MAX_SPEED;
        }
    }
    private void updateVelocityWithSpeed() {
        if (!attachedToPaddle) {
            double currentSpeed = Math.sqrt(dx * dx + dy * dy);
            if (currentSpeed > 0) {
                double factor = this.speed / currentSpeed;
                dx *= factor;
                dy *= factor;
            } else if (speed > 0) {
                // Nếu đang đứng yên (ví dụ sau reset) và speed > 0, tính lại dx/dy
                // Có thể cần logic khác ở đây tùy thuộc vào cách bạn muốn xử lý
            }
        }
    }
}