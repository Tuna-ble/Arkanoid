package org.example.gamelogic.core;

import org.example.config.GameConstants;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.powerups.PowerUp;
import org.example.gamelogic.events.BallHitPaddleEvent;
import org.example.gamelogic.events.BallLostEvent;
import org.example.gamelogic.events.BrickHitEvent;
import org.example.gamelogic.events.PowerUpCollectedEvent;

import java.util.Iterator;
import java.util.List;

public final class CollisionManager {

    public CollisionManager() {

    }

    public void checkCollisions(List<? extends IBall> balls, Paddle paddle, List<Brick> bricks, List<PowerUp> fallingPowerUps) {
        Iterator<? extends IBall> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            IBall ball = ballIterator.next();
            if (!ball.isActive()) continue;

            checkBallBoundsCollisions(ball);
            if (!ball.isActive()) continue;

            checkBallPaddleCollision(ball, paddle);

            checkBallBrickCollisions(ball, bricks);
        }

        // 4. Thanh đỡ vs PowerUp
        checkPaddlePowerUpCollisions(paddle, fallingPowerUps);
    }

    private void checkBallBoundsCollisions(IBall ball) {
        // Tường trái/phải
        if (ball.getX() <= 0 || (ball.getX() + ball.getWidth()) >= GameConstants.SCREEN_WIDTH) {
            ball.reverseDirX();
            // EventManager.getInstance().publish(new BallHitWallEvent(ball, WallDirection.SIDE)); // Tùy chọn
        }
        // Tường trên
        if (ball.getY() <= 0) {
            ball.reverseDirY();
            // EventManager.getInstance().publish(new BallHitWallEvent(ball, WallDirection.TOP)); // Tùy chọn
        }
        // Đáy màn hình
        if (ball.getY() > GameConstants.SCREEN_HEIGHT) {
            EventManager.getInstance().publish(new BallLostEvent(ball));
            ball.destroy(); // Đánh dấu bóng để xóa bởi BallManager
        }
    }

    private void checkBallPaddleCollision(IBall ball, Paddle paddle) {
        if (paddle != null && ((GameObject) ball).intersects(paddle.getGameObject())) {
            // Tính toán vị trí va chạm tương đối trên paddle (-1 đến 1)
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2.0;
            double ballCenter = ball.getX() + ball.getWidth() / 2.0;
            double hitPositionRatio = (ballCenter - paddleCenter) / (paddle.getWidth() / 2.0);
            // Giới hạn tỉ lệ trong khoảng [-1, 1]
            hitPositionRatio = Math.max(-1.0, Math.min(1.0, hitPositionRatio));

            // Yêu cầu bóng tự xử lý va chạm với paddle
            ball.handlePaddleCollision(paddle, hitPositionRatio);

            // Phát sự kiện
            EventManager.getInstance().publish(new BallHitPaddleEvent(ball, paddle));
        }
    }

    private void checkBallBrickCollisions(IBall ball, List<Brick> bricks) {
        // Không dùng Iterator ở đây vì gạch không bị xóa ngay lập tức
        for (Brick brick : bricks) {
            // Chỉ kiểm tra gạch còn sống và có va chạm
            if (!brick.isDestroyed() && ((GameObject) ball).intersects(brick.getGameObject())) {

                // Phát sự kiện va chạm (Gạch sẽ tự xử lý takeDamage khi nghe sự kiện này)
                EventManager.getInstance().publish(new BrickHitEvent(brick, ball));

                // Xử lý vật lý cơ bản: Đổi hướng bóng
                // TODO: Logic đổi hướng cần chính xác hơn (xét va chạm cạnh nào)
                ball.reverseDirY(); // Giả định va chạm trên/dưới

                // Chỉ xử lý 1 va chạm gạch mỗi frame cho mỗi quả bóng
                return; // Thoát khỏi hàm này sau khi xử lý 1 gạch
            }
        }
    }

    private void checkPaddlePowerUpCollisions(Paddle paddle, List<PowerUp> fallingPowerUps) {
        if (paddle == null || fallingPowerUps == null) return;

        // Dùng Iterator vì PowerUp có thể bị đánh dấu xóa ngay sau khi nhặt
        Iterator<PowerUp> powerUpIterator = fallingPowerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            // Chỉ kiểm tra power-up còn "sống"
            if (powerUp.isActive() && paddle.intersects(powerUp.getGameObject())) {
                // Phát sự kiện PowerUp đã được nhặt
                EventManager.getInstance().publish(new PowerUpCollectedEvent(powerUp));
                powerUp.markAsTaken(); // Đánh dấu đã nhặt (và isAlive = false)
            }
        }
    }
}
