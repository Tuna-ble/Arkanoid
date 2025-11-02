package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;

public class Enemy1 extends AbstractEnemy {
    public Enemy1(double x, double y, double width, double height,
                            double dx, double dy) {
        super(x, y, width, height, dx, dy);
        this.health = 1;
        this.scoreValue = 100;
    }

    @Override
    public Enemy clone() {
        return new Enemy1(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BROWN);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        this.isActive = false;
        /*EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        EventManager.getInstance().publish(new ExplosiveBrickEvent(this));*/
    }
}
