package org.example.presentation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteAnimation {
    private final Image spriteSheet;
    private final int frameCount;
    private final int frameWidth;
    private final int frameHeight;
    private final int columns;
    private final double frameDuration;

    private double elapsedTime = 0;
    private int currentFrame = 0;
    private boolean loops = true;
    private boolean isFinished = false;

    public SpriteAnimation(Image sheet, int frameCount, int columns, double duration, boolean loops) {
        this.spriteSheet = sheet;
        this.frameCount = frameCount;
        this.columns = columns;
        this.frameWidth = (int) (sheet.getWidth() / columns);
        this.frameHeight = (int) (sheet.getHeight() / (Math.ceil((double) frameCount / columns)));
        this.frameDuration = duration / frameCount;
        this.loops = loops;
    }

    public void update(double deltaTime) {
        if (isFinished) return;

        elapsedTime += deltaTime;
        if (elapsedTime >= frameDuration) {
            elapsedTime -= frameDuration;
            currentFrame++;
            if (currentFrame >= frameCount) {
                if (loops) {
                    currentFrame = 0;
                } else {
                    isFinished = true;
                    currentFrame = frameCount - 1;
                }
            }
        }
    }

    public void render(GraphicsContext gc, double x, double y, double w, double h) {
        int sx = (currentFrame % columns) * frameWidth;
        int sy = (currentFrame / columns) * frameHeight;

        gc.drawImage(spriteSheet,
                sx, sy, frameWidth, frameHeight, // Vùng nguồn
                x, y, w, h); // Vùng đích
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void reset() {
        this.currentFrame = 0;
        this.elapsedTime = 0;
        this.isFinished = false;
    }
}