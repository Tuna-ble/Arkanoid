package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.core.BallManager;
import org.example.gamelogic.core.BrickManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.core.PowerUpManager;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.events.PowerUpCollectedEvent;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PlayingState implements GameState {
    BrickManager brickManager;
    PowerUpManager powerUpManager;
    GameManager gameManager;
    BallManager ballManager;
    CollisionManager collisionManager;
    Paddle paddle;
    Font scoreFont;

    private List<PowerUpStrategy> activeStrategies = new ArrayList<>();

    public PlayingState(GameManager gameManager, int levelNumber) {
        this.gameManager=gameManager;
        this.brickManager = gameManager.getBrickManager();
        this.brickManager.loadLevel(levelNumber);
        this.powerUpManager = gameManager.getPowerUpManager();
        this.ballManager = gameManager.getBallManager();
        this.collisionManager = gameManager.getCollisionManager();
        this.paddle = new Paddle(
                GameConstants.PADDLE_X,
                GameConstants.PADDLE_Y,
                GameConstants.PADDLE_WIDTH,
                GameConstants.PADDLE_HEIGHT,
                0,
                0);

        this.ballManager.createInitialBall(this.paddle);
        powerUpManager.spawnPowerUp("E", 400, 100);
        powerUpManager.spawnPowerUp("F", 500, 100);

        ScoreManager.getInstance().resetScore();
        this.scoreFont = new Font("Arial", 24);

        subscribeToPowerUpCollectedEvent();
    }

    private void subscribeToPowerUpCollectedEvent() {
        EventManager.getInstance().subscribe(
                PowerUpCollectedEvent.class,
                this::handlePowerUpCollected
        );
    }

    private void handlePowerUpCollected(PowerUpCollectedEvent event) {
        if (event.getPowerUpCollected() != null && event.getPowerUpCollected().getStrategy() != null) {
            addStrategy(event.getPowerUpCollected().getStrategy());
        }
    }

    @Override
    public void update(double deltaTime) {
        updateStrategy(deltaTime);

        paddle.update(deltaTime);
        updateAttachedBallPosition();

        brickManager.update(deltaTime);
        ballManager.update(deltaTime);
        powerUpManager.update(gameManager, deltaTime);

        if (collisionManager != null) {
            collisionManager.checkCollisions(
                    ballManager.getActiveBalls(),
                    paddle,
                    brickManager.getBricks(),
                    powerUpManager.getActivePowerUps()
            );
        }
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(Color.PINK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        //render
        brickManager.render(gc);
        ballManager.render(gc);
        powerUpManager.render(gc);
        paddle.render(gc);

        renderScore(gc);
    }

    private void renderScore(GraphicsContext gc) {
        int currentScore = ScoreManager.getInstance().getScore();

        gc.setFont(scoreFont);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Score: " + currentScore, 10, 25);
    }

    @Override
    public void handleInput(I_InputProvider input) {
        if (input.isKeyPressed(KeyCode.LEFT) || input.isKeyPressed(KeyCode.A)) {
            paddle.setVelocity(-GameConstants.PADDLE_SPEED, 0);
        } else if (input.isKeyPressed(KeyCode.RIGHT) || input.isKeyPressed(KeyCode.D)) {
            paddle.setVelocity(GameConstants.PADDLE_SPEED, 0);
        } else {
            paddle.setVelocity(0, 0);
        }

        if (input.isKeyPressed(KeyCode.SPACE) || input.isMouseClicked()) {
            ballManager.releaseAttachedBalls(); // gọi hàm mới trong BallManager
        }
    }

    private void updateAttachedBallPosition() {
        for (IBall ball : ballManager.getActiveBalls()) {
            // Kiểm tra xem bóng có đang dính không
            if (ball.isAttachedToPaddle()) {
                // Tính toán và đặt lại vị trí bóng dựa trên paddle
                ball.setPosition(
                        paddle.getX() + (paddle.getWidth() / 2.0) - (ball.getGameObject().getWidth() / 2.0),
                        paddle.getY() - ball.getGameObject().getHeight()
                );
            }
        }
    }

    public void addStrategy(PowerUpStrategy strategy) {
        strategy.reset();
        activeStrategies.add(strategy);
        strategy.apply(this);
    }

    public void updateStrategy(double deltaTime) {
        Iterator<PowerUpStrategy> iterator = activeStrategies.iterator();
        while (iterator.hasNext()) {
            PowerUpStrategy strategy = iterator.next();

            strategy.update(this, deltaTime); // Giả sử PowerUpStrategy có phương thức này

            if (strategy.isExpired()) {
                strategy.remove(this);
                iterator.remove();
            }
        }
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public BallManager getBallManager() {
        return ballManager;
    }
}