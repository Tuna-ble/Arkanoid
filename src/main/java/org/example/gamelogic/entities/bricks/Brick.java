package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.GameObject;

public interface Brick {
    void takeDamage(double damage);
    boolean isDestroyed();
    boolean isBreakable();
    void setPosition(double x, double y);
    double getX();
    double getY();
    double getWidth();
    double getHeight();
    Brick clone();

    boolean withinRangeOf(Brick other);

    void update(double deltaTime);
    void render(GraphicsContext gc);

    GameObject getGameObject();
}
