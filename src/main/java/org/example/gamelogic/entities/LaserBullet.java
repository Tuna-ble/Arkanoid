package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;

public class LaserBullet extends MovableObject {
    private final BulletFrom faction;
    public LaserBullet(double x, double y, double dy, BulletFrom faction) {
        super(x, y, 4, 20, 0, dy);
        this.faction = faction;
    }

    @Override
    public void update(double deltaTime) {
        y -= dy * deltaTime;
        if (y + height < 0 || y > GameConstants.SCREEN_HEIGHT) isActive = false;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (faction == BulletFrom.PLAYER) {
            gc.setFill(Color.CYAN); // Đạn ta
        } else {
            gc.setFill(Color.RED); // Đạn địch
        }
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }

    public BulletFrom getFaction() {
        return this.faction;
    }
}
