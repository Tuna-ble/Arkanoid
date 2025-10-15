package org.example.gamelogic.entities.powerups;

import org.example.gamelogic.entities.MovableObject;

import javafx.scene.canvas.GraphicsContext;

public class FastBallPowerUp extends MovableObject implements PowerUp {
    public FastBallPowerUp(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height, dx, dy);
    }
    public void update() {}
    public void render(GraphicsContext gc) {}
}
