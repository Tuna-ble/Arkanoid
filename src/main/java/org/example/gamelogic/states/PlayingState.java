package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
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
import org.example.gamelogic.I_InputProvider;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;
import javafx.scene.effect.DropShadow;
import org.example.presentation.TextRenderer;


public final class PlayingState implements GameState {
    BrickManager brickManager;
    PowerUpManager powerUpManager;
    GameManager gameManager;
    BallManager ballManager;
    CollisionManager collisionManager;
    Paddle paddle;
    Font scoreFont;
    Image pauseIcon;

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
    }

    @Override
    public void update(double deltaTime) {
        paddle.update(deltaTime);
        updateAttachedBallPosition();

        brickManager.update(deltaTime);
        ballManager.update(deltaTime);
        powerUpManager.update(gameManager, deltaTime);

        if (ballManager.isEmpty()) {
            GameState gameOverState = new GameOverState(gameManager);
            gameManager.setState(gameOverState);
        }

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
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setFill(Color.PINK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        //render
        brickManager.render(gc);
        ballManager.render(gc);
        powerUpManager.render(gc);
        paddle.render(gc);
        renderScore(gc);
        try {
            pauseIcon = new Image(getClass().getResourceAsStream("/GameIcon/pause.png"));
            renderPauseButton(gc);
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh pause.png từ resources!");
            e.printStackTrace();
        }
    }

    private void renderScore(GraphicsContext gc) {
        int currentScore = ScoreManager.getInstance().getScore();
        DropShadow shadow = new DropShadow(6, Color.color(0,0,0,0.7));
        TextRenderer.drawOutlinedText(
                gc,
                "Score: " + currentScore,
                12,
                28,
                scoreFont,
                Color.WHITE,
                Color.color(0,0,0,0.9),
                1.5,
                shadow
        );
    }

    private void renderPauseButton(GraphicsContext gc) {
        if (pauseIcon == null) return;
        double iconWidth = 40;
        double iconHeight = 40;
        double x = gc.getCanvas().getWidth() - iconWidth - 10;
        double y = 10;
        gc.drawImage(pauseIcon, x, y, iconWidth, iconHeight);
    }

    @Override
    public void handleInput(I_InputProvider input) {
        if (input == null) return;

        // --- 1️⃣ Điều khiển paddle ---
        boolean moveLeft = input.isKeyPressed(KeyCode.LEFT) || input.isKeyPressed(KeyCode.A);
        boolean moveRight = input.isKeyPressed(KeyCode.RIGHT) || input.isKeyPressed(KeyCode.D);

        if (moveLeft && !moveRight) {
            paddle.setVelocity(-GameConstants.PADDLE_SPEED, 0);
        } else if (moveRight && !moveLeft) {
            paddle.setVelocity(GameConstants.PADDLE_SPEED, 0);
        } else {
            paddle.setVelocity(0, 0);
        }

        // --- 2️⃣ Tạm dừng game bằng phím tắt ---
        if (input.isKeyPressed(KeyCode.P)) {
            gameManager.setState(new PauseState(gameManager, this));
            return; // tránh xử lý thêm sau khi chuyển state
        }

        // --- 3️⃣ Xử lý chuột ---
        if (input.isMouseClicked()) {
            int mouseX = input.getMouseX();
            int mouseY = input.getMouseY();

            double pauseIconX = GameConstants.SCREEN_WIDTH - 50;
            double pauseIconY = 10;
            double pauseIconSize = 40;

            boolean clickOnPause =
                    mouseX >= pauseIconX &&
                            mouseX <= pauseIconX + pauseIconSize &&
                            mouseY >= pauseIconY &&
                            mouseY <= pauseIconY + pauseIconSize;

            if (clickOnPause) {
                // Click vào icon pause
                gameManager.setState(new PauseState(gameManager, this));
                return;
            } else {
                // Click ngoài icon => thả bóng
                ballManager.releaseAttachedBalls();
            }
        }

        // --- 4️⃣ Thả bóng bằng phím cách ---
        if (input.isKeyPressed(KeyCode.SPACE)) {
            ballManager.releaseAttachedBalls();
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

}
