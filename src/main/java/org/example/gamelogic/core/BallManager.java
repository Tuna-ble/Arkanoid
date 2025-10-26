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
    private boolean Start;

    public BallManager() {
        this.activeBalls =  new ArrayList<>();
        BallRegistry registry = BallRegistry.getInstance();
        registerBallPrototypes(registry);
        this.ballFactory = new BallFactory(registry);
        Start = false;
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

    public void update(double deltaTime, Paddle paddle) {
        for (IBall ball : activeBalls) {
            if(Start) ball.update(deltaTime);
            else ball.updateBeforeStart(deltaTime, paddle);
        }
        activeBalls.removeIf(ball -> {
            boolean destroyed = ball.isDestroyed();
            return destroyed;
        });
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

    public void Start() {
        if(!activeBalls.isEmpty()) {
            Start = true;
            IBall ball = activeBalls.get(0);
            ball.release();
        }
    }

    public void clear() {
        Start = false;
        activeBalls.clear();
    }
}
