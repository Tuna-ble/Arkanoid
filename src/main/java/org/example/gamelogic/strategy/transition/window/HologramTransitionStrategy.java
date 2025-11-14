package org.example.gamelogic.strategy.transition.window;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.windows.Window;

public class HologramTransitionStrategy implements ITransitionStrategy {
    private double timer = 0.0;
    private final double DURATION = 0.5;

    private Image imgTL, imgTR, imgBL, imgBR;
    private double startX, startY;

    public HologramTransitionStrategy() {
        AssetManager am = AssetManager.getInstance();
        imgTL = am.getImage("holo_tl");
        imgTR = am.getImage("holo_tr");
        imgBL = am.getImage("holo_bl");
        imgBR = am.getImage("holo_br");

        this.startX = GameConstants.SCREEN_WIDTH / 2.0;
        this.startY = GameConstants.SCREEN_HEIGHT / 2.0;
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

        double targetX_TL = targetX;
        double targetY_TL = targetY;
        double targetX_TR = targetX + targetWidth - (imgTR != null ? imgTR.getWidth() : 0);
        double targetY_TR = targetY;
        double targetX_BL = targetX;
        double targetY_BL = targetY + targetHeight - (imgBL != null ? imgBL.getHeight() : 0);
        double targetX_BR = targetX + targetWidth - (imgBR != null ? imgBR.getWidth() : 0);
        double targetY_BR = targetY + targetHeight - (imgBR != null ? imgBR.getHeight() : 0);

        double currentX_TL = lerp(startX, targetX_TL, progress);
        double currentY_TL = lerp(startY, targetY_TL, progress);

        double currentX_TR = lerp(startX, targetX_TR, progress);
        double currentY_TR = lerp(startY, targetY_TR, progress);

        double currentX_BL = lerp(startX, targetX_BL, progress);
        double currentY_BL = lerp(startY, targetY_BL, progress);

        double currentX_BR = lerp(startX, targetX_BR, progress);
        double currentY_BR = lerp(startY, targetY_BR, progress);

        gc.drawImage(imgTL, currentX_TL, currentY_TL,
                GameConstants.UI_WINDOW_CORNER_SIZE, GameConstants.UI_WINDOW_CORNER_SIZE);
        gc.drawImage(imgTR, currentX_TR, currentY_TR,
                GameConstants.UI_WINDOW_CORNER_SIZE, GameConstants.UI_WINDOW_CORNER_SIZE);
        gc.drawImage(imgBL, currentX_BL, currentY_BL,
                GameConstants.UI_WINDOW_CORNER_SIZE, GameConstants.UI_WINDOW_CORNER_SIZE);
        gc.drawImage(imgBR, currentX_BR, currentY_BR,
                GameConstants.UI_WINDOW_CORNER_SIZE, GameConstants.UI_WINDOW_CORNER_SIZE);

        Image fillImage = AssetManager.getInstance().getImage("holo_fill");
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

    private double lerp(double start, double end, double progress) {
        return start + (end - start) * progress;
    }
}
