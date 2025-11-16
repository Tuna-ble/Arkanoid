package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.strategy.transition.button.IUIElementTransitionStrategy;

public abstract class AbstractUIElement {
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    protected IUIElementTransitionStrategy transition;
    protected boolean transitionStarted = false;
    protected boolean transitionFinished;
    protected boolean isDisabled = false;

    public AbstractUIElement(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.transitionFinished = false;
    }

    public void setTransition(IUIElementTransitionStrategy transition) {
        this.transition = transition;
        if (transition == null) {
            this.transitionFinished = true;
        }
    }

    public void startTransition() {
        if (transition != null && !transitionStarted) {
            transition.start();
            transitionStarted = true;
        } else if (transition == null) {
            transitionFinished = true;
        }
    }

    public void updateTransition(double deltaTime) {
        if (transition != null && transitionStarted && !transitionFinished) {
            transition.update(deltaTime);
            if (transition.isFinished()) {
                transitionFinished = true;
            }
        }
    }

    public void render(GraphicsContext gc) {
        gc.save();
        try {
            if (this.isDisabled) {
                gc.setGlobalAlpha(0.5);
            }
            if (transition != null && !transitionFinished) {
                transition.renderElement(gc, this);
            }
            else {
                renderDefault(gc);
            }
        } finally {
            gc.restore();
        }

    }

    public abstract void renderDefault(GraphicsContext gc);

    public abstract void handleInput(I_InputProvider input);

    public boolean isTransitionFinished() {
        return (transition == null) || transitionFinished;
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
