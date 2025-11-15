package org.example.gamelogic.strategy.transition.window;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.windows.Window;

public class HologramTransitionStrategy implements ITransitionStrategy {
    private double timer = 0.0;
    private final double DURATION = 0.5;

    private final Image fillImage;
    private double startX, startY;

    public HologramTransitionStrategy() {
        this.startX = GameConstants.SCREEN_WIDTH / 2.0;
        this.startY = GameConstants.SCREEN_HEIGHT / 2.0;
        fillImage = AssetManager.getInstance().getImage("hologram");
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

        double targetX = window.getX();
        double targetY = window.getY();
        double targetWidth = window.getWidth();
        double targetHeight = window.getHeight();

        double currentW = targetWidth * progress;
        double currentH = targetHeight * progress;
        double currentX = targetX + (targetWidth - currentW) / 2.0;
        double currentY = targetY + (targetHeight - currentH) / 2.0;

        gc.drawImage(fillImage, currentX, currentY, currentW, currentH);

        if (isFinished()) {
            window.renderContents(gc);
        }
    }

    @Override
    public void reset() {
        timer = 0.0;
    }
}
