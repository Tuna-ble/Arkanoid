package org.example.gamelogic.core;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.data.ILevelRepository;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.events.*;
import org.example.gamelogic.states.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

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

    private GraphicsContext gc;
    private ILevelRepository levelRepository;
    private I_InputProvider inputProvider;
    private GameState currentState;

    private double accumulator = 0.0;
    private final double FIXED_TIMESTEP = GameConstants.FIXED_TIMESTEP;

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
                    // 1. Cập nhật logic với BƯỚC THỜI GIAN CỐ ĐỊNH
                    // Lưu ý: Không truyền "deltaTime" nữa, mà là "FIXED_TIMESTEP"
                    update(FIXED_TIMESTEP);

                    // 2. Trừ đi thời gian đã xử lý
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
    }

    public void init() {
        this.stateManager = new StateManager();
        this.brickManager = new BrickManager(levelRepository);
        this.powerUpManager = new PowerUpManager();
        this.ballManager = new BallManager();
        this.collisionManager = new CollisionManager();
        currentState = new MainMenuState();
        this.stateManager.setState(currentState);

        this.soundManager = SoundManager.getInstance();
        this.scoreManager = ScoreManager.getInstance();
        this.lifeManager = LifeManager.getInstance();

        subscribeToEvents();
    }

    public void update(double deltaTime) {
        if (stateManager != null && inputProvider != null) {
            stateManager.handleInput(inputProvider);
            stateManager.update(deltaTime);
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
            powerUpManager.clear();
            ballManager.clear();
        }

        switch (event.targetState) {
            case PLAYING:
                if (currentState instanceof PlayingState) {
                    ((PlayingState) currentState).cleanUp();
                    powerUpManager.clear();
                    ballManager.clear();
                }
                newState = new PlayingState(this, levelToLoad);
                break;
            case LEVEL_STATE:
                newState = new LevelState();
                break;

            case RANKING_STATE:
                newState = new RankingState();
                break;

            case MAIN_MENU:
                if (currentState instanceof PlayingState) {
                    ((PlayingState) currentState).cleanUp();
                    powerUpManager.clear();
                    ballManager.clear();
                }
                newState = new MainMenuState();
                break;
            case GAME_OVER:
                int currentLevel = 1;

                if (currentState instanceof PlayingState) {
                    PlayingState playingState = (PlayingState) currentState;
                    currentLevel = playingState.getLevelNumber();
                    int finalScore = ScoreManager.getInstance().getScore();
                    HighscoreManager.saveNewScore(finalScore);

                    playingState.cleanUp();
                    powerUpManager.clear();
                    ballManager.clear();
                }
                newState = new GameOverState(currentLevel);
                break;
            case PAUSED:
                if (currentState instanceof PlayingState) {
                    newState = new PauseState(this, currentState);
                }
                break;
            case RESUME_GAME:
                if (currentState instanceof PauseState) {
                    newState = ((PauseState) currentState).getPreviousState();
                }
                break;
        }

        if (newState != null && stateManager != null) {
            stateManager.setState(newState);
        }
    }

    public void startNewGame() {
        GameState newPlayingState = new PlayingState(this, 1);
        stateManager.setState(newPlayingState);
    }

    public void goToMainMenu() {
        GameState mainMenuState = new MainMenuState();
        stateManager.setState(mainMenuState);
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
}