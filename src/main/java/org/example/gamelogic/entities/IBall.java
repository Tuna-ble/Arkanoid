package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.bricks.Brick;

public interface IBall {
    boolean isDestroyed();
    boolean isAttachedToPaddle();
    void setPosition(double x, double y);
    void release();
    double getX();
    double getY();
    double getRadius();
    void reverseDirX();
    void reverseDirY();
    void reset(double paddleX, double paddleY, double paddleWidth);
    void handlePaddleCollision(Paddle paddle, double hitPositionRatio);
    void destroy();
    IBall clone();

    void update(double deltaTime);
    void render(GraphicsContext gc);
    boolean isActive();
    double getWidth();
    double getHeight();

    GameObject getGameObject();
}
