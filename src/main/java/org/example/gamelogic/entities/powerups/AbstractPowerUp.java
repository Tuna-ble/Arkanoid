package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.MovableObject;
import org.example.gamelogic.events.PowerUpCollectedEvent;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;
import org.example.presentation.RowAnimation;

public abstract class AbstractPowerUp extends MovableObject implements PowerUp {
    protected PowerUpStrategy strategy;
    private boolean isTaken = false;
    protected boolean outOfBounds = false;

    protected RowAnimation animation;

    public AbstractPowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy);
        this.strategy = strategy;
        this.isActive = true;

        Image powerUpSprites = AssetManager.getInstance().getImage("powerups");
        int spriteRow = getSpriteRow();

        if (powerUpSprites != null) {
            this.animation = new RowAnimation(
                    powerUpSprites,
                    spriteRow,
                    GameConstants.POWERUP_TOTAL_FRAMES,
                    GameConstants.POWERUP_FRAME_DURATION
            );
        }
        subscribeToPowerUpCollectedEvent();
    }

    private void subscribeToPowerUpCollectedEvent() {
        EventManager.getInstance().subscribe(
                PowerUpCollectedEvent.class,
                this::onPowerUpCollected
        );
    }

    protected void onPowerUpCollected(PowerUpCollectedEvent event) {
        if (event.getPowerUpCollected() == this) {
            markAsTaken();
        }
    }

    @Override
    public abstract PowerUp clone();

    @Override
    public void update(double deltaTime) {
        if (!outOfBounds) y += dy;
        if (y > GameConstants.SCREEN_HEIGHT) outOfBounds = true;

        if (animation != null) {
            animation.update(deltaTime);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (animation != null) {
            animation.render(gc, this.x, this.y, this.width, this.height);
        }
    }

    public abstract int getSpriteRow();

    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public boolean isOutOfBounds() {
        return outOfBounds;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public PowerUpStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PowerUpStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public GameObject getGameObject() {
        return this;
    }

    public void destroy() {
        this.isActive = false;
    }

    public void markAsTaken() {
        this.isTaken = true;
        destroy();
    }
}
