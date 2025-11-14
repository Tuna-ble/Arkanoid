package org.example.gamelogic.strategy.transition.window;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.windows.Window;

public class ScrollDownTransitionStrategy implements ITransitionStrategy {
    private double timer = 0.0;
    private final double DURATION = 0.4;

    private Image imgScrollBar;
    private Image imgScrollFill;
    private double barHeight;

    public ScrollDownTransitionStrategy() {
        AssetManager am = AssetManager.getInstance();
        imgScrollBar = am.getImage("scroll_bar");
        imgScrollFill = am.getImage("scroll_fill");
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

        if (imgScrollBar != null) {
            gc.drawImage(imgScrollBar, winX, winY, winWidth, barHeight);
        }

        double contentY = winY + barHeight;
        double contentMaxHeight = winHeight - barHeight;
        double currentContentHeight = contentMaxHeight * progress;

        if (imgScrollFill != null && currentContentHeight > 0) {

            double sx = 0;
            double sy = 0;
            double sw = imgScrollFill.getWidth();
            double sh = imgScrollFill.getHeight() * progress;

            gc.drawImage(
                    imgScrollFill,
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
