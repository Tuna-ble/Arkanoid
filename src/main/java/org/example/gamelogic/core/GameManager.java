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

    public static GameManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setInputProvider(I_InputProvider provider) {
        this.inputProvider = provider;
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setLevelRepository(ILevelRepository repo) {
        this.levelRepository = repo;
        this.brickManager.setLevelRepository(repo);
    }

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

    private void initializeLevelBackgrounds(int numLevels) {
        for (int i = 1; i <= numLevels; i++) {
            String randomBgKey = backgroundKeys.get(random.nextInt(backgroundKeys.size()));

            levelBackgroundMap.put(i, randomBgKey);
            System.out.println("Gán ảnh : Level " + i + " -> " + randomBgKey);
        }
    }

    public Image getBackgroundForLevel(int levelId) {
        String key = levelBackgroundMap.get(levelId);

        if (key == null) {
            int index = (levelId - 1) % backgroundKeys.size();
            key = backgroundKeys.get(index);
        }

        return AssetManager.getInstance().getImage(key);
    }

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

    public void render() {
        GameState currentState = stateManager.getState();
        if (currentState != null && gc != null) {
            currentState.render(gc);
        }
        if (particleManager != null && gc != null) {
            particleManager.render(gc);
        }
    }

    public void startGameLoop() {
        gameLoop.start();
    }

    public void stopGameLoop() {
        gameLoop.stop();
    }

    private void subscribeToEvents() {
        EventManager.getInstance().subscribe(
                ChangeStateEvent.class,
                this::handleStateChangeRequest
        );
    }

    private void resetBallAndPaddle() {
        currentState = stateManager.getState();
        if (currentState instanceof PlayingState) {
            PlayingState playingState = (PlayingState) currentState;
            ballManager.resetBalls(playingState.getPaddle());
        }
    }

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

    public void startNewGame() {
        GameState newPlayingState = new PlayingState(this, currentGameMode, 1, true);
        stateManager.setState(newPlayingState);
    }

    public void goToMainMenu() {
        GameState mainMenuState = new MainMenuState();
        stateManager.setState(mainMenuState);
    }

    public ILevelRepository getLevelRepository() {
        return levelRepository;
    }

    public BrickManager getBrickManager() {
        return this.brickManager;
    }

    public PowerUpManager getPowerUpManager() {
        return this.powerUpManager;
    }

    public BallManager getBallManager() {
        return this.ballManager;
    }

    public StateManager getStateManager() {
        return this.stateManager;
    }

    public CollisionManager getCollisionManager() {
        return this.collisionManager;
    }

    public LaserManager getLaserManager() {
        return this.laserManager;
    }

    public ParticleManager getParticleManager() {
        return this.particleManager;
    }

    public EnemyManager getEnemyManager() {
        return this.enemyManager;
    }

    public GameModeEnum getCurrentGameMode() { return this.currentGameMode; }
}
