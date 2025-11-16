package org.example.gamelogic.states;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.data.AssetManager;
import org.example.config.GameConstants;
import org.example.data.FileLevelRepository;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.*;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.events.*;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;
import org.example.data.SavedGameState;
import org.example.data.SaveGameRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public final class PlayingState implements GameState {
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
    private Image gameFrameImage;
    private Image hudFrameImage;
    private double elapsedTime = 0.0;
    private String formattedTime = "00:00";
    private int lastSecond = -1;
    private Font labelFont;
    private Font valueFont;

    private List<PowerUpStrategy> activeStrategies = new ArrayList<>();
    private int levelNumber;

    private final Consumer<BallLostEvent> handleBallLost;
    private final Consumer<PowerUpCollectedEvent> handlePowerUpCollected;
    private final Consumer<LifeLostEvent> handleLifeLost;
    private final Consumer<LifeAddedEvent> handleLifeAdded;

    private enum SubState {
        LEVEL_START,
        NORMAL_PLAY,
        BOSS_WARNING,
        BOSS_DYING,
        WAVE_CLEARED
    }

    private GameModeEnum currentGameMode;
    private SubState currentSubState;

    private double levelStartTimer = 0.0;
    private final double LEVEL_START_DURATION = 1.5;

    private double warningFlashTimer = 0.0;
    private double warningFlashDuration = 7.0;

    private double bossDyingTimer = 0.0;
    private final double BOSS_DEATH_DURATION = 5.0;

    private double waveClearedTimer = 0.0;
    private final double WAVE_CLEARED_DURATION = 3.0;

    public PlayingState(GameManager gameManager, GameModeEnum currentGameMode,
                        int levelNumber, boolean startingNewGame) {
        this.gameManager = gameManager;
        this.brickManager = gameManager.getBrickManager();
        this.brickManager.loadLevel(levelNumber);
        this.powerUpManager = gameManager.getPowerUpManager();
        this.ballManager = gameManager.getBallManager();
        this.enemyManager = gameManager.getEnemyManager();
        this.collisionManager = gameManager.getCollisionManager();
        this.laserManager = gameManager.getLaserManager();

        this.paddle = new Paddle(
                GameConstants.PLAY_AREA_X + (GameConstants.PLAY_AREA_WIDTH / 2.0) - (GameConstants.PADDLE_WIDTH / 2.0),
                GameConstants.PLAY_AREA_Y + GameConstants.PLAY_AREA_HEIGHT - GameConstants.PADDLE_HEIGHT - 45,
                GameConstants.PADDLE_WIDTH,
                GameConstants.PADDLE_HEIGHT,
                0,
                0);
        ObjectAccess.getInstance().registerPaddle(this.paddle);

        this.ballManager.createInitialBall(this.paddle);

        handleBallLost = this::handleBallLost;
        handlePowerUpCollected = this::handlePowerUpCollected;
        handleLifeLost = this::handleLifeLost;
        handleLifeAdded = this::handleLifeAdded;

        this.currentGameMode = currentGameMode;
        Map<String, String> data = ProgressManager.loadSession(currentGameMode.toString());
        if (!data.isEmpty() && !startingNewGame) {
            ScoreManager.getInstance().resetScore();
            ScoreManager.getInstance().addScore(Integer.parseInt(data.get("score")));
            elapsedTime = Double.parseDouble(data.get("time"));
            this.levelNumber = Integer.parseInt(data.get("level"));
            this.currentLives = Integer.parseInt(data.get("lives"));
            LifeManager.getInstance().setLives(this.currentLives);
        } else if (startingNewGame) {
            ScoreManager.getInstance().resetScore();
            LifeManager.getInstance().reset();
            this.currentLives = LifeManager.getInstance().getLives();
            this.levelNumber = levelNumber;
        }

        this.scoreFont = new Font("Arial", 24);
        this.labelFont = new Font("Arial", 18);
        this.valueFont = new Font("Arial", 28);

        org.example.data.AssetManager am = org.example.data.AssetManager.getInstance();
        this.pauseIcon = am.getImage("pause");
        subscribeToEvents();

        this.currentSubState = SubState.LEVEL_START;
        this.levelStartTimer = 0.0;

        this.gameFrameImage = am.getImage("frame");
        this.hudFrameImage = am.getImage("hudFrame");
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
    }

    private void handlePowerUpCollected(PowerUpCollectedEvent event) {
        if (event.getPowerUpCollected() != null && event.getPowerUpCollected().getStrategy() != null) {
            addStrategy(event.getPowerUpCollected().getStrategy());
        }
    }

    @Override
    public void update(double deltaTime) {
        if (currentSubState == SubState.NORMAL_PLAY) {
            elapsedTime += deltaTime;
        }
        int totalSeconds = (int) elapsedTime;
        if (totalSeconds != lastSecond) {
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            this.formattedTime = String.format("%02d:%02d", minutes, seconds);
            this.lastSecond = totalSeconds;
        }

        switch (currentSubState) {
            case LEVEL_START:
                if (levelStartTimer == 0) {
                    ProgressManager.saveSession(currentGameMode.toString(),
                            ScoreManager.getInstance().getScore(),
                            elapsedTime,
                            levelNumber,
                            LifeManager.getInstance().getLives());
                }

                levelStartTimer += deltaTime;

                if (levelStartTimer >= LEVEL_START_DURATION) {
                    this.enemyManager.loadLevelScript(this.currentGameMode, this.levelNumber);
                    this.currentSubState = SubState.NORMAL_PLAY;
                    levelStartTimer = 0;
                }
                break;

            case NORMAL_PLAY:
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
                            paddle, brickManager.getBricks(),
                            powerUpManager.getActivePowerUps(),
                            laserManager.getLasers(),
                            enemyManager.getActiveEnemies()
                    );
                }
                if (((this.currentGameMode == GameModeEnum.LEVEL && this.levelNumber == 5) ||
                        (this.currentGameMode == GameModeEnum.INFINITE && this.levelNumber % 5 == 0)) &&
                        brickManager.isLevelComplete() &&
                        !enemyManager.hasBossSpawned()) {
                    enemyManager.spawnEnemy("BOSS", GameConstants.PLAY_AREA_X
                            + GameConstants.PLAY_AREA_WIDTH / 2, -GameConstants.BOSS_HEIGHT);

                    this.currentSubState = SubState.BOSS_WARNING;
                    this.warningFlashTimer = 0.0;
                }
                if (enemyManager.isBossDying()) {
                    this.currentSubState = SubState.BOSS_DYING;
                    this.bossDyingTimer = 0.0;
                }

                handleVictory();
                break;

            case BOSS_WARNING:
                warningFlashTimer += deltaTime;

                enemyManager.updateBossOnly(deltaTime);

                if (enemyManager.isBossReady() && warningFlashTimer >= warningFlashDuration) {
                    this.currentSubState = SubState.NORMAL_PLAY;
                }
                break;

            case BOSS_DYING:
                bossDyingTimer += deltaTime;

                enemyManager.updateBossOnly(deltaTime);

                if (bossDyingTimer >= BOSS_DEATH_DURATION && enemyManager.isBossDefeated()) {
                    if (this.currentGameMode == GameModeEnum.LEVEL) {
                        EventManager.getInstance().publish(
                                new ChangeStateEvent(GameStateEnum.VICTORY)
                        );
                    } else if (this.currentGameMode == GameModeEnum.INFINITE) {
                        clearManagers();
                        this.currentSubState = SubState.WAVE_CLEARED;
                    }
                }
                break;

            case WAVE_CLEARED:
                waveClearedTimer += deltaTime;
                if (waveClearedTimer >= WAVE_CLEARED_DURATION) {
                    levelNumber++;

                    brickManager.loadLevel(levelNumber);
                    ballManager.createInitialBall(this.paddle);

                    waveClearedTimer = 0;
                    hasWon = false;

                    currentSubState = SubState.LEVEL_START;
                }
                break;
        }
    }

    private void handleVictory() {
        if (this.hasWon || LifeManager.getInstance().getLives() <= 0) {
            return;
        }
        if (currentGameMode == GameModeEnum.LEVEL && this.levelNumber != 5) {
            if (brickManager.isLevelComplete()) {
                this.hasWon = true;
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.VICTORY)
                );
            }
        } else if (currentGameMode == GameModeEnum.INFINITE && this.levelNumber % 5 != 0) {
            if (brickManager.isLevelComplete()) {
                clearManagers();
                this.hasWon = true;
                this.currentSubState = SubState.WAVE_CLEARED;
                this.waveClearedTimer = 0;
            }
        }
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setFill(Color.DARKBLUE);
        gc.fillRect(0, 0,
                GameConstants.SCREEN_WIDTH - GameConstants.UI_BAR_WIDTH,
                GameConstants.SCREEN_HEIGHT);

        gc.setFill(Color.BLACK);
        gc.fillRect(GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH + GameConstants.FRAME_RIGHT_BORDER,
                0,
                GameConstants.UI_BAR_WIDTH + GameConstants.FRAME_RIGHT_BORDER,
                GameConstants.SCREEN_HEIGHT);

        renderHUD(gc);
        if (gameFrameImage != null) {
            gc.drawImage(gameFrameImage, 0, 0,
                    GameConstants.SCREEN_WIDTH - GameConstants.UI_BAR_WIDTH, GameConstants.SCREEN_HEIGHT);
        }

        gc.save();
        gc.beginPath();
        gc.rect(GameConstants.PLAY_AREA_X, GameConstants.PLAY_AREA_Y,
                GameConstants.PLAY_AREA_WIDTH, GameConstants.PLAY_AREA_HEIGHT);
        gc.clip();

        if (currentSubState == SubState.LEVEL_START) {
            brickManager.render(gc, levelStartTimer, LEVEL_START_DURATION);
            paddle.render(gc);
            ballManager.render(gc);

        } else {
            brickManager.render(gc);
            enemyManager.render(gc);
            powerUpManager.render(gc);
            ballManager.render(gc);
            laserManager.render(gc);
            paddle.render(gc);
            ParticleManager.getInstance().render(gc);
        }

        gc.restore();
        renderPauseButton(gc);

        if (currentSubState == SubState.BOSS_WARNING) {
            if ((warningFlashTimer % 0.8) < 0.5) {

                gc.setFill(Color.RED);
                gc.setFont(new Font("Arial", 80));

                gc.setTextAlign(TextAlignment.CENTER);
                gc.setTextBaseline(VPos.CENTER);

                gc.fillText("WARNING",
                        GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH / 2,
                        GameConstants.PLAY_AREA_Y + GameConstants.PLAY_AREA_HEIGHT / 2);

                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.BASELINE);
            }
        }

        if (currentSubState == SubState.BOSS_DYING && currentGameMode == GameModeEnum.LEVEL) {
            double fadeAlpha = Math.min(1.0, bossDyingTimer / BOSS_DEATH_DURATION);

            gc.save();
            try {
                gc.setFill(Color.color(1.0, 1.0, 1.0, fadeAlpha));
                gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
            } finally {
                gc.restore();
            }
        }

        if (currentSubState == SubState.WAVE_CLEARED) {
            gc.setFill(Color.GREEN);
            gc.setFont(new Font("Arial", 80));

            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);

            gc.fillText("WAVE CLEARED",
                    GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH / 2,
                    GameConstants.PLAY_AREA_Y + GameConstants.PLAY_AREA_HEIGHT / 2);

            gc.setFont(new Font("Arial", 30));
            gc.fillText("Loading next wave, please wait warmly...",
                    GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH / 2,
                    GameConstants.PLAY_AREA_Y + GameConstants.PLAY_AREA_HEIGHT / 2 + 60);

            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BASELINE);
        }
    }

    private void renderHUD(GraphicsContext gc) {
        double hudAreaStartX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH + GameConstants.FRAME_RIGHT_BORDER;
        double hudAreaWidth = GameConstants.UI_BAR_WIDTH;
        double hudCenterX = hudAreaStartX + (hudAreaWidth / 2.0);

        double startY = 150.0;
        double spacingY = 120.0;

        int currentScore = ScoreManager.getInstance().getScore();

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        gc.setFont(labelFont);
        gc.fillText("SCORE", hudCenterX, startY);
        gc.setFont(valueFont);
        gc.fillText(String.valueOf(currentScore), hudCenterX, startY + 40);

        gc.setFont(labelFont);
        gc.fillText("TIME", hudCenterX, startY + spacingY);
        gc.setFont(valueFont);
        gc.fillText(this.formattedTime, hudCenterX, startY + spacingY + 40);

        gc.setFont(labelFont);
        gc.fillText((currentGameMode == GameModeEnum.LEVEL ? "ROUND" : "WAVE"), hudCenterX, startY + (spacingY * 2));
        gc.setFont(valueFont);
        gc.fillText(String.valueOf(this.levelNumber), hudCenterX, startY + (spacingY * 2) + 40);

        gc.setFont(labelFont);
        gc.fillText("LIVES", hudCenterX, startY + (spacingY * 3));
        gc.setFont(valueFont);
        gc.fillText(String.valueOf(this.currentLives), hudCenterX, startY + (spacingY * 3) + 40);
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
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.LEFT);

        double x_pos = GameConstants.FRAME_LEFT_BORDER + 50;

        double y_pos = GameConstants.SCREEN_HEIGHT - (GameConstants.UI_BAR_HEIGHT / 2.0) + 8;

        gc.fillText("Score: " + currentScore, x_pos, y_pos);
    }

    private void renderLives(GraphicsContext gc) {
        gc.setFont(scoreFont);
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.RIGHT);

        double x_pos = GameConstants.SCREEN_WIDTH - GameConstants.FRAME_RIGHT_BORDER - 50;
        double y_pos = GameConstants.SCREEN_HEIGHT - (GameConstants.UI_BAR_HEIGHT / 2.0) + 8;
        gc.fillText("Lives: " + this.currentLives, x_pos, y_pos);
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

            strategy.update(this, deltaTime);

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
    public SavedGameState collectGameStateToSave() {
        SavedGameState state = new SavedGameState();

        state.levelId = this.levelNumber;
        state.score = ScoreManager.getInstance().getScore();
        state.lives = LifeManager.getInstance().getLives();

        state.paddleX = paddle.getX();
        state.paddleY = paddle.getY();
        state.paddleWidth = paddle.getWidth();

        state.balls = ballManager.getDataToSave();
        state.bricks = brickManager.getDataToSave();
        state.enemies = enemyManager.getDataToSave();
        System.out.println("Collected game state (level " + this.levelNumber + ") to save.");
        return state;
    }

    public void loadGame(SavedGameState state) {
        System.out.println("Applying saved state for level " + state.levelId);

        ScoreManager.getInstance().setScore(state.score);
        LifeManager.getInstance().setLives(state.lives);

        this.levelNumber = state.levelId;
        this.currentLives = state.lives;

        System.out.println("--- DEBUG PADDLE ---");
        System.out.println("Đang tải vị trí Paddle X: " + state.paddleX);

        paddle.setPosition(state.paddleX, state.paddleY);
        paddle.setWidth(state.paddleWidth);
        ballManager.loadData(state.balls);
        brickManager.loadData(state.bricks);
        enemyManager.loadData(state.enemies);

        // 5. Cập nhật lại các thứ khác nếu cần
        // vd: tgian choi
        // this.elapsedTime = state.elapsedTime; // (Nếu bạn thêm elapsedTime vào SavedGameState)
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

        clearManagers();
    }

    private void clearManagers() {
        for (PowerUpStrategy strategy : activeStrategies) {
            strategy.remove(this);
        }
        activeStrategies.clear();

        ballManager.clear();
        powerUpManager.clear();
        laserManager.clear();
        enemyManager.clear();
        ParticleManager.getInstance().clear();
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
