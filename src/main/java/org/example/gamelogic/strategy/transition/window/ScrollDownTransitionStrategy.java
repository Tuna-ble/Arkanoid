package org.example.gamelogic.strategy.transition.window;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.windows.Window;

public class ScrollDownTransitionStrategy implements ITransitionStrategy {
    private double timer = 0.0;
    private final double DURATION = 0.4;

    private Image fillImage;

    public ScrollDownTransitionStrategy() {
        AssetManager am = AssetManager.getInstance();
        fillImage = am.getImage("hologram");
    }

    @Override
    public void update(double deltaTime) {
        if (timer < DURATION) {
            timer += deltaTime;
        }
    }

    @Override
    public boolean isFinished() {
        return timer >= DURATION;
    }

    @Override
    public void render(GraphicsContext gc, Window window) {
        double progress = Math.min(1.0, timer / DURATION);
        if (progress <= 0.0) return;

        double winX = window.getX();
        double winY = window.getY();
        double winWidth = window.getWidth();
        double winHeight = window.getHeight();

        double contentY = winY;
        double contentMaxHeight = winHeight;
        double currentContentHeight = contentMaxHeight * progress;

        if (fillImage != null && currentContentHeight > 0) {

            double sx = 0;
            double sy = 0;
            double sw = fillImage.getWidth();
            double sh = fillImage.getHeight() * progress;

            gc.drawImage(
                    fillImage,
                    sx, sy, sw, sh,
                    winX, contentY,
                    winWidth, currentContentHeight
            );
        }
    }

    @Override
    public void reset() {
        timer = 0.0;
    }
}
