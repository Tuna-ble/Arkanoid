package org.example.gamelogic.core;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.example.data.ILevelRepository;
import org.example.gamelogic.states.GameState;
import org.example.gamelogic.states.PlayingState;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;
import org.example.presentation.InputHandler;

import java.util.List;
import java.util.ArrayList;

//singleton (co dung Bill Pugh Idiom de xu li multithreading)
public final class GameManager {
    private final AnimationTimer gameLoop;
    private StateManager stateManager;
    private BrickManager brickManager;
    private PowerUpManager powerUpManager;
    private BallManager ballManager;
    private GraphicsContext gc;
    private ILevelRepository levelRepository;
    private InputHandler inputHandler;

    private List<PowerUpStrategy> activeStrategies = new ArrayList<>();

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
                handleInput();
                update(deltaTime);
                render();
                if (inputHandler != null) {
                    inputHandler.resetMouseClick();
                }
            }
        };
    }

    private static class SingletonHolder {
        private static final GameManager INSTANCE = new GameManager();
    }

    public static GameManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }
    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setLevelRepository(ILevelRepository repo) {
        this.levelRepository = repo;
    }

    public void addStrategy(PowerUpStrategy strategy) {
        activeStrategies.add(strategy);
        strategy.apply(this);
    }

    public void init() {
        this.stateManager = new StateManager();
        this.brickManager = new BrickManager(levelRepository);
        this.powerUpManager = new PowerUpManager();
        this.ballManager = new BallManager();
        GameState currentState = new PlayingState(this, 1);
        this.stateManager.setState(currentState);
    }

    public void updateStrategy(double deltaTime) {
        for (PowerUpStrategy strategy : activeStrategies) {
            strategy.update(this, deltaTime);
            if (strategy.isExpired()) {
                strategy.remove(this);
                activeStrategies.remove(strategy);
            }
        }
    }

    public void handleInput() {
        GameState currentState = stateManager.getState();
        if (currentState != null && this.inputHandler != null) {
            currentState.handleInput(this.inputHandler);
        }
    }

    public void update(double deltaTime) {
        if (stateManager != null) {
            stateManager.update(deltaTime);
        }
        updateStrategy(deltaTime);
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

    public BrickManager getBrickManager() {
        return this.brickManager;
    }

    public PowerUpManager getPowerUpManager() {
        return this.powerUpManager;
    }

    public BallManager getBallManager() {
        return this.ballManager;
    }
}