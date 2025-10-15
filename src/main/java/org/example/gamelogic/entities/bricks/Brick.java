package org.example.gamelogic.entities.bricks;

public interface Brick {
    void takeDamage();
    boolean isDestroyed();
    int getScore();
    void setPosition(double x, double y);
    double getX();
    double getY();
    Brick clone();

}
