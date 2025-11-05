package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.data.FileLevelRepository;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.*;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.events.*;
import org.example.gamelogic.entities.powerups.PowerUp;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class PlayingState implements GameState {
    private Image PlayingImage;
    BrickManager brickManager;
    PowerUpManager powerUpManager;
    GameManager gameManager;
    BallManager ballManager;
    CollisionManager collisionManager;
    LaserManager laserManager;
    EnemyManager enemyManager;
    Paddle paddle;
    Font scoreFont;
    Image pauseIcon;
    private int currentLives;
    private boolean hasWon = false;

    private List<PowerUpStrategy> activeStrategies = new ArrayList<>();
    private int levelNumber;

    private final Consumer<BallLostEvent> handleBallLost;
    private final Consumer<PowerUpCollectedEvent> handlePowerUpCollected;
    private final Consumer<LifeLostEvent> handleLifeLost;
    private final Consumer<LifeAddedEvent> handleLifeAdded;

    public PlayingState(GameManager gameManager, int levelNumber) {
        PlayingImage = new Image(getClass().getResourceAsStream("/GameIcon/playing.png"));
        this.gameManager = gameManager;
        this.brickManager = gameManager.getBrickManager();
        this.brickManager.loadLevel(levelNumber);
        this.powerUpManager = gameManager.getPowerUpManager();
        this.ballManager = gameManager.getBallManager();
        this.enemyManager = gameManager.getEnemyManager();
        this.collisionManager = gameManager.getCollisionManager();
        this.laserManager = gameManager.getLaserManager();

        this.paddle = new Paddle(
                GameConstants.PADDLE_X,
                GameConstants.PADDLE_Y,
                GameConstants.PADDLE_WIDTH,
                GameConstants.PADDLE_HEIGHT,
                0,
                0);

        this.ballManager.createInitialBall(this.paddle);

        handleBallLost = this::handleBallLost;
        handlePowerUpCollected = this::handlePowerUpCollected;
        handleLifeLost = this::handleLifeLost;
        handleLifeAdded = this::handleLifeAdded;

        if (gameManager.getLevelRepository() instanceof FileLevelRepository) {
            ScoreManager.getInstance().resetScore();
            LifeManager.getInstance().reset();
        }

        this.scoreFont = new Font("Arial", 24);

        this.currentLives = LifeManager.getInstance().getLives();

        try {
            pauseIcon = new Image(getClass().getResourceAsStream("/GameIcon/pause.png"));
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh pause.png từ resources!");
            e.printStackTrace();
        }
        subscribeToEvents();
        this.levelNumber = levelNumber;
        this.enemyManager.loadLevelScript(this.levelNumber);

        powerUpManager.spawnPowerUp("H", 400, 300);
        powerUpManager.spawnPowerUp("E", 400, 400);
    }

    private void subscribeToEvents() {
        EventManager.getInstance().subscribe(
                BallLostEvent.class,
                handleBallLost
        );
        EventManager.getInstance().subscribe(
                PowerUpCollectedEvent.class,
                handlePowerUpCollected
        );
        EventManager.getInstance().subscribe(
                LifeLostEvent.class,
                handleLifeLost
        );
        EventManager.getInstance().subscribe(
                LifeAddedEvent.class,
                handleLifeAdded
        );
        EventManager.getInstance().subscribe(
                LifeAddedEvent.class,
                this::handleLifeAdded
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

        laserManager.update(deltaTime);
        brickManager.update(deltaTime);
        ballManager.update(deltaTime);
        powerUpManager.update(deltaTime);
        enemyManager.update(deltaTime);

        if (collisionManager != null) {
            collisionManager.checkCollisions(
                    ballManager.getActiveBalls(),
                    paddle,
                    brickManager.getBricks(),
                    powerUpManager.getActivePowerUps(),
                    laserManager.getLasers(),
                    enemyManager.getActiveEnemies()
            );
        }
        handleVictory();
    }

    private void handleVictory() {
        if (this.hasWon || LifeManager.getInstance().getLives() <= 0) {
            return;
        }
        if (this.levelNumber == 5) {
            if (enemyManager.hasBossSpawned() && enemyManager.isBossDefeated()) {
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.VICTORY)
                );
            }
        } else {
            if (brickManager.isLevelComplete()) {
                this.hasWon = true;
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.VICTORY)
                );
            }
        }
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.drawImage(PlayingImage, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        //render
        brickManager.render(gc);
        ballManager.render(gc);
        powerUpManager.render(gc);
        laserManager.render(gc);
        enemyManager.render(gc);
        paddle.render(gc);
        renderScore(gc);

        renderPauseButton(gc);
        renderLives(gc);
    }

    private void renderPauseButton(GraphicsContext gc) {
        if (pauseIcon == null) return;
        double iconWidth = 40;
        double iconHeight = 40;
        double x = gc.getCanvas().getWidth() - iconWidth - 10;
        double y = 10;
        gc.drawImage(pauseIcon, x, y, iconWidth, iconHeight);
    }

    private void renderScore(GraphicsContext gc) {
        int currentScore = ScoreManager.getInstance().getScore();

        gc.setFont(scoreFont);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Score: " + currentScore, 10, 25);
    }

    private void renderLives(GraphicsContext gc) {
        gc.setFont(scoreFont);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Lives: " + this.currentLives, 10, GameConstants.SCREEN_HEIGHT - 10);
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

        if (input.isKeyPressed(KeyCode.P)) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PAUSED)
            );
            return;
        }

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
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.PAUSED)
                );
                return;
            } else {
                ballManager.releaseAttachedBalls();
            }
        }
        if (input.isKeyPressed(KeyCode.SPACE)) {
            ballManager.releaseAttachedBalls();
        }
    }

    private void updateAttachedBallPosition() {
        for (IBall ball : ballManager.getActiveBalls()) {
            if (ball.isAttachedToPaddle()) {
                ball.setPosition(
                        paddle.getX() + (paddle.getWidth() / 2.0) - (ball.getGameObject().getWidth() / 2.0),
                        paddle.getY() - ball.getGameObject().getHeight()
                );
            }
        }
    }

    public void addStrategy(PowerUpStrategy strategy) {
        Iterator<PowerUpStrategy> iterator = activeStrategies.iterator();
        while (iterator.hasNext()) {
            PowerUpStrategy now = iterator.next();
            if (now.getClass().equals(strategy.getClass())) {
                now.reset();
                return;
            }
        }
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

    public void cleanUp() {
        EventManager.getInstance().unsubscribe(
                BallLostEvent.class,
                handleBallLost
        );
        EventManager.getInstance().unsubscribe(
                PowerUpCollectedEvent.class,
                handlePowerUpCollected
        );
        EventManager.getInstance().unsubscribe(
                LifeLostEvent.class,
                handleLifeLost
        );
        EventManager.getInstance().unsubscribe(
                LifeAddedEvent.class,
                handleLifeAdded
        );

        for (PowerUpStrategy strategy : activeStrategies) {
            strategy.remove(this);
        }
        activeStrategies.clear();

        ballManager.clear();
        powerUpManager.clear();
        laserManager.clear();
    }

    private void handleBallLost(BallLostEvent event) {
        if (ballManager.countActiveBalls() == 0) {
            LifeManager.getInstance().loseLife();
        }
    }

    private void handleLifeLost(LifeLostEvent event) {
        this.currentLives = event.getRemainingLives();
        ballManager.resetBalls(this.paddle);
    }

    private void handleLifeAdded(LifeAddedEvent event) {
        this.currentLives = event.getRemainingLives();
    }

    public int getLevelNumber() {
        return this.levelNumber;
    }

    public int getCurrentLives() {
        return this.currentLives;
    }
}
