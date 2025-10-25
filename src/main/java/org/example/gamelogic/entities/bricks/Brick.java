package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.GameObject;

public interface Brick {
    void takeDamage();
    boolean isDestroyed();
    int getScore();
    void setPosition(double x, double y);
    double getX();
    double getY();
    Brick clone();

    void update(double deltaTime);
    void render(GraphicsContext gc);

    GameObject getGameObject();
}
