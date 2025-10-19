package org.example.gamelogic.states;

import javafx.scene.paint.Color;
import org.example.gamelogic.core.BrickManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.core.PowerUpManager;

public final class PlayingState implements GameState {
    BrickManager brickManager;
    PowerUpManager powerUpManager;
    GameManager gameManager;

    public PlayingState(GameManager gameManager, int levelNumber) {
        this.gameManager=gameManager;
        this.brickManager = gameManager.getBrickManager();
        this.brickManager.loadLevel(levelNumber);
        this.powerUpManager=gameManager.getPowerUpManager();

        powerUpManager.spawnPowerUp("E", 400, 100);
        powerUpManager.spawnPowerUp("F", 500, 100);
    }

    @Override
    public void update(double deltaTime) {
        brickManager.update(deltaTime);
        powerUpManager.update(gameManager);
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(Color.PINK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        //render
    }

    @Override
    public void handleInput() {

    }
}
