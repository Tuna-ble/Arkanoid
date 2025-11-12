package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.events.ExplosiveBrickEvent;
import org.example.gamelogic.core.ParticleManager;

public class ExplosiveBrick extends AbstractBrick {
    /// type: E
    private Color color = Color.RED;
    private Image brickImage;
    public ExplosiveBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.brickImage = AssetManager.getInstance().getImage("explosiveBrick");
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
            gc.drawImage(brickImage, this.x, this.y, this.width, this.height);
        }
    }

    @Override
    public Brick clone() {
        return new ExplosiveBrick(0, 0, this.width, this.height);
    }
}
