package org.example.business.entities;

import org.example.data.GameConstants;

public class Ball extends MovableObject {
    private double radius;
    private double speed;
    private boolean stuck;

    public Ball() {
        super(GameConstants.BALL_INITIAL_X,
                GameConstants.BALL_INITIAL_Y,
                GameConstants.BALL_RADIUS * 2,
                GameConstants.BALL_RADIUS * 2);

        this.radius = GameConstants.BALL_RADIUS;
        this.speed = GameConstants.BALL_INITIAL_SPEED;
        this.stuck = true;
        this.dx = 0;
        this.dy = 0;
    }

    // nếu sau này cần constructor ball theo toạ độ
    public Ball(double x, double y) {
        super(x, y, GameConstants.BALL_RADIUS * 2, GameConstants.BALL_RADIUS * 2);
        this.radius = GameConstants.BALL_RADIUS;
        this.speed = GameConstants.BALL_INITIAL_SPEED;
        this.stuck = false;
        this.dx = 0;
        this.dy = -speed;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    // update bóng theo delta
    public void update(double deltaTime) {
        if (!stuck) { // bóng rời paddle

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
        if (stuck) {
            stuck = false;
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

    // reverse chuyển động của bóng
    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    // reset
    public void reset(double paddleX, double paddleY) {
        this.stuck = true;
        this.x = paddleX;
        this.y = paddleY - radius * 2;
        this.dx = 0;
        this.dy = 0;
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
    public boolean isStuck() { return stuck; }

    // setter vận tốc và đảm bảo trong khoảng [min, max]
    public void setSpeed(double speed) {
        this.speed = Math.max(GameConstants.BALL_MIN_SPEED,
                Math.min(speed, GameConstants.BALL_MAX_SPEED));

        if (!stuck) {
            double currentSpeed = Math.sqrt(dx * dx + dy * dy);
            if (currentSpeed > 0) {
                double factor = this.speed / currentSpeed;
                dx *= factor;
                dy *= factor;
            }
        }
    }
}
