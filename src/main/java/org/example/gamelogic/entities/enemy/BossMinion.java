package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;

public class BossMinion extends AbstractEnemy {
    public BossMinion(double x, double y, double width, double height,
                      double dx, double dy) {
        super(x, y, width, height, dx, dy);
    }

    @Override
    public Enemy clone() {
        return new BossMinion(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    @Override
    public void update(double deltaTime) {
        this.y += this.dy * deltaTime;
        if (this.y > GameConstants.SCREEN_HEIGHT) {
            this.setActive(false);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
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
