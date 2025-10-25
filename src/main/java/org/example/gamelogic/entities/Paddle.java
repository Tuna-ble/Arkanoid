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
        return x; // x là tâm
    }

    @Override
    // update paddle theo delta time
    public void update(double deltaTime) {
        x += dx * deltaTime;

        // Kiểm tra mép TRÁI của paddle
        if (x - (width / 2) < minX) {
            x = minX + (width / 2);
            dx = 0;
        }
        // Kiểm tra mép PHẢI của paddle
        if (x + (width / 2) > maxX) {
            x = maxX - (width / 2);
            dx = 0;
        }
    }

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
        double drawX = x;
        double drawY = y;

        gc.setFill(GameConstants.PADDLE_COLOR);
        gc.fillRoundRect(drawX, drawY, width, height, 10, 10);

        gc.setStroke(GameConstants.PADDLE_BORDER_COLOR);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(drawX, drawY, width, height, 10, 10);
    }

    public void setVelocity(double v, int i) {
        this.dx = v;
        this.dy = i;
    }
}
