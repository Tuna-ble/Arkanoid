package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.entities.MovableObject;
import org.example.gamelogic.events.*;

public abstract class AbstractEnemy extends MovableObject implements Enemy {
    private boolean isHit = false;
    protected boolean outOfBounds = false;
    protected double health;
    protected double scoreValue;
    private boolean hasEnteredScreen;

    public AbstractEnemy(double x, double y, double width,
                         double height, double dx, double dy) {
        super(x, y, width, height, dx, dy);
        this.isActive = true;
        this.hasEnteredScreen = false;

        subscribeToEvents();
    }

    private void subscribeToEvents() {
        EventManager.getInstance().subscribe(
                BallHitEnemyEvent.class,
                this::onHit
        );
        EventManager.getInstance().subscribe(
                EnemyDamagedEvent.class,
                this::onDamaged
        );
    }

    private void onDamaged(EnemyDamagedEvent event) {
        if (event.getDamagedEnemy()==this && !isDestroyed()) {
            GameObject damageSource=event.getDamageSource();
            if (damageSource instanceof LaserBullet) {
                takeDamage(GameConstants.LASER_BULLET_DAMAGE);
            }
        }
    }

    private void onHit(BallHitEnemyEvent event) {
        if (event.getEnemy() == this && !isDestroyed()) {
            takeDamage(GameConstants.BALL_DAMAGE);
        }
    }

    @Override
    public abstract Enemy clone();

    @Override
    public void update(double deltaTime) {
        this.x += this.dx * deltaTime;
        this.y += this.dy * deltaTime;

        if (this.x + this.width >= GameConstants.SCREEN_WIDTH) {
            this.x = GameConstants.SCREEN_WIDTH - this.width;
            this.dx = -this.dx;
        }

        if (!hasEnteredScreen && this.y > 0) {
            this.hasEnteredScreen = true;
        }

        if (this.y > GameConstants.SCREEN_HEIGHT) {
            this.setActive(false);
        }
    }

    @Override
    public abstract void render(GraphicsContext gc);

    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    public boolean isOutOfBounds() {
        return outOfBounds;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive=active;
    }

    @Override
    public GameObject getGameObject() {
        return this;
    }

    @Override
    public boolean isDestroyed() {
        return !isActive();
    }

    public void destroy() {
        this.isActive = false;
    }

    public void reverseDirX() {
        this.dx = -this.dx;
    }

    public void reverseDirY() {
        this.dy = -this.dy;
    }
    public boolean getHasEnteredScreen() {
        return this.hasEnteredScreen;
    }
}
