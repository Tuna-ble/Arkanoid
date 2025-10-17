package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;

//mo dau cua State design pattern
public interface GameState {
    void update(double deltaTime);
    void render(GraphicsContext gc);
    void handleInput();
}
