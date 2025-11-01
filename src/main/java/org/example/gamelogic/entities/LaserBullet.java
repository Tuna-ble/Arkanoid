package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LaserBullet extends MovableObject {
    public LaserBullet(double x, double y) {
        super(x, y, 4, 20, 0, 600);
    }

    @Override
    public void update(double deltaTime) {
        y -= dy * deltaTime;
        if (y + height < 0) isActive = false;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }
}
