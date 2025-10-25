package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.config.GameConstants;
import org.example.gamelogic.core.*;
import org.example.gamelogic.entities.Paddle;
import org.example.presentation.InputHandler;

public final class PlayingState implements GameState {
    BrickManager brickManager;
    PowerUpManager powerUpManager;
    GameManager gameManager;
    BallManager ballManager;
    CollisionManager collisionManager;
    Paddle paddle;
    Font scoreFont;

    public PlayingState(GameManager gameManager, int levelNumber) {
        this.gameManager = gameManager;
        this.brickManager = gameManager.getBrickManager();
        this.brickManager.loadLevel(levelNumber);
        this.powerUpManager = gameManager.getPowerUpManager();
        this.ballManager = gameManager.getBallManager();
        this.collisionManager = gameManager.getCollisionManager();
        this.paddle = gameManager.getPaddle();

        this.ballManager.createInitialBall(this.paddle);
        this.powerUpManager.spawnPowerUp("E", 360, 100);
        this.powerUpManager.spawnPowerUp("F", 450, 100);

        ScoreManager.getInstance().resetScore();
        this.scoreFont = new Font("Arial", 24);
    }

    @Override
    public void update(double deltaTime) {
        paddle.update(deltaTime);
        ballManager.updateAttachedBalls(paddle);

        brickManager.update(deltaTime);
        ballManager.update(deltaTime);
        powerUpManager.update(gameManager, deltaTime);

        collisionManager.checkCollisions(ballManager.getActiveBalls(), paddle,
                brickManager.getBricks(), powerUpManager.getActivePowerUps());
        gameManager.updateStrategy(deltaTime);
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
        gc.fillText("Score: " + currentScore, 10, 25);
    }

    @Override
    public void handleInput(InputHandler input) {

        if (input.isKeyPressed(KeyCode.LEFT) || input.isKeyPressed(KeyCode.A)) {
            paddle.setVelocity(-GameConstants.PADDLE_SPEED, 0);
        } else if (input.isKeyPressed(KeyCode.RIGHT) || input.isKeyPressed(KeyCode.D)) {
            paddle.setVelocity(GameConstants.PADDLE_SPEED, 0);
        } else {
            paddle.setVelocity(0, 0);
        }


        if (input.isKeyPressed(KeyCode.SPACE) || input.isMouseClicked()) {
            ballManager.releaseAllBalls(); // gọi hàm mới trong BallManager
        }
    }
}
