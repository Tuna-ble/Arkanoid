package org.example.gamelogic.entities.bricks;

import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.events.BrickDamagedEvent;
import org.example.gamelogic.events.BallHitBrickEvent;

public abstract class AbstractBrick extends GameObject implements Brick {
    public AbstractBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        subscribeToBrickEvents();
    }

    private void subscribeToBrickEvents() {
        EventManager.getInstance().subscribe(
                BallHitBrickEvent.class,
                this::onHit
        );
        EventManager.getInstance().subscribe(
                BrickDamagedEvent.class,
                this::onDamaged
        );
    }

    private void onHit(BallHitBrickEvent event) {
        if (event.getBrick() == this && !isDestroyed()) {
            takeDamage(GameConstants.BALL_DAMAGE);
        }
    }

    private void onDamaged(BrickDamagedEvent event) {
        if (event.getDamagedBrick()==this && !isDestroyed()) {
            GameObject damageSource=event.getDamageSource();
            if (damageSource instanceof ExplosiveBrick) {
                takeDamage(GameConstants.EXPLOSIVE_BRICK_DAMAGE);
            }
            else if (damageSource instanceof LaserBullet) {
                takeDamage(GameConstants.LASER_BULLET_DAMAGE);
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return !this.isAlive();
    }

    @Override
    public boolean isBreakable() {
        return true;
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
