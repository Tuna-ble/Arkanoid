package org.example.gamelogic.states;

import javafx.scene.paint.Color;

public final class PlayingState implements GameState {
    @Override
    public void update(double deltaTime) {
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
