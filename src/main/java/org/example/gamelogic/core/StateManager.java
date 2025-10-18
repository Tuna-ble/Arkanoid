package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.states.GameState;

//singleton (co dung Bill Pugh Idiom de xu li multithreading)
public final class StateManager {
    private GameState currentState;

    public void setState(GameState state) {
        this.currentState = state;
    }

    public void update() {
        if (currentState != null)
            currentState.update();
    }

    public void render(GraphicsContext gc) {
        if (currentState != null)
            currentState.render(gc);
    }

    public void handleInput() {
        if (currentState != null)
            currentState.handleInput();
    }
}
