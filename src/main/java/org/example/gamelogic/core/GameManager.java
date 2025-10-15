package org.example.gamelogic.core;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import org.example.data.ILevelRepository;
import org.example.gamelogic.states.GameState;
import org.example.gamelogic.states.PlayingState;

//singleton (co dung Bill Pugh Idiom de xu li multithreading)
public final class GameManager {
    private final AnimationTimer gameLoop;
    private StateManager stateManager;
    private BrickManager brickManager;
    private GraphicsContext gc;
    private ILevelRepository levelRepository;

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

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setLevelRepository(ILevelRepository repo) {
        this.levelRepository = repo;
    }

    public void init() {
        this.stateManager = new StateManager();
        this.brickManager = new BrickManager(levelRepository);
        GameState currentState = new PlayingState(this, 1);
        this.stateManager.setState(currentState);
    }

    public void update(double deltaTime) {
        if (stateManager != null) {
            stateManager.update(deltaTime);
        }
    }

    public void render() {
        GameState currentState = stateManager.getState();
        if (currentState != null && gc != null) {
            currentState.render(gc);
        }
        brickManager.render(gc);
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
}
