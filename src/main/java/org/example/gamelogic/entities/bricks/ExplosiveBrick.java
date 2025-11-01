package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.events.BrickExplodedEvent;

public class ExplosiveBrick extends AbstractBrick {
    public ExplosiveBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void takeDamage() {
        if (isDestroyed()) {
            return;
        }
        this.isActive = false;
        EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        EventManager.getInstance().publish(new BrickExplodedEvent(this));
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            gc.setFill(Color.RED);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    @Override
    public Brick clone() {
        return new ExplosiveBrick(0, 0, this.width, this.height);
    }
}
