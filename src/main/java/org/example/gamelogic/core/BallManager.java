package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.factory.BallFactory;
import org.example.gamelogic.registry.BallRegistry;
import org.example.data.SavedGameState;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;

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
        registry.register("STANDARD", new Ball(0, 0, GameConstants.BALL_RADIUS));
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

        activeBalls.removeIf(IBall::isDestroyed);
    }

    public void releaseAttachedBalls() {
        for (IBall ball : activeBalls) {
            if (ball instanceof Ball && ((Ball) ball).isAttachedToPaddle()) {
                ball.release();
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

    public void addBall(IBall ball) {
        if (ball != null && !ball.isAttachedToPaddle()) {
            this.activeBalls.add(ball);
        }
    }

    public long countActiveBalls() {
        return activeBalls.stream().filter(IBall::isActive).count();
    }

    public List<SavedGameState.BallData> getDataToSave() {
        List<SavedGameState.BallData> ballDataList = new ArrayList<>();
        for (IBall ball : activeBalls) {
            ballDataList.add(new SavedGameState.BallData(
                    ball.getX(),
                    ball.getY(),
                    ball.getDx(),
                    ball.getDy()
            ));
        }
        return ballDataList;
    }


    public void loadData(List<SavedGameState.BallData> ballDataList) {
        activeBalls.clear();

        for (SavedGameState.BallData data : ballDataList) {

            IBall ball = ballFactory.createBall("STANDARD", 0, 0);

            if (ball != null) {
                ball.setPosition(data.x, data.y);
                ball.setDx(data.vx);
                ball.setDy(data.vy);

                ball.release();
                activeBalls.add(ball);
            }
        }
    }
}