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

public abstract class AbstractPowerUp extends MovableObject implements PowerUp {
    protected PowerUpStrategy strategy;
    private boolean isTaken = false;

    // Kiểm tra powerup đã rơi khỏi cửa sổ chưa
    protected boolean outOfBounds = false;

    protected final Image powerUpSprites;
    protected int currentFrame = 0;
    protected double frameTimer = 0.0;
    protected double spriteRow; // look up the sprite sheet and go to subclass

    public AbstractPowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy);
        this.strategy = strategy;
        this.isActive = true;

        powerUpSprites = AssetManager.getInstance().getImage("powerups");
        spriteRow = getSpriteRow();
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

        frameTimer += deltaTime;
        if (frameTimer >= GameConstants.POWERUP_FRAME_DURATION) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % GameConstants.POWERUP_TOTAL_FRAMES;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        double sourceX = GameConstants.POWERUP_SPRITE_OFFSET + currentFrame * (GameConstants.POWERUP_SPRITE_WIDTH + GameConstants.POWERUP_SPRITE_PADDING);
        double sourceY = GameConstants.POWERUP_SPRITE_OFFSET + spriteRow * (GameConstants.POWERUP_SPRITE_WIDTH + GameConstants.POWERUP_SPRITE_PADDING);
        double sourceWidth = GameConstants.POWERUP_SPRITE_WIDTH;
        double sourceHeight = GameConstants.POWERUP_SPRITE_HEIGHT;

        gc.drawImage(
                powerUpSprites,
                sourceX, sourceY, sourceWidth, sourceHeight,
                this.x, this.y, this.width, this.height
        );
    }

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

    public abstract double getSpriteRow();
}
