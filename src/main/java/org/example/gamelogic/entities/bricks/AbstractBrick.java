package org.example.gamelogic.entities.bricks;

import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.events.BrickHitEvent;

public abstract class AbstractBrick extends GameObject implements Brick {
    public AbstractBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        subscribeToHitEvent();
    }

    private void subscribeToHitEvent() {
        EventManager.getInstance().subscribe(
                BrickHitEvent.class,
                this::onHit
        );
    }

    private void onHit(BrickHitEvent event) {
        if (event.getBrick() == this && !isDestroyed()) {
            takeDamage();
        }
    }

    @Override
    public boolean isDestroyed() {
        return !this.isAlive();
    }

    @Override
    public abstract Brick clone();

    @Override
    public boolean withinRangeOf(Brick other) {
        if (other==null) {
            return false;
        }
        return this.x - GameConstants.BRICK_WIDTH - GameConstants.PADDING - 1 < other.getX() &&
                this.x + GameConstants.BRICK_WIDTH + GameConstants.PADDING + 1 > other.getX() &&
                this.y - GameConstants.BRICK_HEIGHT - GameConstants.PADDING - 1 < other.getY() &&
                this.y + GameConstants.BRICK_HEIGHT + GameConstants.PADDING + 1 > other.getY();
    }

    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public GameObject getGameObject() {
        return this;
    }
}
