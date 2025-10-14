package org.example.business.states;

import org.example.business.core.StateManager;
import org.example.business.entities.Ball;
import org.example.business.entities.Brick;
import org.example.business.entities.Paddle;
import org.example.presentation.controller.InputHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayingState implements GameState {
    private final StateManager stateManager;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;

    private int score;
    private int lives;

    private static final int SCREEN_WIDTH = 960;
    private static final int SCREEN_HEIGHT = 600;

    public PlayingState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void initLevel() {
        score = 0;
        lives = 3;

        paddle = new Paddle(SCREEN_WIDTH / 2.0 - 50, SCREEN_HEIGHT - 30, 100, 15);
        ball = new Ball(SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0, 8, -5, -5);

        bricks = new ArrayList<>();
        int brickWidth = 75;
        int brickHeight = 20;
        int padding = 10;
        int rows = 5;
        int cols = 10;
        int offsetTop = 50;
        int offsetLeft = 60;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                double x = i * (brickWidth + padding) + offsetLeft;
                double y = j * (brickHeight + padding) + offsetTop;
            }
        }
    }

    @Override
    public void update() {
        handleInput();
        ball.update();
        paddle.update(SCREEN_WIDTH);
        checkCollisions();

        if (lives <= 0) {
            stateManager.setState("GAME_OVER");
        }
        if (bricks.isEmpty()) {
            initLevel();
        }
    }

    private void checkCollisions() {

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        paddle.render(gc);
        ball.render(gc);
        for (Brick brick : bricks) {
            brick.render(gc);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        gc.fillText("Score: " + score, 20, 30);
        gc.fillText("Lives: " + lives, SCREEN_WIDTH - 100, 30);
    }

    @Override
    public void handleInput() {

    }

    public int getScore() {
        return score;
    }
}