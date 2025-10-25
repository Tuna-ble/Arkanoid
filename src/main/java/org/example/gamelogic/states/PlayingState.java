package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.core.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.core.BallManager;
import org.example.gamelogic.core.BrickManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.core.PowerUpManager;
import org.example.gamelogic.entities.Paddle;

public final class PlayingState implements GameState {
    BrickManager brickManager;
    PowerUpManager powerUpManager;
    GameManager gameManager;
    BallManager ballManager;
    Paddle paddle;
    Font scoreFont;

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

        ScoreManager.getInstance().resetScore();
        this.scoreFont = new Font("Arial", 24);
    }

    @Override
    public void update(double deltaTime) {
        brickManager.update(deltaTime);
        ballManager.update(deltaTime);
        powerUpManager.update(gameManager, deltaTime);
        paddle.update(deltaTime);
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
    public void handleInput() {

    }
}