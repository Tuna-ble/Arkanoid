package org.example.gamelogic.strategy.transition.button;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.graphics.buttons.AbstractUIElement;

public class WipeElementTransitionStrategy implements IUIElementTransitionStrategy {
    private double duration;
    private double timer;
    private boolean started;
    private boolean finished;

    public WipeElementTransitionStrategy(double duration) {
        this.duration = (duration <= 0) ? 0.1 : duration;
        this.timer = 0;
        this.started = false;
        this.finished = false;
    }

    @Override public void start() { this.started = true; }
    @Override public boolean isFinished() { return finished; }

    @Override
    public void update(double deltaTime) {
        if (!started || finished) return;
        timer += deltaTime;
        if (timer >= duration) {
            timer = duration;
            finished = true;
        }
    }

    @Override
    public void renderElement(GraphicsContext gc, AbstractUIElement element) {
        double progress = timer / duration;
        if (progress <= 0.0) return;

        double currentWidth = element.getWidth() * progress;

        gc.save();

        try {
            gc.beginPath();
            gc.rect(
                    element.getX(),
                    element.getY(),
                    currentWidth,
                    element.getHeight()
            );
            gc.clip();

            element.renderDefault(gc);

        } finally {
            gc.restore();
        }
    }
}
