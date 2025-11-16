package org.example.gamelogic.strategy.transition.window;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.windows.Window;

public class PopupTransitionStrategy implements ITransitionStrategy {
    private double timer = 0.0;
    private final double DURATION = 0.3;

    private Image fillImage;

    @Override
    public void update(double deltaTime) {
        if (timer < DURATION) timer += deltaTime;
    }

    @Override
    public boolean isFinished() {
        return timer >= DURATION;
    }

    @Override
    public void render(GraphicsContext gc, Window window) {
        double alpha = Math.min(1.0, timer / DURATION);

        double targetX = window.getX();
        double targetY = window.getY();
        double targetWidth = window.getWidth();
        double targetHeight = window.getHeight();
        AssetManager am = AssetManager.getInstance();
        fillImage = am.getImage("popup_fill");

        gc.save();
        try {
            gc.setGlobalAlpha(alpha);
            gc.drawImage(fillImage, targetX, targetY, targetWidth, targetHeight);
            window.renderContents(gc);
        } finally {
            gc.restore();
        }
    }

    @Override
    public void reset() {
        timer = 0.0;
    }
}
