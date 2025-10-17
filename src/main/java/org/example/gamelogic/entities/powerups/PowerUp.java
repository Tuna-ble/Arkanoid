package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.entities.Paddle;

public interface PowerUp {
    void applyPowerUp(GameManager gm);
    void removePowerUp(GameManager gm);
    boolean isTaken(Paddle p);

    void setPosition(double x, double y);
    double getX();
    double getY();
    PowerUp clone();

    void update();
    void render(GraphicsContext gc);

    boolean isActive();
    boolean isOutOfBounds();
    double getRemainingTime();
}
