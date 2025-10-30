package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import org.example.presentation.InputHandler;

public interface GameState {
    void update(double deltaTime);
    void render(GraphicsContext gc);
    void handleInput(InputHandler inputHandler);
}