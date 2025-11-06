package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.GameObject;

public interface Enemy {
    void takeDamage(double damage);
    void setPosition(double x, double y);
    double getX();
    double getY();
    double getDx();
    double getDy();
    double getWidth();
    double getHeight();
    Enemy clone();

    void update(double deltaTime);
    void render(GraphicsContext gc);

    void setActive(boolean active);
    boolean isActive();
    boolean isOutOfBounds();
    boolean isDestroyed();
    void destroy();

    public GameObject getGameObject();

    void setDx(double dx);
    void setDy(double dy);
    void setX(double x);
    void setY(double y);
    void reverseDirX();
    void reverseDirY();

    boolean getHasEnteredScreen();
    void setHasEnteredScreen(boolean hasEnteredScreen);
}
