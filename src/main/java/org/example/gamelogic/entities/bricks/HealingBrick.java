package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ParticleManager;
import org.example.gamelogic.events.BrickDestroyedEvent;

public class HealingBrick extends AbstractBrick {
    /// type: R
    private enum State {
        IDLE,
        DAMAGED
    }

    private State currentState = State.IDLE;
    private double healingTimer = 0.0;
    private final double HEAL_TIME = 5.0;

    private Image brickImage;
    private Color particleColor = Color.GREEN;

    public HealingBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.isActive = true;
        this.brickImage = AssetManager.getInstance().getImage("healingBrick");
    }

    @Override
    public void takeDamage(double damage) {
        if (currentState == State.IDLE) {
            currentState = State.DAMAGED;
            healingTimer = HEAL_TIME;
        } else if (currentState == State.DAMAGED) {
            ParticleManager.getInstance().spawnBrickDebris(this.x, this.y, this.particleColor);
            this.isActive = false;
            EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        }
    }

    @Override
    public boolean isDestroyed() {
        return !this.isActive;
    }

    @Override
    public boolean isBreakable() {
        return true;
    }

    @Override
    public void update(double deltaTime) {
        if (currentState == State.DAMAGED) {
            healingTimer -= deltaTime;
            if (healingTimer <= 0) {
                currentState = State.IDLE;
                healingTimer = 0;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        gc.drawImage(brickImage, this.x, this.y, this.width, this.height);

        if (currentState == State.DAMAGED) {
            double pulseAlpha = (Math.sin(healingTimer * 10) + 1) / 2.0;
            try {
                gc.setGlobalAlpha(pulseAlpha * 0.7);
                gc.setFill(Color.WHITE);
                gc.fillRect(x, y, width, height);
            } finally {
                gc.restore();
            }
        }
    }

    @Override
    public Brick clone() {
        return new HealingBrick(0, 0, this.width, this.height);
    }
}
