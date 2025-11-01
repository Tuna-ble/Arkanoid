package org.example.gamelogic.core;

import org.example.config.GameConstants;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.powerups.PowerUp;
import org.example.gamelogic.events.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CollisionManager {

    public CollisionManager() {

    }

    public void checkCollisions(List<IBall> balls, Paddle paddle, List<Brick> bricks,
                                List<PowerUp> fallingPowerUps, List<LaserBullet> lasers) {
        List<IBall> ballsSnapshot = new ArrayList<>(balls);
        for (IBall ball : ballsSnapshot) {
            if (!ball.isActive()) continue;

            checkBallBoundsCollisions(ball);
            if (!ball.isActive()) continue;

            checkBallPaddleCollision(ball, paddle);

            checkBallBrickCollisions(ball, bricks);
        }

        // 4. Thanh đỡ vs PowerUp
        checkPaddlePowerUpCollisions(paddle, fallingPowerUps);

        checkLaserBrickCollisions(lasers, bricks);
    }

    private void checkBallBoundsCollisions(IBall ball) {
        boolean collisionOccurred = false;
        // Tường trái/phải
        if (ball.getX() <= 0) {
            ball.setPosition(0, ball.getY()); // Đẩy bóng về đúng biên trái
            ball.reverseDirX();
            collisionOccurred = true;
        }
        // Tường phải
        else if ((ball.getX() + ball.getWidth()) >= GameConstants.SCREEN_WIDTH) {
            // Đẩy bóng về đúng biên phải
            ball.setPosition(GameConstants.SCREEN_WIDTH - ball.getWidth(), ball.getY());
            ball.reverseDirX();
            collisionOccurred = true;
        }

        // Tường trên
        if (ball.getY() <= 0) {
            ball.setPosition(ball.getX(), 0);
            ball.reverseDirY();
            collisionOccurred = true;
        }
        // Đáy màn hình
        if (ball.getY() > GameConstants.SCREEN_HEIGHT) {
            ball.destroy();// Đánh dấu bóng để xóa bởi BallManager
            EventManager.getInstance().publish(new BallLostEvent(ball));
            collisionOccurred = true;
        }

        if (collisionOccurred) {
            EventManager.getInstance().publish(new BallHitWallEvent(ball));
        }
    }

    private void checkBallPaddleCollision(IBall ball, Paddle paddle) {
        if (paddle != null && ball.getGameObject().intersects(paddle.getGameObject())) {
            boolean hitTopSurface = (ball.getY() + ball.getHeight()) < (paddle.getY() + paddle.getHeight() * 0.5);

            if (hitTopSurface) {
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
    }

    private void checkBallBrickCollisions(IBall ball, List<Brick> bricks) {
        Brick bestCollisionBrick = null; // Viên gạch va chạm "tốt nhất"
        double maxOverlap = -1.0;          // Độ lún lớn nhất tìm thấy
        boolean collisionIsHorizontal = false; // Hướng va chạm chính

        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ball.getGameObject().intersects(brick.getGameObject())) {
                // --- Tính toán độ lún (overlap) ---
                double ballCenterX = ball.getX() + ball.getWidth() / 2.0;
                double ballCenterY = ball.getY() + ball.getHeight() / 2.0;
                double brickCenterX = brick.getX() + brick.getWidth() / 2.0;
                double brickCenterY = brick.getY() + brick.getHeight() / 2.0;
                double dx = ballCenterX - brickCenterX;
                double dy = ballCenterY - brickCenterY;
                double combinedHalfWidth = ball.getWidth() / 2.0 + brick.getWidth() / 2.0;
                double combinedHalfHeight = ball.getHeight() / 2.0 + brick.getHeight() / 2.0;
                double overlapX = combinedHalfWidth - Math.abs(dx);
                double overlapY = combinedHalfHeight - Math.abs(dy);

                // Chỉ xử lý nếu thực sự có lún vào (overlap > 0)
                if (overlapX > 0 && overlapY > 0) {
                    // Xác định hướng va chạm chính (hướng lún ÍT hơn)
                    double currentOverlap;
                    boolean isHorizontal;
                    if (overlapX < overlapY) {
                        currentOverlap = overlapX;
                        isHorizontal = true;
                    } else {
                        currentOverlap = overlapY;
                        isHorizontal = false;
                    }

                    // Lưu lại va chạm có độ lún lớn nhất
                    if (currentOverlap > maxOverlap) {
                        maxOverlap = currentOverlap;
                        bestCollisionBrick = brick;
                        collisionIsHorizontal = isHorizontal;
                    }
                }
            }
        } // Kết thúc vòng lặp FOR

        // --- Xử lý va chạm TỐT NHẤT sau khi duyệt hết ---
        if (bestCollisionBrick != null) {
            // Đẩy bóng ra khỏi viên gạch va chạm sâu nhất
            if (collisionIsHorizontal) { // Va chạm ngang
                if (ball.getX() + ball.getWidth() / 2.0 > bestCollisionBrick.getX() + bestCollisionBrick.getWidth() / 2.0) { // Bóng bên phải
                    ball.setPosition(bestCollisionBrick.getX() + bestCollisionBrick.getWidth(), ball.getY());
                } else { // Bóng bên trái
                    ball.setPosition(bestCollisionBrick.getX() - ball.getWidth(), ball.getY());
                }
                ball.reverseDirX();
            } else { // Va chạm dọc
                if (ball.getY() + ball.getHeight() / 2.0 > bestCollisionBrick.getY() + bestCollisionBrick.getHeight() / 2.0) { // Bóng bên dưới
                    ball.setPosition(ball.getX(), bestCollisionBrick.getY() + bestCollisionBrick.getHeight());
                } else { // Bóng bên trên
                    ball.setPosition(ball.getX(), bestCollisionBrick.getY() - ball.getHeight());
                }
                ball.reverseDirY();
            }

            // Phát sự kiện cho viên gạch bị va chạm
            EventManager.getInstance().publish(new BallHitBrickEvent(bestCollisionBrick, ball));
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

    private void checkLaserBrickCollisions(List<LaserBullet> lasers, List<Brick> bricks) {
        Iterator<LaserBullet> iterator = lasers.iterator();
        while (iterator.hasNext()) {
            LaserBullet laser = iterator.next();

            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && laser.intersects(brick.getGameObject())) {
                    EventManager.getInstance().
                            publish(new BrickDamagedEvent(brick, laser.getGameObject()));
                    laser.setActive(false);
                    break;
                }
            }
        }
    }
}