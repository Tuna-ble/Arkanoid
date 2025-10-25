package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.bricks.ExplosiveBrick;

public class Ball extends MovableObject implements IBall {
    private double radius;
    private double speed;
    private boolean attachedToPaddle;

    public Ball(double x, double y, double radius, double dx, double dy) {
        super(x, y, radius * 2, radius * 2, dx, dy);
        this.radius = radius;
        this.speed = GameConstants.BALL_INITIAL_SPEED;
        this.attachedToPaddle=true;
        this.isActive=true;
    }

    // update bóng theo delta
    @Override
    public void update(double deltaTime) {
        if (!attachedToPaddle && isActive) { // bóng rời paddle

            // đảm bảo vận tốc theo trục Y đủ lớn -> bóng nảy lên xuống
            if (Math.abs(dy) < GameConstants.BALL_MIN_VY) {
                dy = dy > 0 ? GameConstants.BALL_MIN_VY : -GameConstants.BALL_MIN_VY;
            }

            // tính speed và đảm bảo trong khoảng [min, max] nếu có cơ chế tăng tốc
            double currentSpeed = Math.sqrt(dx * dx + dy * dy);
            if (currentSpeed < GameConstants.BALL_MIN_SPEED) {
                double factor = GameConstants.BALL_MIN_SPEED / currentSpeed;
                dx *= factor;
                dy *= factor;
            } else if (currentSpeed > GameConstants.BALL_MAX_SPEED) {
                double factor = GameConstants.BALL_MAX_SPEED / currentSpeed;
                dx *= factor;
                dy *= factor;
            }

            // update theo vận tốc
            this.x += dx * deltaTime;
            this.y += dy * deltaTime;
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

    // tăng tốc nhẹ
    public void incrementSpeed() {
        speed = Math.min(speed + GameConstants.BALL_SPEED_INCREMENT_PER_BRICK,
                GameConstants.BALL_MAX_SPEED);

        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        if (currentSpeed > 0) {
            double factor = speed / currentSpeed;
            dx *= factor;
            dy *= factor;
        }
    }

    // getter
    public double getRadius() { return radius; }
    public double getSpeed() { return speed; }

    // setter vận tốc và đảm bảo trong khoảng [min, max]
    public void setSpeed(double speed) {
        this.speed = Math.max(GameConstants.BALL_MIN_SPEED,
                Math.min(speed, GameConstants.BALL_MAX_SPEED));

        if (!isActive) {
            double currentSpeed = Math.sqrt(dx * dx + dy * dy);
            if (currentSpeed > 0) {
                double factor = this.speed / currentSpeed;
                dx *= factor;
                dy *= factor;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {

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

    public IBall clone() {
        return new Ball(0.0, 0.0, this.radius, this.dx, this.dy);
    }

    public boolean isDestroyed() {
        return !isActive;
    }

    public boolean isAttachedToPaddle() {
        return attachedToPaddle;
    }

    public void destroy() {
        isActive = false;
        attachedToPaddle=false;
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

    public void reset(double paddleX, double paddleY, double paddleWidth) {
        this.attachedToPaddle = true;
        this.isActive = true;
        this.x = paddleX + (paddleWidth / 2.0) - (this.width / 2.0);
        this.y = paddleY - this.height;
        this.dx = 0;
        this.dy = 0;
        this.speed = GameConstants.BALL_INITIAL_SPEED;
    }

    public void handlePaddleCollision(Paddle paddle, double hitPositionRatio) {
        if (!isActive || attachedToPaddle) return;

        this.y = paddle.getY() - this.height;
        if (dy > 0) {
            dy = -dy;
        }

        double maxAngleChange = Math.toRadians(60);
        double angleOffset = maxAngleChange * hitPositionRatio;
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        if (currentSpeed == 0) currentSpeed = speed;

        double newAngle = Math.PI / 2.0 + angleOffset;

        dx = currentSpeed * Math.cos(newAngle);
        dy = -currentSpeed * Math.sin(newAngle);

        incrementSpeed();
    }
}
