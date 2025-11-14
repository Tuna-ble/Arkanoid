package org.example.gamelogic.graphics.windows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.states.GameState;

public class HologramWindow extends AbstractWindow {
    private double transitionTimer = 0.0;
    private final double TRANSITION_DURATION = 0.5;
    private final double HOLO_WINDOW_PADDING = 100.0; // Padding của khung

    // Tọa độ của "quyển sách" (khung hologram)
    private double windowX, windowY, windowWidth, windowHeight;
    // Tọa độ 4 góc của "quyển sách"
    private double targetX_TL, targetY_TL, targetX_TR, targetY_TR;
    private double targetX_BL, targetY_BL, targetX_BR, targetY_BR;

    // Tọa độ tâm (điểm bắt đầu)
    private double startX, startY;

    // 4 ảnh góc
    private Image imgTL, imgTR, imgBL, imgBR, imgFill;

    public HologramWindow(GameState previousState) {
        super(previousState);
        AssetManager am = AssetManager.getInstance();
        imgTL = am.getImage("holo_tl");
        imgTR = am.getImage("holo_tr");
        imgBL = am.getImage("holo_bl");
        imgBR = am.getImage("holo_br");
        imgFill = am.getImage("holo_fill");

        // 2. Tính toán vị trí
        // Điểm bắt đầu (tâm màn hình)
        this.startX = GameConstants.SCREEN_WIDTH / 2.0;
        this.startY = GameConstants.SCREEN_HEIGHT / 2.0;

        // Tính toán hình chữ nhật đích (quyển sách)
        this.windowX = HOLO_WINDOW_PADDING;
        this.windowY = HOLO_WINDOW_PADDING;
        this.windowWidth = GameConstants.SCREEN_WIDTH - (HOLO_WINDOW_PADDING * 2);
        this.windowHeight = GameConstants.SCREEN_HEIGHT - (HOLO_WINDOW_PADDING * 2);

        // Tính 4 góc đích
        this.targetX_TL = windowX;
        this.targetY_TL = windowY;
        this.targetX_TR = windowX + windowWidth - (imgTR != null ? imgTR.getWidth() : 0);
        this.targetY_TR = windowY;
        this.targetX_BL = windowX;
        this.targetY_BL = windowY + windowHeight - (imgBL != null ? imgBL.getHeight() : 0);
        this.targetX_BR = windowX + windowWidth - (imgBR != null ? imgBR.getWidth() : 0);
        this.targetY_BR = windowY + windowHeight - (imgBR != null ? imgBR.getHeight() : 0);
    }

    @Override
    public void update(double deltaTime) {
        if (isTransitioningIn) {
            transitionTimer += deltaTime;
            if (transitionTimer >= TRANSITION_DURATION) {
                isTransitioningIn = false;
                transitionTimer = TRANSITION_DURATION;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // 1. Gọi hàm render của cha (để vẽ nền + overlay)
        super.render(gc);

        // 2. Tính toán và vẽ 4 góc hologram (logic lerp)
        double progress = transitionTimer / TRANSITION_DURATION;
        if (imgFill != null) {
            // Tính toán kích thước hiện tại (từ 0% đến 100%)
            double currentWidth = windowWidth * progress;
            double currentHeight = windowHeight * progress;

            // Tính toán tọa độ X, Y để giữ cho ảnh luôn ở tâm
            double currentX = windowX + (windowWidth - currentWidth) / 2.0;
            double currentY = windowY + (windowHeight - currentHeight) / 2.0;

            // Vẽ ảnh nền (đã được scale)
            gc.drawImage(imgFill, currentX, currentY, currentWidth, currentHeight);
        }

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

        if (!isTransitioningIn) {
            isFinished = true;
            for (AbstractButton btn : buttons) {
                btn.render(gc);
            }
        }
    }

    private double lerp(double start, double end, double progress) {
        return start + (end - start) * progress;
    }
}
