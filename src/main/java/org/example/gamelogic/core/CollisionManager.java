package org.example.gamelogic.core;

import org.example.config.GameConstants;
import org.example.gamelogic.entities.*;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.enemy.Enemy;
import org.example.gamelogic.entities.powerups.PowerUp;
import org.example.gamelogic.events.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CollisionManager {

    public CollisionManager() {

    }

    public void checkCollisions(List<IBall> balls, Paddle paddle, List<Brick> bricks,
                                List<PowerUp> fallingPowerUps, List<LaserBullet> lasers,
                                List<Enemy> enemies) {
        List<IBall> ballsSnapshot = new ArrayList<>(balls);
        for (IBall ball : ballsSnapshot) {
            if (!ball.isActive()) continue;

            checkBallBoundsCollisions(ball);
            if (!ball.isActive()) continue;

            checkBallPaddleCollision(ball, paddle);

            checkBallBrickCollisions(ball, bricks);

            checkBallEnemyCollisions(ball, enemies);
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) continue;

            boolean didCollideWithBrick = checkEnemyBrickCollisions(enemy, bricks);

            if (!didCollideWithBrick) {
                checkEnemyBoundsCollisions(enemy);
            }
        }
        checkPaddlePowerUpCollisions(paddle, fallingPowerUps);

        checkLaserCollisions(lasers, bricks, enemies, paddle);
    }

    private void checkBallBoundsCollisions(IBall ball) {
        boolean collisionOccurred = false;

        if (ball.getX() <= GameConstants.PLAY_AREA_X + 25) {
            ball.setPosition(GameConstants.PLAY_AREA_X + 25, ball.getY());
            ball.reverseDirX();
            collisionOccurred = true;
        } else if ((ball.getX() + ball.getWidth()) >= (GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH) - 25) {
            ball.setPosition(GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - 25 - ball.getWidth(), ball.getY());
            ball.reverseDirX();
            collisionOccurred = true;
        }

        if (ball.getY() <= GameConstants.PLAY_AREA_Y + 25) {
            ball.setPosition(ball.getX(), GameConstants.PLAY_AREA_Y + 25);
            ball.reverseDirY();
            collisionOccurred = true;
        }

        if (ball.getY() > (GameConstants.PLAY_AREA_Y + GameConstants.PLAY_AREA_HEIGHT)) {
            ball.destroy();
            EventManager.getInstance().publish(new BallLostEvent(ball));
            collisionOccurred = false;
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
        if (ball.getPierceLeft() > 0) {
            handlePiercingBrickCollision(ball, bricks);
        } else {
            handleNormalBrickCollision(ball, bricks);
        }
    }

    private void handlePiercingBrickCollision(IBall ball, List<Brick> bricks) {
        // ConcurrentModificationException is the worst
        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;
            if (!ball.getGameObject().intersects(brick.getGameObject())) continue;

            boolean alreadyPierced = false;

            // skip repeated damage on the same brick
            for (GameObject piercingObject : ball.getPiercingObjects()) {
                if (brick.getGameObject().equals(piercingObject)) {
                    alreadyPierced = true;
                    break;
                }
            }

            if (!alreadyPierced) {
                ball.getPiercingObjects().add(brick.getGameObject());
                EventManager.getInstance().publish(new BallHitBrickEvent(brick, ball));
            }
        }

        // clear piercing if brick is destroyed or ball is no longer overlapping
        if (!ball.getPiercingObjects().isEmpty()) {
            Iterator<GameObject> iterator = ball.getPiercingObjects().iterator();
            while (iterator.hasNext()) {
                GameObject now = iterator.next();
                if (now instanceof Brick brick) {
                    if (brick.isDestroyed() || !ball.getGameObject().intersects(brick.getGameObject())) {
                        ball.setPierceLeft(ball.getPierceLeft() - 1);
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void handleNormalBrickCollision(IBall ball, List<Brick> bricks) {
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

    private void checkLaserCollisions(List<LaserBullet> lasers, List<Brick> bricks, List<Enemy> enemies, Paddle paddle) {
        // ConcurrentModificationException is the worst
        Iterator<LaserBullet> iterator = lasers.iterator();
        while (iterator.hasNext()) {
            LaserBullet laser = iterator.next();

            if (laser.getFaction() == BulletFrom.PLAYER) {
                for (Brick brick : bricks) {
                    if (!brick.isDestroyed() && laser.intersects(brick.getGameObject())) {
                        EventManager.getInstance().
                                publish(new BrickDamagedEvent(brick, laser.getGameObject()));
                        laser.setActive(false);
                        break;
                    }
                }
                for (Enemy enemy : enemies) {
                    if (!enemy.isDestroyed() && laser.intersects(enemy.getGameObject())) {
                        EventManager.getInstance().
                                publish(new EnemyDamagedEvent(enemy, laser.getGameObject()));
                        laser.setActive(false);
                        break;
                    }
                }
            } else {
                if (paddle.intersects(laser.getGameObject())) {
                    // EventManager.getInstance().publish(new PaddleHitEvent(paddle));
                    LifeManager.getInstance().loseLife();
                    laser.setActive(false);
                    break;
                }
            }
        }
    }

    private void checkBallEnemyCollisions(IBall ball, List<Enemy> enemies) {
        if (ball.getPierceLeft() > 0) {
            handlePiercingEnemyCollision(ball, enemies);
        } else {
            handleNormalEnemyCollision(ball, enemies);
        }
    }

    private void handlePiercingEnemyCollision(IBall ball, List<Enemy> enemies) {
        // ConcurrentModificationException is the worst
        for (Enemy enemy : enemies) {
            if (enemy.isDestroyed()) continue;
            if (!ball.getGameObject().intersects(enemy.getGameObject())) continue;

            boolean alreadyPierced = false;

            // skip repeated damage on the same enemy
            for (GameObject piercingObject : ball.getPiercingObjects()) {
                if (enemy.getGameObject().equals(piercingObject)) {
                    alreadyPierced = true;
                }
            }

            if (!alreadyPierced) {
                ball.getPiercingObjects().add(enemy.getGameObject());
                EventManager.getInstance().publish(new BallHitEnemyEvent(enemy, ball));
            }
        }

        // clear piercing if enemy is destroyed or ball is no longer overlapping
        if (!ball.getPiercingObjects().isEmpty()) {
            Iterator<GameObject> iterator = ball.getPiercingObjects().iterator();
            while (iterator.hasNext()) {
                GameObject now = iterator.next();
                if (now instanceof Enemy enemy) {
                    if (enemy.isDestroyed() || !ball.getGameObject().intersects(enemy.getGameObject())) {
                        ball.setPierceLeft(ball.getPierceLeft() - 1);
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void handleNormalEnemyCollision(IBall ball, List<Enemy> enemies) {
        Enemy bestCollisionEnemy = null;
        double maxOverlap = -1.0;
        boolean collisionIsHorizontal = false;

        for (Enemy enemy : enemies) {
            if (!enemy.isDestroyed() && ball.getGameObject().intersects(enemy.getGameObject())) {
                // --- Tính toán độ lún (overlap) ---
                double ballCenterX = ball.getX() + ball.getWidth() / 2.0;
                double ballCenterY = ball.getY() + ball.getHeight() / 2.0;
                double enemyCenterX = enemy.getX() + enemy.getWidth() / 2.0;
                double enemyCenterY = enemy.getY() + enemy.getHeight() / 2.0;
                double dx = ballCenterX - enemyCenterX;
                double dy = ballCenterY - enemyCenterY;
                double combinedHalfWidth = ball.getWidth() / 2.0 + enemy.getWidth() / 2.0;
                double combinedHalfHeight = ball.getHeight() / 2.0 + enemy.getHeight() / 2.0;
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
                        bestCollisionEnemy = enemy;
                        collisionIsHorizontal = isHorizontal;
                    }
                }
            }
        } // Kết thúc vòng lặp FOR

        // --- Xử lý va chạm TỐT NHẤT sau khi duyệt hết ---
        if (bestCollisionEnemy != null) {
            // Đẩy bóng ra khỏi viên gạch va chạm sâu nhất
            if (collisionIsHorizontal) { // Va chạm ngang
                if (ball.getX() + ball.getWidth() / 2.0 > bestCollisionEnemy.getX() + bestCollisionEnemy.getWidth() / 2.0) { // Bóng bên phải
                    ball.setPosition(bestCollisionEnemy.getX() + bestCollisionEnemy.getWidth(), ball.getY());
                } else { // Bóng bên trái
                    ball.setPosition(bestCollisionEnemy.getX() - ball.getWidth(), ball.getY());
                }
                ball.reverseDirX();
            } else { // Va chạm dọc
                if (ball.getY() + ball.getHeight() / 2.0 > bestCollisionEnemy.getY() + bestCollisionEnemy.getHeight() / 2.0) { // Bóng bên dưới
                    ball.setPosition(ball.getX(), bestCollisionEnemy.getY() + bestCollisionEnemy.getHeight());
                } else { // Bóng bên trên
                    ball.setPosition(ball.getX(), bestCollisionEnemy.getY() - ball.getHeight());
                }
                ball.reverseDirY();
            }

            // Phát sự kiện cho viên gạch bị va chạm
            EventManager.getInstance().publish(new BallHitEnemyEvent(bestCollisionEnemy, ball));
        }
    }

    // BÊN TRONG CollisionManager.java

    private boolean checkEnemyBrickCollisions(Enemy enemy, List<Brick> bricks) {
        if ((enemy.getY() + enemy.getHeight()) < GameConstants.TOP_MARGIN) {
            return false;
        }

        Brick bestCollisionBrick = null;
        double maxOverlap = -1.0;
        boolean collisionIsHorizontal = false; // Sẽ được quyết định bởi logic SỬA LỖI

        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && enemy.getGameObject().intersects(brick.getGameObject())) {

                double enemyCenterX = enemy.getX() + enemy.getWidth() / 2.0;
                double enemyCenterY = enemy.getY() + enemy.getHeight() / 2.0;
                double brickCenterX = brick.getX() + brick.getWidth() / 2.0;
                double brickCenterY = brick.getY() + brick.getHeight() / 2.0;

                double dx = enemyCenterX - brickCenterX;
                double dy = enemyCenterY - brickCenterY;
                double combinedHalfWidth = enemy.getWidth() / 2.0 + brick.getWidth() / 2.0;
                double combinedHalfHeight = enemy.getHeight() / 2.0 + brick.getHeight() / 2.0;

                double overlapX = combinedHalfWidth - Math.abs(dx);
                double overlapY = combinedHalfHeight - Math.abs(dy);

                if (overlapX > 0 && overlapY > 0) {
                    boolean isHorizontal;
                    double currentOverlap;

                    if (enemy.getDy() > 0) {
                        // ƯU TIÊN va chạm DỌC
                        isHorizontal = false;
                        currentOverlap = overlapY;
                    } else {
                        if (overlapX < overlapY) {
                            currentOverlap = overlapX;
                            isHorizontal = true;
                        } else {
                            currentOverlap = overlapY;
                            isHorizontal = false;
                        }
                    }

                    if (currentOverlap > maxOverlap) {
                        maxOverlap = currentOverlap;
                        bestCollisionBrick = brick;
                        collisionIsHorizontal = isHorizontal;
                    }
                }
            }
        }

        if (bestCollisionBrick != null) {
            if (collisionIsHorizontal) {
                if (enemy.getX() + enemy.getWidth() / 2.0 > bestCollisionBrick.getX() + bestCollisionBrick.getWidth() / 2.0) {
                    enemy.setPosition(bestCollisionBrick.getX() + bestCollisionBrick.getWidth(), enemy.getY());
                } else {
                    enemy.setPosition(bestCollisionBrick.getX() - enemy.getWidth(), enemy.getY());
                }
                enemy.reverseDirX();
            } else {
                if (enemy.getY() + enemy.getHeight() / 2.0 > bestCollisionBrick.getY() + bestCollisionBrick.getHeight() / 2.0) {
                    enemy.setPosition(enemy.getX(), bestCollisionBrick.getY() + bestCollisionBrick.getHeight());
                } else {
                    enemy.setPosition(enemy.getX(), bestCollisionBrick.getY() - enemy.getHeight());
                }
                enemy.reverseDirY();
            }

            return true;
        }
        return false;
    }

    private void checkEnemyBoundsCollisions(Enemy enemy) {
        if (enemy.getX() <= GameConstants.PLAY_AREA_X + 25) {
            enemy.setPosition(GameConstants.PLAY_AREA_X + 25, enemy.getY());
            enemy.reverseDirX();
        } else if ((enemy.getX() + enemy.getWidth()) >= (GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH) - 25) { // 0
            enemy.setPosition(GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - 25 - enemy.getWidth(), enemy.getY()); // 0
            enemy.reverseDirX();
        }

        if (enemy.getHasEnteredScreen() && enemy.getY() <= GameConstants.PLAY_AREA_Y + 25) {
            enemy.setPosition(enemy.getX(), GameConstants.PLAY_AREA_Y + 25);
            enemy.reverseDirY();
        }
    }
}
