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
import java.util.function.BiConsumer;

/**
 * Xử lý va chạm giữa các game object: bóng, paddle, gạch, kẻ địch và tia laser.
 *
 * <p>Phương thức công khai chủ yếu là {@link #checkCollisions(...)}; lớp cũng cung cấp
 * các helper để giải quyết va chạm cụ thể.
 */
public final class CollisionManager {

    /**
     * Tạo mới CollisionManager.
     *
     * <p>Hiện tại không cần tham số; constructor để public vì lớp được sử dụng trực tiếp.
     */
    public CollisionManager() {

    }

    /**
     * Tìm và xử lý va chạm tốt nhất giữa một quả bóng và một danh sách đối tượng Collidable.
     * Chọn đối tượng có độ "overlap" lớn nhất và đẩy bóng ra ngoài, sau đó gọi callback onHit.
     *
     * @param ball quả bóng cần kiểm tra
     * @param objects danh sách đối tượng có thể va chạm
     * @param onHit callback được gọi khi xác nhận va chạm (thường để phát sự kiện)
     * @param <T> kiểu đối tượng Collidable
     * @return true nếu có va chạm và đã xử lý; false nếu không có va chạm
     */
    private <T extends Collidable> boolean resolveBallCollision(IBall ball, List<T> objects, BiConsumer<IBall, T> onHit) {
        T bestCollisionObject = null;
        double maxOverlap = -1.0;
        boolean collisionIsHorizontal = false;

        // Tìm va chạm tốt nhất
        for (T obj : objects) {
            if (!obj.isDestroyed() && ball.getGameObject().intersects(obj.getGameObject())) {
                // Tính toán độ lún
                double ballCenterX = ball.getX() + ball.getWidth() / 2.0;
                double ballCenterY = ball.getY() + ball.getHeight() / 2.0;
                double objCenterX = obj.getX() + obj.getWidth() / 2.0;
                double objCenterY = obj.getY() + obj.getHeight() / 2.0;

                double dx = ballCenterX - objCenterX;
                double dy = ballCenterY - objCenterY;
                double combinedHalfWidth = ball.getWidth() / 2.0 + obj.getWidth() / 2.0;
                double combinedHalfHeight = ball.getHeight() / 2.0 + obj.getHeight() / 2.0;

                double overlapX = combinedHalfWidth - Math.abs(dx);
                double overlapY = combinedHalfHeight - Math.abs(dy);

                if (overlapX > 0 && overlapY > 0) {
                    double currentOverlap;
                    boolean isHorizontal;
                    if (overlapX < overlapY) {
                        currentOverlap = overlapX;
                        isHorizontal = true;
                    } else {
                        currentOverlap = overlapY;
                        isHorizontal = false;
                    }

                    if (currentOverlap > maxOverlap) {
                        maxOverlap = currentOverlap;
                        bestCollisionObject = obj;
                        collisionIsHorizontal = isHorizontal;
                    }
                }
            }
        }

        // Xử lí va chạm tốt nhất
        if (bestCollisionObject != null) {
            // Đẩy bóng ra
            if (collisionIsHorizontal) {
                if (ball.getX() + ball.getWidth() / 2.0 > bestCollisionObject.getX() + bestCollisionObject.getWidth() / 2.0) {
                    ball.setPosition(bestCollisionObject.getX() + bestCollisionObject.getWidth()
                            + GameConstants.COLLISION_EPSILON, ball.getY());
                } else {
                    ball.setPosition(bestCollisionObject.getX() - ball.getWidth()
                            - GameConstants.COLLISION_EPSILON, ball.getY());
                }
                ball.reverseDirX();
            } else {
                if (ball.getY() + ball.getHeight() / 2.0
                        > bestCollisionObject.getY() + bestCollisionObject.getHeight() / 2.0) {
                    ball.setPosition(ball.getX(), bestCollisionObject.getY()
                            + bestCollisionObject.getHeight() + GameConstants.COLLISION_EPSILON);
                } else {
                    ball.setPosition(ball.getX(), bestCollisionObject.getY()
                            - ball.getHeight() - GameConstants.COLLISION_EPSILON);
                }
                ball.reverseDirY();
            }

            // Gọi logic lambda
            onHit.accept(ball, bestCollisionObject);
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra và xử lý tất cả va chạm giữa các đối tượng trò chơi.
     *
     * @param balls danh sách bóng hiện tại (kỳ vọng không null)
     * @param paddle paddle hiện tại (có thể null)
     * @param bricks danh sách gạch (kỳ vọng không null)
     * @param fallingPowerUps danh sách power-up rơi (kỳ vọng không null)
     * @param lasers danh sách tia laser hiện tại (kỳ vọng không null)
     * @param enemies danh sách kẻ địch (kỳ vọng không null)
     */
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

    /**
     * Kiểm tra và xử lý va chạm của quả bóng với biên play area (trái/phải/trên/dưới).
     * Phát sự kiện tương ứng khi bóng chạm tường hoặc bị mất.
     *
     * @param ball quả bóng cần kiểm tra
     */
    private void checkBallBoundsCollisions(IBall ball) {
        boolean collisionOccurred = false;

        if (ball.getX() <= GameConstants.PLAY_AREA_X) {
            ball.setPosition(GameConstants.PLAY_AREA_X, ball.getY());
            ball.reverseDirX();
            collisionOccurred = true;
        }

        else if ((ball.getX() + ball.getWidth()) >= (GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH)) {
            ball.setPosition(GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - ball.getWidth(), ball.getY());
            ball.reverseDirX();
            collisionOccurred = true;
        }

        if (ball.getY() <= GameConstants.PLAY_AREA_Y) {
            ball.setPosition(ball.getX(), GameConstants.PLAY_AREA_Y);
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

    /**
     * Kiểm tra va chạm giữa bóng và paddle; nếu va chạm đúng mặt trên paddle thì
     * tính toán vị trí va chạm tương đối và ủy quyền cho bóng xử lý.
     *
     * @param ball quả bóng
     * @param paddle paddle (có thể null)
     */
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

    /**
     * Chọn chế độ xử lý va chạm với gạch tuỳ theo trạng thái piercing của bóng.
     *
     * @param ball quả bóng
     * @param bricks danh sách gạch
     */
    private void checkBallBrickCollisions(IBall ball, List<Brick> bricks) {
        if (ball.getPierceLeft() > 0) {
            handlePiercingBrickCollision(ball, bricks);
        } else {
            handleNormalBrickCollision(ball, bricks);
        }
    }

    /**
     * Xử lý va chạm khi bóng đang ở trạng thái piercing: cho phép xuyên nhiều gạch
     * và đảm bảo không gây trùng lặp damage trên cùng một gạch.
     *
     * @param ball quả bóng
     * @param bricks danh sách gạch
     */
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

    /**
     * Xử lý va chạm bình thường giữa bóng và gạch (không xuyên), dùng resolveBallCollision.
     *
     * @param ball quả bóng
     * @param bricks danh sách gạch
     */
    private void handleNormalBrickCollision(IBall ball, List<Brick> bricks) {
        resolveBallCollision(ball, bricks, (theBall, theBrick) -> {
            EventManager.getInstance().publish(new BallHitBrickEvent(theBrick, theBall));
        });
    }

    /**
     * Kiểm tra va chạm giữa paddle và các power-up rơi; nếu trúng thì publish sự kiện
     * và đánh dấu power-up đã được thu.
     *
     * @param paddle paddle hiện tại (có thể null)
     * @param fallingPowerUps danh sách power-up rơi
     */
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

    /**
     * Kiểm tra va chạm cho các tia laser với gạch, kẻ địch và paddle; xử lý thiệt hại và
     * vô hiệu hoá tia khi cần.
     *
     * @param lasers danh sách tia laser
     * @param bricks danh sách gạch
     * @param enemies danh sách kẻ địch
     * @param paddle paddle (dùng khi tia bắn của kẻ địch có thể chạm paddle)
     */
    private void checkLaserCollisions(List<LaserBullet> lasers, List<Brick> bricks, List<Enemy> enemies, Paddle paddle) {
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

                if (laser.isActive()) {
                    for (Enemy enemy : enemies) {
                        if (!enemy.isDestroyed() && laser.intersects(enemy.getGameObject())) {
                            EventManager.getInstance().
                                    publish(new EnemyDamagedEvent(enemy, laser.getGameObject()));
                            laser.setActive(false);
                            break;
                        }
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

    /**
     * Chọn chế độ xử lý va chạm giữa bóng và kẻ địch tuỳ trạng thái piercing.
     *
     * @param ball quả bóng
     * @param enemies danh sách kẻ địch
     */
    private void checkBallEnemyCollisions(IBall ball, List<Enemy> enemies) {
        if (ball.getPierceLeft() > 0) {
            handlePiercingEnemyCollision(ball, enemies);
        } else {
            handleNormalEnemyCollision(ball, enemies);
        }
    }

    /**
     * Xử lý va chạm khi bóng có khả năng xuyên kẻ địch (piercing), tương tự như piercing brick.
     *
     * @param ball quả bóng
     * @param enemies danh sách kẻ địch
     */
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

    /**
     * Xử lý va chạm bình thường giữa bóng và kẻ địch bằng resolveBallCollision.
     *
     * @param ball quả bóng
     * @param enemies danh sách kẻ địch
     */
    private void handleNormalEnemyCollision(IBall ball, List<Enemy> enemies) {
        resolveBallCollision(ball, enemies, (theBall, theEnemy) -> {
            EventManager.getInstance().publish(new BallHitEnemyEvent(theEnemy, theBall));
        });
    }

    /**
     * Kiểm tra va chạm giữa một enemy và danh sách gạch; trả về true nếu có va chạm
     * và đã xử lý vị trí/đảo chiều di chuyển.
     *
     * @param enemy kẻ địch cần kiểm tra
     * @param bricks danh sách gạch
     * @return true nếu có va chạm và đã được xử lý
     */
    private boolean checkEnemyBrickCollisions(Enemy enemy, List<Brick> bricks) {
        if ((enemy.getY() + enemy.getHeight()) < GameConstants.TOP_MARGIN) {
            return false;
        }

        Brick bestCollisionBrick = null;
        double maxOverlap = -1.0;
        boolean collisionIsHorizontal = false;

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

                    if (overlapX < overlapY) {
                        currentOverlap = overlapX;
                        isHorizontal = true;
                    } else {
                        currentOverlap = overlapY;
                        isHorizontal = false;
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

    /**
     * Kiểm tra và xử lý va chạm của enemy với biên play area (trái/phải/trên).
     *
     * @param enemy kẻ địch cần kiểm tra
     */
    private void checkEnemyBoundsCollisions(Enemy enemy) {
        if (enemy.getX() <= GameConstants.PLAY_AREA_X) {
            enemy.setPosition(GameConstants.PLAY_AREA_X, enemy.getY());
            enemy.reverseDirX();
        }

        else if ((enemy.getX() + enemy.getWidth()) >= (GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH)) {
            enemy.setPosition(GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - enemy.getWidth(), enemy.getY());
            enemy.reverseDirX();
        }

        if (enemy.getHasEnteredScreen() && enemy.getY() <= GameConstants.PLAY_AREA_Y) {
            enemy.setPosition(enemy.getX(), GameConstants.PLAY_AREA_Y);
            enemy.reverseDirY();
        }
    }
}
