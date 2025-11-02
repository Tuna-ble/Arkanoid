package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.core.ParticleManager;


public class HardBrick extends AbstractBrick {
    private double durability; //do ben

    public HardBrick(double x, double y,double width, double height) {
        super(x, y, width, height);
        this.durability = GameConstants.HARD_BRICK_DURABILITY;
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }

        this.durability -= damage;
        if (this.durability <= 0) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, Color.LIGHTGRAY);
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
        if (this.durability == 3) {
            gc.setFill(Color.DARKGRAY);
        } else if (this.durability == 2) {
            gc.setFill(Color.GRAY);
        } else {
            gc.setFill(Color.LIGHTGRAY);
        }

        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }

    public void takeDamage() {
        if (isDestroyed()) {
            return;
        }

        this.durability--;
        if (this.durability <= 0) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, Color.LIGHTGRAY);
            this.isActive = false;
            EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        }
    }

    public int getScore() {
        return 0;
    }

    @Override
    public Brick clone() {
        return new HardBrick(0,0,this.width,this.height);
    }
}
