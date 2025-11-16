package org.example.presentation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;


public class RowAnimation {
    private final Image spriteSheet;
    private final int spriteRow;
    private final int totalFrames;
    private final double frameDuration;

    private int currentFrame = 0;
    private double frameTimer = 0.0;

    private final double SPRITE_OFFSET = GameConstants.POWERUP_SPRITE_OFFSET;
    private final double SPRITE_WIDTH = GameConstants.POWERUP_SPRITE_WIDTH;
    private final double SPRITE_HEIGHT = GameConstants.POWERUP_SPRITE_HEIGHT;
    private final double SPRITE_PADDING = GameConstants.POWERUP_SPRITE_PADDING;

    public RowAnimation(Image spriteSheet, int spriteRow, int totalFrames, double frameDuration) {
        this.spriteSheet = spriteSheet;
        this.spriteRow = spriteRow;
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
    }

    public void update(double deltaTime) {
        frameTimer += deltaTime;
        if (frameTimer >= frameDuration) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % totalFrames;
        }
    }

    public void render(GraphicsContext gc, double x, double y, double w, double h) {
        if (spriteSheet == null) return;

        double sourceX = SPRITE_OFFSET + currentFrame * (SPRITE_WIDTH + SPRITE_PADDING);
        double sourceY = SPRITE_OFFSET + spriteRow * (SPRITE_HEIGHT + SPRITE_PADDING);

        gc.drawImage(
                spriteSheet,
                sourceX, sourceY, SPRITE_WIDTH, SPRITE_HEIGHT,
                x, y, w, h
        );
    }
}