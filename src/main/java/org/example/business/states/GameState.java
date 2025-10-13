package org.example.business.states;

import javafx.scene.canvas.GraphicsContext;

public interface GameState {
    void update();
    void render(GraphicsContext gc);
    void handleInput();

}
