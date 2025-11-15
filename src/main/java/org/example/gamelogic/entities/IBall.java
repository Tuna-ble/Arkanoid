package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.bricks.Brick;

import java.util.List;

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
    void multiplySpeed(double factor);
    void destroy();
    IBall clone();
    IBall duplicate();

    void update(double deltaTime);
    void render(GraphicsContext gc);
    boolean isActive();
    double getWidth();
    double getHeight();

    GameObject getGameObject();

    double getDx();
    double getDy();

    double getSpeed();

    void setDx(double v);
    void setDy(double v);

    void setPierceLeft(int pierceLeft);
    int getPierceLeft();
    List<GameObject> getPiercingObjects();
}
