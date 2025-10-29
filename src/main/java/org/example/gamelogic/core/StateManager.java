package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.states.GameState;
import org.example.presentation.InputHandler;

public final class StateManager {
    private GameState currentState;

    private static class SingletonHolder {
        private static final StateManager INSTANCE = new StateManager();
    }

    public static StateManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setState(GameState state) {
        this.currentState = state;
    }

    public GameState getState() {
        return this.currentState;
    }

    public void update(double deltaTime) {
        if (currentState != null)
            currentState.update(deltaTime);
    }

    public void render(GraphicsContext gc) {
        if (currentState != null)
            currentState.render(gc);
    }

    public void handleInput(I_InputProvider input) {
        if (currentState != null)
            currentState.handleInput(input);
    }
}