package org.example.business.entities;

import java.lang.Math;
import org.example.data.GameConstants;

public class Paddle extends MovableObject {
    private double speed;
    private double minX;
    private double maxX;

    public Paddle() {
        super(GameConstants.PADDLE_CENTER_X - GameConstants.PADDLE_WIDTH / 2.0,
                GameConstants.PADDLE_Y,
                GameConstants.PADDLE_WIDTH,
                GameConstants.PADDLE_HEIGHT);
        this.speed = GameConstants.PADDLE_SPEED;
        this.minX = 0;
        this.maxX = GameConstants.SCREEN_WIDTH;
    }

    public double getCenterX() {
        return x + width / 2;
    }

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

    /**
     * demo va chạm với ball
     */
    public double getHitPosition(Ball ball) {
        double ballCenter = ball.getCenterX();
        double paddleCenter = getCenterX();
        double relativePosition = (ballCenter - paddleCenter) / (width / 2);
        return Math.max(-1, Math.min(1, relativePosition));
    }

    // setter
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
}
