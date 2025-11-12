package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.Collidable;
import org.example.gamelogic.entities.GameObject;

public interface Enemy extends Collidable {
    void takeDamage(double damage);
    void setPosition(double x, double y);
    double getDx();
    double getDy();
    double getCenterX();
    double getCenterY();
    Enemy clone();

    void update(double deltaTime);
    void render(GraphicsContext gc);

    void setActive(boolean active);
    boolean isActive();
    boolean isOutOfBounds();
    void destroy();

    void setDx(double dx);
    void setDy(double dy);
    void setX(double x);
    void setY(double y);
    void reverseDirX();
    void reverseDirY();

    boolean getHasEnteredScreen();
    void setHasEnteredScreen(boolean hasEnteredScreen);

    String getType();

    int getHealth();

    void setHealth(int health);
}
