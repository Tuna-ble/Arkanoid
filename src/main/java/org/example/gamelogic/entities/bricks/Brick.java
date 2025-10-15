package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;

public interface Brick {
    void takeDamage();
    boolean isDestroyed();
    int getScore();
    void setPosition(double x, double y);
    double getX();
    double getY();
    Brick clone();

    void update();
    void render(GraphicsContext gc);
}
