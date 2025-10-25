package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle; // MỚI: Import Paddle
import org.example.gamelogic.factory.BallFactory;
import org.example.gamelogic.registry.BallRegistry;

import java.util.ArrayList;
import java.util.List;

public final class BallManager {
    private final BallFactory ballFactory;
    private List<IBall> activeBalls;

    public BallManager() {
        this.activeBalls =  new ArrayList<>();
        BallRegistry registry = BallRegistry.getInstance();
        registerBallPrototypes(registry);
        this.ballFactory = new BallFactory(registry);
    }

    private void registerBallPrototypes(BallRegistry registry) {
        registry.register("STANDARD", new Ball(0, 0, GameConstants.BALL_RADIUS,0, 0));
    }

    public void createInitialBall(Paddle paddle) {
        activeBalls.clear();
        IBall ball = ballFactory.createBall("STANDARD", 0, 0);
        if (ball != null) {
            ball.reset(paddle.getX(), paddle.getY(), paddle.getWidth());
            activeBalls.add(ball);
        }
    }

    public void update(double deltaTime) {
        for (IBall ball : activeBalls) {
            ball.update(deltaTime);
        }

        activeBalls.removeIf(ball -> !ball.isActive());
    }

    /**
     * MỚI: Thêm hàm bắn tất cả bóng đang dính vào paddle.
     * (Giả định hàm ball.isActive() trả về true nếu đang dính, false nếu đang bay)
     */
    public void releaseAllBalls() {
        for (IBall ball : activeBalls) {
            if (ball.isAttachedToPaddle()) {
                ball.release();
            }
        }
    }

    /**
     * MỚI: Thêm hàm cập nhật vị trí bóng theo paddle (khi bóng đang dính).
     */
    public void updateAttachedBalls(Paddle paddle) {
        for (IBall ball : activeBalls) {
            if (ball.isAttachedToPaddle()) {
                ball.setPosition(
                        paddle.getX() + (paddle.getWidth() / 2.0) - (ball.getWidth() / 2.0),
                        paddle.getY() - ball.getHeight()
                );
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (IBall ball : activeBalls) {
            ball.render(gc);
        }
    }

    public List<IBall> getActiveBalls() {
        return activeBalls;
    }

    public void resetBalls(Paddle paddle) {
        activeBalls.clear();
        createInitialBall(paddle);
    }

    public IBall getPrimaryBall() {
        return activeBalls.isEmpty() ? null : activeBalls.get(0);
    }

    public void clear() {
        activeBalls.clear();
    }
}
