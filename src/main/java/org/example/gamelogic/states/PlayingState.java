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

/**
 * Quản lý trạng thái "Đang chơi" (Playing) cốt lõi của game.
 * <p>
 * Chịu trách nhiệm cho toàn bộ logic màn chơi, bao gồm update, render,
 * xử lý va chạm, và quản lý các trạng thái con (level start, boss warning).
 */
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

    private Image currentBackground;
    private Image bossBackground;
    private double backgroundTransitionTimer = 0.0;
    private final double BACKGROUND_TRANSITION_DURATION = 3.0;

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

    /**
     * Khởi tạo trạng thái Đang Chơi.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập toàn bộ môi trường chơi game cho một level.
     * Tải level, khởi tạo các manager, tạo paddle, bóng. Tải dữ liệu
     * (nếu load game) hoặc reset (nếu chơi mới). Đăng ký các sự kiện game.
     * <p>
     * <b>Expected:</b> Trạng thái sẵn sàng bắt đầu màn chơi,
     * {@code currentSubState} được đặt thành {@code LEVEL_START}.
     *
     * @param gameManager     Tham chiếu đến GameManager chính.
     * @param currentGameMode Chế độ chơi (LEVEL hoặc INFINITE).
     * @param levelNumber     Số thứ tự level (hoặc wave) để tải.
     * @param startingNewGame True nếu bắt đầu game mới (reset điểm/mạng),
     * False nếu tải từ session.
     */
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
        this.currentBackground = gameManager.getBackgroundForLevel(this.levelNumber);
        org.example.data.AssetManager am = org.example.data.AssetManager.getInstance();
        this.bossBackground = am.getImage("bossBackground");
        this.scoreFont = am.getFont("Anxel", 24);
        this.labelFont = am.getFont("Anxel", 18);
        this.valueFont = am.getFont("Anxel", 28);


        this.pauseIcon = am.getImage("pause");
        subscribeToEvents();

        this.currentSubState = SubState.LEVEL_START;
        this.levelStartTimer = 0.0;

        this.gameFrameImage = am.getImage("frame");
        this.hudFrameImage = am.getImage("hudFrame");
    }

    /**
     * (Helper) Đăng ký các hàm xử lý (handler)
     * vào {@link EventManager} cho các sự kiện game cốt lõi.
     * <p>
     * <b>Expected:</b> Các hàm {@code handleBallLost},
     * {@code handlePowerUpCollected},...
     * sẵn sàng nhận và xử lý sự kiện.
     */
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

    /**
     * (Helper) Xử lý khi nhận sự kiện {@link PowerUpCollectedEvent}.
     * <p>
     * <b>Định nghĩa:</b> Lấy chiến lược (strategy) từ power-up
     * và gọi {@link #addStrategy(PowerUpStrategy)}.
     * <p>
     * <b>Expected:</b> Chiến lược power-up mới được kích hoạt.
     *
     * @param event Sự kiện nhặt power-up.
     */
    private void handlePowerUpCollected(PowerUpCollectedEvent event) {
        if (event.getPowerUpCollected() != null && event.getPowerUpCollected().getStrategy() != null) {
            addStrategy(event.getPowerUpCollected().getStrategy());
        }
    }

    /**
     * Cập nhật logic game mỗi frame.
     * <p>
     * <b>Định nghĩa:</b> Thực thi logic update chính dựa trên
     * trạng thái con ({@code currentSubState}) hiện tại
     * (vd: LEVEL_START, NORMAL_PLAY, BOSS_WARNING...).
     * <p>
     * <b>Expected:</b> Tất cả đối tượng game (paddle, bóng, gạch, enemy),
     * power-up, và logic (va chạm) được cập nhật. Trạng thái con
     * được chuyển đổi khi đủ điều kiện.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
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
                    SoundManager.getInstance().playSound("siren");
                    this.backgroundTransitionTimer = 0.0;
                }
                if (enemyManager.isBossDying()) {
                    this.currentSubState = SubState.BOSS_DYING;
                    this.bossDyingTimer = 0.0;
                }

                handleVictory();
                break;

            case BOSS_WARNING:
                warningFlashTimer += deltaTime;
                this.backgroundTransitionTimer = Math.min(BACKGROUND_TRANSITION_DURATION, this.backgroundTransitionTimer + deltaTime);

                enemyManager.updateBossOnly(deltaTime);

                if (enemyManager.isBossReady() && warningFlashTimer >= warningFlashDuration) {
                    this.currentSubState = SubState.NORMAL_PLAY;
                    this.currentBackground = this.bossBackground;
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
                        EventManager.getInstance().publish(
                                new LevelCompletedEvent()
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

    /**
     * (Helper) Kiểm tra và xử lý điều kiện thắng màn chơi.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra nếu tất cả gạch đã bị phá
     * (và boss, nếu có, đã bị hạ).
     * <p>
     * <b>Expected:</b> Phát sự kiện {@link ChangeStateEvent} (sang VICTORY)
     * hoặc chuyển sang {@code SubState.WAVE_CLEARED} (chế độ INFINITE)
     * nếu thỏa mãn điều kiện thắng.
     */
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
                EventManager.getInstance().publish(
                        new LevelCompletedEvent()
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

    /**
     * Vẽ (render) toàn bộ màn chơi lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Xóa màn hình, vẽ HUD, khung game (frame),
     * nền (background), và tất cả các đối tượng game
     * (gạch, paddle, bóng, enemy,...) dựa trên {@code currentSubState}.
     * <p>
     * <b>Expected:</b> Toàn bộ giao diện màn chơi được hiển thị,
     * bao gồm các hiệu ứng đặc biệt (cảnh báo boss, fade-out, ...).
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

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
        if (currentSubState == SubState.BOSS_WARNING && this.bossBackground != null) {
            double alpha = this.backgroundTransitionTimer / BACKGROUND_TRANSITION_DURATION;

            if (this.currentBackground != null) {
                gc.drawImage(this.currentBackground,
                        GameConstants.PLAY_AREA_X, GameConstants.PLAY_AREA_Y,
                        GameConstants.PLAY_AREA_WIDTH, GameConstants.PLAY_AREA_HEIGHT);
            }

            gc.setGlobalAlpha(alpha);
            gc.drawImage(this.bossBackground,
                    GameConstants.PLAY_AREA_X, GameConstants.PLAY_AREA_Y,
                    GameConstants.PLAY_AREA_WIDTH, GameConstants.PLAY_AREA_HEIGHT);
            gc.setGlobalAlpha(1.0);

        } else if (this.currentBackground != null) {
            gc.drawImage(this.currentBackground,
                    GameConstants.PLAY_AREA_X, GameConstants.PLAY_AREA_Y,
                    GameConstants.PLAY_AREA_WIDTH, GameConstants.PLAY_AREA_HEIGHT);
        }

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
                gc.setFont(AssetManager.getInstance().getFont("Anxel", 80));

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
            gc.setFont(AssetManager.getInstance().getFont("Anxel", 80));

            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);

            gc.fillText("WAVE CLEARED",
                    GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH / 2,
                    GameConstants.PLAY_AREA_Y + GameConstants.PLAY_AREA_HEIGHT / 2);

            gc.setFont(AssetManager.getInstance().getFont("Anxel", 30));
            gc.fillText("Loading next wave, please wait warmly...",
                    GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH / 2,
                    GameConstants.PLAY_AREA_Y + GameConstants.PLAY_AREA_HEIGHT / 2 + 60);

            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BASELINE);
        }
    }

    /**
     * (Helper) Vẽ giao diện người dùng (HUD) bên thanh phải.
     * <p>
     * <b>Định nghĩa:</b> Hiển thị điểm (SCORE), thời gian (TIME),
     * màn (ROUND/WAVE), và mạng (LIVES).
     * <p>
     * <b>Expected:</b> Thanh HUD bên phải được vẽ với thông tin mới nhất.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
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

    /**
     * (Helper) Vẽ biểu tượng (icon) Pause.
     * <p>
     * <b>Định nghĩa:</b> Vẽ ảnh {@code pauseIcon}
     * tại vị trí cố định (góc trên bên phải).
     * <p>
     * <b>Expected:</b> Icon Pause được hiển thị trên màn hình.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    private void renderPauseButton(GraphicsContext gc) {
        if (pauseIcon == null) return;
        double iconWidth = 40;
        double iconHeight = 40;
        double x = gc.getCanvas().getWidth() - iconWidth - 10;
        double y = 10;
        gc.drawImage(pauseIcon, x, y, iconWidth, iconHeight);
    }

    /**
     * (Helper) Vẽ điểm số (score) (dùng cho HUD cũ).
     * <p>
     * <b>Định nghĩa:</b> Vẽ văn bản điểm số lên thanh UI dưới cùng.
     * <p>
     * <b>Expected:</b> Điểm số được hiển thị.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    private void renderScore(GraphicsContext gc) {
        int currentScore = ScoreManager.getInstance().getScore();

        gc.setFont(scoreFont);
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.LEFT);

        double x_pos = GameConstants.FRAME_LEFT_BORDER + 50;

        double y_pos = GameConstants.SCREEN_HEIGHT - (GameConstants.UI_BAR_HEIGHT / 2.0) + 8;

        gc.fillText("Score: " + currentScore, x_pos, y_pos);
    }

    /**
     * (Helper) Vẽ số mạng (lives) (dùng cho HUD cũ).
     * <p>
     * <b>Định nghĩa:</b> Vẽ văn bản số mạng lên thanh UI dưới cùng.
     * <p>
     * <b>Expected:</b> Số mạng được hiển thị.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    private void renderLives(GraphicsContext gc) {
        gc.setFont(scoreFont);
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.RIGHT);

        double x_pos = GameConstants.SCREEN_WIDTH - GameConstants.FRAME_RIGHT_BORDER - 50;
        double y_pos = GameConstants.SCREEN_HEIGHT - (GameConstants.UI_BAR_HEIGHT / 2.0) + 8;
        gc.fillText("Lives: " + this.currentLives, x_pos, y_pos);
    }

    /**
     * Xử lý input (phím, chuột) từ người chơi.
     * <p>
     * <b>Định nghĩa:</b> Điều khiển paddle (phím A/D, Trái/Phải).
     * Thả bóng (Space, Click chuột). Tạm dừng game (P, Click icon Pause).
     * <p>
     * <b>Expected:</b> Paddle di chuyển, bóng được thả,
     * hoặc game chuyển sang trạng thái PAUSED.
     *
     * @param input Nguồn cung cấp input (phím, chuột).
     */
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

    /**
     * (Helper) Cập nhật vị trí các quả bóng đang dính vào paddle.
     * <p>
     * <b>Định nghĩa:</b> Đảm bảo các bóng (ở trạng thái "attached")
     * di chuyển theo paddle.
     * <p>
     * <b>Expected:</b> Vị trí của bóng "attached" được cập nhật
     * theo vị trí paddle.
     */
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

    /**
     * Thêm một chiến lược (power-up) vào danh sách đang hoạt động.
     * <p>
     * <b>Định nghĩa:</b> Thêm {@code strategy} vào {@code activeStrategies}.
     * Nếu power-up cùng loại đã tồn tại, reset thời gian của nó.
     * <p>
     * <b>Expected:</b> Power-up được kích hoạt hoặc được làm mới thời gian.
     *
     * @param strategy Chiến lược power-up cần áp dụng.
     */
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

    /**
     * Cập nhật tất cả các power-up đang hoạt động.
     * <p>
     * <b>Định nghĩa:</b> Lặp qua {@code activeStrategies},
     * cập nhật thời gian, và gỡ bỏ nếu hết hạn.
     * <p>
     * <b>Expected:</b> Thời gian của các power-up giảm xuống,
     * và các power-up hết hạn bị vô hiệu hóa (removed).
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
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

    /**
     * Lấy đối tượng Paddle.
     * <p>
     * <b>Định nghĩa:</b> Trả về tham chiếu đến paddle của người chơi.
     * <p>
     * <b>Expected:</b> Đối tượng {@link Paddle} hiện tại.
     *
     * @return Paddle.
     */
    public Paddle getPaddle() {
        return paddle;
    }

    /**
     * Lấy đối tượng BallManager.
     * <p>
     * <b>Định nghĩa:</b> Trả về tham chiếu đến trình quản lý bóng.
     * <p>
     * <b>Expected:</b> Đối tượng {@link BallManager} hiện tại.
     *
     * @return BallManager.
     */
    public BallManager getBallManager() {
        return ballManager;
    }
    /**
     * Thu thập dữ liệu trạng thái game hiện tại để lưu.
     * <p>
     * <b>Định nghĩa:</b> Tạo một đối tượng {@link SavedGameState}
     * và điền dữ liệu (level, điểm, mạng, vị trí đối tượng) vào đó.
     * <p>
     * <b>Expected:</b> Một đối tượng {@code SavedGameState}
     * chứa toàn bộ thông tin cần thiết để khôi phục game.
     *
     * @return Đối tượng trạng thái đã lưu.
     */
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

    /**
     * Tải (load) game từ một trạng thái đã lưu.
     * <p>
     * <b>Định nghĩa:</b> Áp dụng dữ liệu từ {@link SavedGameState}
     * vào trạng thái chơi hiện tại (cập nhật điểm, mạng, vị trí
     * paddle, bóng, gạch, enemy).
     * <p>
     * <b>Expected:</b> Màn chơi được khôi phục về đúng trạng thái
     * đã lưu, và {@code currentSubState} được đặt thành {@code NORMAL_PLAY}.
     *
     * @param state Đối tượng trạng thái chứa dữ liệu cần tải.
     */
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
        this.currentSubState = SubState.NORMAL_PLAY;
    }

    /**
     * Dọn dẹp trạng thái trước khi thoát.
     * <p>
     * <b>Định nghĩa:</b> Hủy đăng ký (unsubscribe) tất cả các sự kiện
     * và gọi {@link #clearManagers()} để xóa các đối tượng game.
     * <p>
     * <b>Expected:</b> Trạng thái được dọn dẹp,
     * ngăn chặn rò rỉ bộ nhớ (memory leak) từ sự kiện.
     */
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

    /**
     * (Helper) Xóa sạch tất cả các đối tượng game đang hoạt động.
     * <p>
     * <b>Định nghĩa:</b> Hủy kích hoạt power-up,
     * xóa bóng, laser, enemy, và hạt (particle).
     * <p>
     * <b>Expected:</b> Tất cả các manager không còn đối tượng
     * (thường dùng khi chuyển level hoặc thoát).
     */
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

    /**
     * (Helper) Xử lý khi nhận {@link BallLostEvent}.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra nếu không còn bóng nào trên màn hình.
     * <p>
     * <b>Expected:</b> Gọi {@code LifeManager.loseLife()}
     * nếu đây là quả bóng cuối cùng.
     *
     * @param event Sự kiện mất bóng.
     */
    private void handleBallLost(BallLostEvent event) {
        if (ballManager.countActiveBalls() == 0) {
            LifeManager.getInstance().loseLife();
        }
    }

    /**
     * (Helper) Xử lý khi nhận {@link LifeLostEvent}.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code currentLives}
     * và reset bóng về vị trí paddle.
     * <p>
     * <b>Expected:</b> Số mạng hiển thị được cập nhật,
     * bóng mới xuất hiện dính vào paddle.
     *
     * @param event Sự kiện mất mạng.
     */
    private void handleLifeLost(LifeLostEvent event) {
        this.currentLives = event.getRemainingLives();
        ballManager.resetBalls(this.paddle);
    }

    /**
     * (Helper) Xử lý khi nhận {@link LifeAddedEvent}.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code currentLives}
     * với số mạng mới.
     * <p>
     * <b>Expected:</b> Số mạng hiển thị được cập nhật.
     *
     * @param event Sự kiện thêm mạng.
     */
    private void handleLifeAdded(LifeAddedEvent event) {
        this.currentLives = event.getRemainingLives();
    }

    /**
     * Lấy số thứ tự level hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code levelNumber}.
     * <p>
     * <b>Expected:</b> Số nguyên (int) của level hiện tại.
     *
     * @return Số level.
     */
    public int getLevelNumber() {
        return this.levelNumber;
    }

    /**
     * Lấy số mạng hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code currentLives}.
     * <p>
     * <b>Expected:</b> Số nguyên (int) của số mạng còn lại.
     *
     * @return Số mạng.
     */
    public int getCurrentLives() {
        return this.currentLives;
    }
}