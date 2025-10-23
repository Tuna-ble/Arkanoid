package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public interface PowerUp {
    PowerUpStrategy getStrategy();
    void setPosition(double x, double y);
    double getX();
    double getY();
    PowerUp clone();

    void update();
    void render(GraphicsContext gc);

    void setActive(boolean active);
    boolean isActive();
    boolean isOutOfBounds();
    void destroy();

    public GameObject getGameObject();

    void markAsTaken();
}