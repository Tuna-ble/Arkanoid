package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.core.ParticleManager;


public class HardBrick extends AbstractBrick {
    /// type: H
    private double durability;
    private Image brickImage;

    public HardBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.durability = GameConstants.HARD_BRICK_DURABILITY;
        this.brickImage = AssetManager.getInstance().getImage("hardBrick1");
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }

        this.durability -= damage;
        if (this.durability <= 0) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, Color.DARKGREY);
            this.isActive = false;
            EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        }
    }

    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        if (isDestroyed()) {
            return;
        }
        AssetManager am = AssetManager.getInstance();
        if (this.durability > 2) {

        } else if (this.durability <= 2 && this.durability > 1) {
            this.brickImage = am.getImage("hardBrick2");
        } else {
            this.brickImage = am.getImage("hardBrick3");
        }
        gc.drawImage(brickImage, this.x, this.y, this.width, this.height);
    }

    @Override
    public Brick clone() {
        return new HardBrick(0, 0, this.width, this.height);
    }
}
