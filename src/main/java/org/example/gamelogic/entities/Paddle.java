package org.example.gamelogic.entities;

import java.lang.Math;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;

public class Paddle extends MovableObject {
    private double speed;
    private double minX;
    private double maxX;

    public Paddle(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height, dx, dy);
        this.speed = GameConstants.PADDLE_SPEED;
        this.minX = 0;
        this.maxX = GameConstants.SCREEN_WIDTH;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    @Override
    // update paddle theop delta time
    public void update(double deltaTime) {
        x += dx * deltaTime;

        if (x < minX) {
            x = minX;
            dx = 0;
        }
        if (x + width > maxX) {
            x = maxX - width;
            dx = 0;
        }
    }

    // set up state cho paddle
    public void moveLeft() {
        dx = -speed;
    }

    public void moveRight() {
        dx = speed;
    }

    public void stop() {
        dx = 0;
    }

    public void setBounds(double minX, double maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    // setter and getter
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    @Override
    public void render(GraphicsContext gc) {
        double drawX = x - width / 2;
        double drawY = y; // y vẫn tính từ trên xuống, nên không cần trừ nửa height

        gc.setFill(GameConstants.PADDLE_COLOR);
        gc.fillRoundRect(drawX, drawY, width, height, 10, 10);

        gc.setStroke(GameConstants.PADDLE_BORDER_COLOR);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(drawX, drawY, width, height, 10, 10);
    }

}