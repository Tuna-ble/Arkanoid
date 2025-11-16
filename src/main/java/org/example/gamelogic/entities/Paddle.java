package org.example.gamelogic.entities;

import java.lang.Math;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;
import org.example.data.AssetManager;

public class Paddle extends MovableObject {
    private Image paddleImage;
    private double speed;
    private double minX;
    private double maxX;

    public Paddle(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height, dx, dy);
        this.speed = GameConstants.PADDLE_SPEED;
        this.minX = GameConstants.PLAY_AREA_X;
        this.maxX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH - this.width;

        this.paddleImage = AssetManager.getInstance().getImage("paddle");
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
        gc.drawImage(paddleImage, x, y, width, height);
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