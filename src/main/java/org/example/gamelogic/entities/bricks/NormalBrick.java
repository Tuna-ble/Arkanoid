package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ParticleManager;
import org.example.gamelogic.events.BrickDestroyedEvent;

public class NormalBrick extends AbstractBrick {
    /// type: N
    private double durability;
    private Color color = Color.CYAN;
    private final Image brickImage;

    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.durability = GameConstants.BRICK_DURABILITY;
        this.brickImage = AssetManager.getInstance().getImage("normalBrick");
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }

        this.durability -= damage;
        if (this.durability <= 0) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, this.color);
            this.isActive = false;
            EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        }
    }

    public int getScore() {
        return 0;
    }

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
        return new NormalBrick(0, 0, this.width, this.height);
    }
}
