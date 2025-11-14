package org.example.gamelogic.strategy.transition.window;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.graphics.windows.Window;

public interface ITransitionStrategy {
    void update(double deltaTime);
    void render(GraphicsContext gc, Window window);
    boolean isFinished();
    void reset();
}
