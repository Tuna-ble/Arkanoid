package org.example.gamelogic.strategy.transition.button;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.graphics.buttons.AbstractUIElement;

public interface IUIElementTransitionStrategy {
    void start();
    void update(double deltaTime);
    boolean isFinished();

    void renderElement(GraphicsContext gc, AbstractUIElement element);
}
