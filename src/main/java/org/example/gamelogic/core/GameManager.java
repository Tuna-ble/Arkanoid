package org.example.gamelogic.core;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.example.data.ILevelRepository;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.events.*;
import org.example.gamelogic.states.*;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

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

    private GraphicsContext gc;
    private ILevelRepository levelRepository;

    private List<PowerUpStrategy> activeStrategies = new ArrayList<>();

    private I_InputProvider inputProvider;

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

                update(deltaTime);
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

    public void addStrategy(PowerUpStrategy strategy) {
        activeStrategies.add(strategy);
        strategy.apply(this);
    }

    public void init() {
        this.stateManager = new StateManager();
        this.brickManager = new BrickManager(levelRepository);
        this.powerUpManager = new PowerUpManager();
        this.ballManager = new BallManager();
        this.collisionManager = new CollisionManager();
        GameState currentState = new MainMenuState();
        this.stateManager.setState(currentState);

        subscribeToEvents();
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

    public void update(double deltaTime) {
        if (stateManager != null && inputProvider != null) {
            stateManager.handleInput(inputProvider);
            stateManager.update(deltaTime);
        }
        updateStrategy(deltaTime);
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
        /*EventManager.getInstance().subscribe(
                LifeLostEvent.class,
                this::onHit
        );
        EventManager.getInstance().subscribe(
                LevelCompletedEvent.class,
                this::onHit
        );
        EventManager.getInstance().subscribe(
                GameOverEvent.class,
                this::GameOver
        );*/

        EventManager.getInstance().subscribe(
                BallLostEvent.class,
                this::handleBallLost
        );
        EventManager.getInstance().subscribe(
                ChangeStateEvent.class,
                this::handleStateChangeRequest
        );
    }

    private void resetBallAndPaddle() {
        GameState currentState = stateManager.getState();
        if (currentState instanceof PlayingState) {
            PlayingState playingState = (PlayingState) currentState;
            ballManager.resetBalls(playingState.getPaddle());
        }
    }

    private void handleBallLost(BallLostEvent event) {
        gameOver();
    }

    public void gameOver() {
        GameState gameOverState = new GameOverState();
        stateManager.setState(gameOverState);
    }

    public void handleStateChangeRequest(ChangeStateEvent event) {
        GameState newState = null;
        switch (event.targetState) {
            case PLAYING:
                newState = new PlayingState(this, 1);
                break;
            case MAIN_MENU:
                newState = new MainMenuState();
                break;
            case GAME_OVER:
                newState = new GameOverState();
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

    public CollisionManager getCollisionManager() {
        return this.collisionManager;
    }
}