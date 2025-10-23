package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;
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
        activeBalls.removeIf(ball -> ball.isDestroyed());
    }

    public void render(GraphicsContext gc) {
        for (IBall ball : activeBalls) {
            ball.render(gc);
        }
    }

    // Cần thiết cho CollisionManager
    public List<IBall> getActiveBalls() {
        return activeBalls;
    }

    public void resetBalls(Paddle paddle) {
        activeBalls.clear();
        createInitialBall(paddle);
    }

    // Lấy bóng chính (nếu chỉ có 1)
    public IBall getPrimaryBall() {
        return activeBalls.isEmpty() ? null : activeBalls.get(0);
    }

    public void clear() {
        activeBalls.clear();
    }
}