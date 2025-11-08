package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ParticleManager;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.events.ExplosiveBrickEvent;

public class ExplosiveBrick extends AbstractBrick {
    /// type: E
    private Color color = Color.RED;
    public ExplosiveBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, this.color);
        this.isActive = false;
        EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        EventManager.getInstance().publish(new ExplosiveBrickEvent(this));
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            gc.setFill(this.color);
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
