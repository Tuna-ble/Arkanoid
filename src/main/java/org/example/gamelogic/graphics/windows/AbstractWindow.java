package org.example.gamelogic.graphics.windows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.states.GameState;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWindow {
    protected List<AbstractButton> buttons = new ArrayList<>();
    protected GameState previousState;
    protected boolean isFinished = false;
    protected boolean isTransitioningIn = true;

    protected AbstractWindow(GameState previousState) {
        this.previousState = previousState;
    }

    public abstract void update(double deltaTime);

    public void render(GraphicsContext gc) {
        if (previousState != null) {
            previousState.render(gc);
        }

        gc.setFill(new Color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
    }

    public void handleInput(I_InputProvider input) {
        if (isTransitioningIn || input == null) {
            return;
        }

        for (AbstractButton btn : buttons) {
            btn.update(input);
        }
    }

    public void addButton(AbstractButton button) {
        this.buttons.add(button);
    }

    public boolean isFinished() {
        return this.isFinished;
    }
}
