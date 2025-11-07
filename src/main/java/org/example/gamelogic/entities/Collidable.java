package org.example.gamelogic.entities;

public interface Collidable {
    double getX();
    double getY();
    double getWidth();
    double getHeight();
    boolean isDestroyed();
    GameObject getGameObject();
}
