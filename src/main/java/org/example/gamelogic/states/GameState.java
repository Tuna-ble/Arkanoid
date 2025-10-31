package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.I_InputProvider;

public interface GameState {
    void update(double deltaTime);
    void render(GraphicsContext gc);
    void handleInput(I_InputProvider input);
}