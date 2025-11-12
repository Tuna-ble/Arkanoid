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
        this.minX = GameConstants.PLAY_AREA_X;
        this.maxX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - this.width;
    }

    public double getCenterX() {
        return x + width / 2.0;
    }

    @Override
    // update paddle theo delta time
    public void update(double deltaTime) {
        x += dx * deltaTime;

        // Kiểm tra mép TRÁI của paddle
        if (x < minX) {
            x = minX;
            dx = 0;
        }
        // Kiểm tra mép PHẢI của paddle
        if (x > maxX) {
            x = maxX;
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
        this.minX = GameConstants.PLAY_AREA_X;
        this.maxX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - this.width;
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

    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        this.maxX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - this.width;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}