package org.example.gamelogic.graphics.windows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractUIElement;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.states.GameState;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;

import java.util.ArrayList;
import java.util.List;

public class Window {
    private double x, y, width, height;;

    protected List<AbstractUIElement> elements = new ArrayList<>();
    protected GameState previousState;
    private ITransitionStrategy windowTransition;

    private boolean windowTransitionFinished = false;
    private boolean childrenTransitionsStarted = false;

    public Window(GameState previousState, double windowWidth, double windowHeight,
                  ITransitionStrategy windowTransition) {
        this.previousState = previousState;

        this.width = windowWidth;
        this.height = windowHeight;
        this.x = GameConstants.SCREEN_WIDTH / 2.0 - windowWidth / 2.0;
        this.y = GameConstants.SCREEN_HEIGHT / 2.0 - windowHeight / 2.0;
        this.windowTransition = windowTransition;
        if (this.windowTransition == null) {
            this.windowTransitionFinished = true;
        }
    }

    public void update(double deltaTime) {
        if (!windowTransitionFinished) {
            windowTransition.update(deltaTime);
            if (windowTransition.isFinished()) {
                windowTransitionFinished = true;
            }
        }

        if (windowTransitionFinished) {

            if (!childrenTransitionsStarted) {
                for (AbstractUIElement element : elements) {
                    element.startTransition();
                }
                childrenTransitionsStarted = true;
            }

            for (AbstractUIElement element : elements) {
                element.updateTransition(deltaTime);
            }
        }
    }

    public void render(GraphicsContext gc) {
        if (previousState != null) {
            previousState.render(gc);
            gc.save();
            try {
                gc.setFill(new Color(0, 0, 0, 0.6));
                gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
            } finally {
                gc.restore();
            }

        }

        if (windowTransition != null) {
            windowTransition.render(gc, this);
        }
        if (windowTransitionFinished) {
            for (AbstractUIElement element : elements) {
                element.render(gc);
            }
        }
    }

    public void renderContents(GraphicsContext gc) {
        for (AbstractUIElement element : elements) {
            element.render(gc);
        }
    }

    public void handleInput(I_InputProvider input) {
        boolean allChildrenFinished = true;
        for (AbstractUIElement element : elements) {
            if (!element.isTransitionFinished()) {
                allChildrenFinished = false;
                break;
            }
        }

        if (windowTransitionFinished && allChildrenFinished) {
            for (AbstractUIElement element : elements) {
                element.handleInput(input);
            }
        }
    }

    public void addButton(AbstractUIElement element) {
        this.elements.add(element);
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public boolean transitionFinished() {
        return this.windowTransitionFinished;
    }

    public List<AbstractUIElement> getElements() {
        return this.elements;
    }
}
