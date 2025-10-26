package org.example.gamelogic.states;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.core.BallManager;
import org.example.gamelogic.core.BrickManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.core.PowerUpManager;
import org.example.gamelogic.entities.Paddle;
import org.example.presentation.InputHandler;

public final class PlayingState implements GameState {
    BrickManager brickManager;
    PowerUpManager powerUpManager;
    GameManager gameManager;
    BallManager ballManager;
    Paddle paddle;

    public PlayingState(GameManager gameManager, int levelNumber) {
        this.gameManager=gameManager;
        this.brickManager = gameManager.getBrickManager();
        this.brickManager.loadLevel(levelNumber);
        this.powerUpManager = gameManager.getPowerUpManager();
        this.ballManager = gameManager.getBallManager();
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
    }

    @Override
    public void update(double deltaTime) {
        brickManager.update(deltaTime);
        paddle.update(deltaTime);
        ballManager.update(deltaTime, paddle);
        powerUpManager.update(gameManager, deltaTime);
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
    }

    public void handleInput(InputHandler inputHandler) {
        // Key states
        boolean leftPressed = inputHandler.isKeyPressed(KeyCode.LEFT) ||
                inputHandler.isKeyPressed(KeyCode.A);
        boolean rightPressed = inputHandler.isKeyPressed(KeyCode.RIGHT) ||
                inputHandler.isKeyPressed(KeyCode.D);

        // Mouse state
        int mouseX = inputHandler.getMouseX();
        boolean mouseInBounds = inputHandler.isMouseInBounds(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        boolean mouseActive = inputHandler.isMouseActive() && mouseInBounds;

        if (mouseActive) { // Ưu tiên chuột
            double newPaddleX = mouseX - paddle.getWidth() / 2.0;
            newPaddleX = Math.max(0, Math.min(newPaddleX, GameConstants.SCREEN_WIDTH - paddle.getWidth()));
            paddle.setX(newPaddleX);
            paddle.stop();
        } else {
            if (leftPressed && !rightPressed) {
                paddle.moveLeft();
            } else if (rightPressed && !leftPressed) {
                paddle.moveRight();
            } else {
                paddle.stop();
            }
        }

        // SPACE để bắt đầu/bắn
        if (inputHandler.isKeyPressed(KeyCode.SPACE)) {
            ballManager.Start();
        }
    }

}