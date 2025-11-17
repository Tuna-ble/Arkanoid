package org.example.gamelogic.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.data.FileLevelRepository;
import org.example.data.ILevelRepository;
import org.example.data.InfiniteLevelRepository;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.states.*;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.Map;

//singleton (co dung Bill Pugh Idiom de xu li multithreading)
public final class GameManager {
    private final AnimationTimer gameLoop;
    private StateManager stateManager;
    private BrickManager brickManager;
    private PowerUpManager powerUpManager;
    private BallManager ballManager;
    private CollisionManager collisionManager;
    private SoundManager soundManager;
    private ScoreManager scoreManager;
    private LifeManager lifeManager;
    private LaserManager laserManager;
    private EnemyManager enemyManager;

    private GraphicsContext gc;
    private ILevelRepository levelRepository;
    private I_InputProvider inputProvider;
    private GameState currentState;
    private GameModeEnum currentGameMode;

    private double accumulator = 0.0;
    private final double FIXED_TIMESTEP = GameConstants.FIXED_TIMESTEP;
    private ParticleManager particleManager;

    private Map<Integer, String> levelBackgroundMap;
    private List<String> backgroundKeys;
    private Random random;

    /**
     * Khởi tạo GameManager và thiết lập vòng lặp game (game loop).
     *
     * <p>Constructor private theo pattern singleton.
     */
    private GameManager() {
        this.gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                accumulator += deltaTime;

                while (accumulator >= FIXED_TIMESTEP) {
                    update(FIXED_TIMESTEP);
                    accumulator -= FIXED_TIMESTEP;
                }
                render();
            }
        };
    }

    private static class SingletonHolder {
        private static final GameManager INSTANCE = new GameManager();
    }

    /**
     * Lấy instance đơn của GameManager.
     *
     * @return singleton GameManager
     */
    public static GameManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Đăng ký provider cho input (keyboard/mouse) để GameManager có thể chuyển tiếp input tới state.
     *
     * @param provider implementation của I_InputProvider
     */
    public void setInputProvider(I_InputProvider provider) {
        this.inputProvider = provider;
    }

    /**
     * Thiết lập GraphicsContext dùng để vẽ (canvas).
     *
     * @param gc GraphicsContext (kỳ vọng không null)
     */
    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }

    /**
     * Thiết lập repository chứa layout level và truyền xuống BrickManager.
     *
     * @param repo implementation của ILevelRepository
     */
    public void setLevelRepository(ILevelRepository repo) {
        this.levelRepository = repo;
        this.brickManager.setLevelRepository(repo);
    }

    /**
     * Khởi tạo các thành phần con của game (managers, state, assets) và đăng ký event.
     * Gọi khi bắt đầu ứng dụng để chuẩn bị game.
     */
    public void init() {
        this.stateManager = new StateManager();
        this.brickManager = new BrickManager(levelRepository);
        this.powerUpManager = new PowerUpManager();
        this.ballManager = new BallManager();
        this.collisionManager = new CollisionManager();

        currentState = new MainMenuState();
        currentGameMode = GameModeEnum.LEVEL;
        this.stateManager.setState(currentState);

        AssetManager.getInstance();
        
        this.levelBackgroundMap = new HashMap<>();
        this.backgroundKeys = List.of("bg1", "bg2", "bg3");
        this.random = new Random();
        initializeLevelBackgrounds(5);
        
        this.enemyManager = EnemyManager.getInstance();
        this.soundManager = SoundManager.getInstance();
        this.scoreManager = ScoreManager.getInstance();
        this.lifeManager = LifeManager.getInstance();
        this.laserManager = LaserManager.getInstance();
        this.particleManager = ParticleManager.getInstance();

        this.enemyManager.setBrickManager(this.brickManager);
        this.soundManager.playSelectedMusic();

        subscribeToEvents();
    }

    /**
     * Tạo map ánh xạ level -> background key (random từ danh sách backgroundKeys).
     *
     * @param numLevels số level cần khởi tạo background
     */
    private void initializeLevelBackgrounds(int numLevels) {
        for (int i = 1; i <= numLevels; i++) {
            String randomBgKey = backgroundKeys.get(random.nextInt(backgroundKeys.size()));

            levelBackgroundMap.put(i, randomBgKey);
            System.out.println("Gán ảnh : Level " + i + " -> " + randomBgKey);
        }
    }

    /**
     * Lấy ảnh nền (Image) phù hợp cho level yêu cầu.
     *
     * @param levelId id level (1-based)
     * @return Image tương ứng với level
     */
    public Image getBackgroundForLevel(int levelId) {
        String key = levelBackgroundMap.get(levelId);

        if (key == null) {
            int index = (levelId - 1) % backgroundKeys.size();
            key = backgroundKeys.get(index);
        }

        return AssetManager.getInstance().getImage(key);
    }

    /**
     * Cập nhật các thành phần game chính (state, particles, ...) theo delta time.
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
    public void update(double deltaTime) {
        if (stateManager != null && inputProvider != null) {
            stateManager.handleInput(inputProvider);
            stateManager.update(deltaTime);
        }
        if (particleManager != null) {
            particleManager.update(deltaTime);
        }

        if (inputProvider != null) {
            inputProvider.resetMouseClick();
        }
    }

    /**
     * Thực hiện render khung hình hiện tại bằng cách ủy quyền cho state và particle manager.
     */
    public void render() {
        GameState currentState = stateManager.getState();
        if (currentState != null && gc != null) {
            currentState.render(gc);
        }
        if (particleManager != null && gc != null) {
            particleManager.render(gc);
        }
    }

    /**
     * Bắt đầu vòng lặp game (animation timer).
     */
    public void startGameLoop() {
        gameLoop.start();
    }

    /**
     * Dừng vòng lặp game.
     */
    public void stopGameLoop() {
        gameLoop.stop();
    }

    /**
     * Đăng ký các listener cần thiết vào EventManager (ví dụ thay đổi state).
     */
    private void subscribeToEvents() {
        EventManager.getInstance().subscribe(
                ChangeStateEvent.class,
                this::handleStateChangeRequest
        );
    }

    /**
     * Reset trạng thái ball và paddle khi chuyển trạng thái cần dọn dẹp (ví dụ restart level).
     */
    private void resetBallAndPaddle() {
        currentState = stateManager.getState();
        if (currentState instanceof PlayingState) {
            PlayingState playingState = (PlayingState) currentState;
            ballManager.resetBalls(playingState.getPaddle());
        }
    }

    /**
     * Xử lý yêu cầu thay đổi trạng thái từ Event (ChangeStateEvent).
     *
     * @param event sự kiện chứa thông tin trạng thái đích và payload (nếu có)
     */
    public void handleStateChangeRequest(ChangeStateEvent event) {
        GameState newState = null;
        GameState currentState = stateManager.getState();

        int levelToLoad = 1;
        if (event.getPayload() != null && event.getPayload() instanceof Integer) {
            levelToLoad = (Integer) event.getPayload();
        }

        if (currentState instanceof PlayingState && event.targetState != GameStateEnum.PAUSED && event.targetState != GameStateEnum.RESUME_GAME) {
            ((PlayingState) currentState).cleanUp();
        }

        switch (event.targetState) {
            case PLAYING:
                if (currentState instanceof PauseState) {
                    ((PauseState) currentState).cleanUp();
                }
                if (currentGameMode == GameModeEnum.INFINITE) {
                    Map<String, String> data = ProgressManager.loadSession(currentGameMode.toString());
                    if (data.isEmpty()) {
                        newState = new PlayingState(this, currentGameMode, 1, true);
                    } else {
                        newState = new PlayingState(this, currentGameMode, Integer.parseInt(data.get("level")), false);
                    }
                } else {
                    newState = new PlayingState(this, currentGameMode, levelToLoad, true);
                }

                break;

            case GAME_MODE:
                newState = new GameModeState();
                break;

            case LEVEL_STATE:
                this.setLevelRepository(new FileLevelRepository());
                this.currentGameMode = GameModeEnum.LEVEL;
                newState = new LevelState();
                break;

            case INFINITE_MODE:
                this.setLevelRepository(new InfiniteLevelRepository());
                this.currentGameMode = GameModeEnum.INFINITE;
                newState = new InfiniteModeState();
                break;

            case RANKING_STATE:
                newState = new RankingState();
                break;

            case MAIN_MENU:
                if (currentState instanceof PlayingState) {
                    ((PlayingState) currentState).cleanUp();
                } else if (currentState instanceof PauseState) {
                    ((PauseState) currentState).cleanUp();
                } else if (currentState instanceof ConfirmQuitToMenuState) {
                    ((ConfirmQuitToMenuState) currentState).cleanUp();
                }
                newState = new MainMenuState();
                soundManager.playSelectedMusic();
                break;

            case VICTORY:
                if (currentState instanceof PlayingState) {
                    PlayingState playingState = (PlayingState) currentState;

                    int livesLeft = playingState.getCurrentLives();
                    int levelCompleted = playingState.getLevelNumber();
                    int finalScore = ScoreManager.getInstance().getScore();

                    HighscoreManager.saveNewScore(finalScore);

                    playingState.cleanUp();
                    soundManager.stopMusic();
                    newState = new VictoryState(livesLeft, levelCompleted);
                }
                break;

            case GAME_OVER:
                int currentLevel = levelToLoad;

                if (currentState instanceof PlayingState) {
                    PlayingState playingState = (PlayingState) currentState;
                    if (currentGameMode == GameModeEnum.LEVEL) {
                        currentLevel = playingState.getLevelNumber();
                    }
                    else {
                         ProgressManager.clearSession(currentGameMode.toString());
                    }

                    int finalScore = ScoreManager.getInstance().getScore();
                    HighscoreManager.saveNewScore(finalScore);

                    playingState.cleanUp();
                    soundManager.stopMusic();
                }
                newState = new GameOverState(currentLevel);
                break;

            case PAUSED:
                if (currentState instanceof PlayingState) {
                    newState = new PauseState(currentState);
                } else if (currentState instanceof ConfirmQuitToMenuState) {
                    newState = new PauseState(((ConfirmQuitToMenuState) currentState).getPreviousState());
                }
                break;

            case RESUME_GAME:
                if (currentState instanceof PauseState) {
                    newState = ((PauseState) currentState).getPreviousState();
                } else if (currentState instanceof SettingsState) {
                    newState = ((SettingsState) currentState).getPreviousState();
                }
                break;

            case SETTINGS:
                newState = new SettingsState(currentState);
                break;

            case CONFIRM_RESET:
                newState = new ConfirmResetState();
                break;

            case CONFIRM_QUIT_TO_MENU:
                if (currentState instanceof PauseState) {
                    newState = new ConfirmQuitToMenuState(((PauseState) currentState).getPreviousState());
                }
                break;
        }

        if (newState != null && stateManager != null) {
            stateManager.setState(newState);
        }
    }

    /**
     * Bắt đầu một game mới và chuyển state sang PlayingState.
     */
    public void startNewGame() {
        GameState newPlayingState = new PlayingState(this, currentGameMode, 1, true);
        stateManager.setState(newPlayingState);
    }

    /**
     * Chuyển về Main Menu.
     */
    public void goToMainMenu() {
        GameState mainMenuState = new MainMenuState();
        stateManager.setState(mainMenuState);
    }

    /**
     * Lấy repository dùng để nạp level.
     *
     * @return ILevelRepository hiện tại (có thể null)
     */
    public ILevelRepository getLevelRepository() {
        return levelRepository;
    }

    /**
     * Lấy BrickManager quản lý gạch.
     *
     * @return BrickManager
     */
    public BrickManager getBrickManager() {
        return this.brickManager;
    }

    /**
     * Lấy PowerUpManager.
     *
     * @return PowerUpManager
     */
    public PowerUpManager getPowerUpManager() {
        return this.powerUpManager;
    }

    /**
     * Lấy BallManager.
     *
     * @return BallManager
     */
    public BallManager getBallManager() {
        return this.ballManager;
    }

    /**
     * Lấy StateManager hiện tại.
     *
     * @return StateManager
     */
    public StateManager getStateManager() {
        return this.stateManager;
    }

    /**
     * Lấy CollisionManager.
     *
     * @return CollisionManager
     */
    public CollisionManager getCollisionManager() {
        return this.collisionManager;
    }

    /**
     * Lấy LaserManager.
     *
     * @return LaserManager
     */
    public LaserManager getLaserManager() {
        return this.laserManager;
    }

    /**
     * Lấy ParticleManager.
     *
     * @return ParticleManager
     */
    public ParticleManager getParticleManager() {
        return this.particleManager;
    }

    /**
     * Lấy EnemyManager.
     *
     * @return EnemyManager
     */
    public EnemyManager getEnemyManager() {
        return this.enemyManager;
    }

    /**
     * Lấy chế độ chơi hiện tại.
     *
     * @return GameModeEnum hiện tại
     */
    public GameModeEnum getCurrentGameMode() { return this.currentGameMode; }
}
