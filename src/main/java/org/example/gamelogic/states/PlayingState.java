package org.example.gamelogic.states;

import javafx.scene.paint.Color;
import org.example.gamelogic.core.BrickManager;
import org.example.gamelogic.core.GameManager;

public final class PlayingState implements GameState {
    BrickManager brickManager;

    public PlayingState(GameManager gameManager, int levelNumber) {
        this.brickManager = gameManager.getBrickManager();
        this.brickManager.loadLevel(levelNumber);
    }

    @Override
    public void update(double deltaTime) {
        brickManager.update(deltaTime);
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        //render
    }

    @Override
    public void handleInput() {

    }
}
