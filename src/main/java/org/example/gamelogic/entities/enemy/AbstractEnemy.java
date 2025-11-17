package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.entities.MovableObject;
import org.example.gamelogic.events.*;
import org.example.gamelogic.strategy.movement.EnemyMovementStrategy;
import org.example.presentation.SpriteAnimation;

public abstract class AbstractEnemy extends MovableObject implements Enemy {
    private boolean isHit = false;
    protected boolean outOfBounds = false;
    protected double health;
    protected double scoreValue;
    protected boolean hasEnteredScreen;

    protected SpriteAnimation explosionAnim;
    protected enum LifeState {
        ALIVE,
        DYING
    }
    protected LifeState lifeState = LifeState.ALIVE;

    protected EnemyMovementStrategy movementStrategy;

    public AbstractEnemy(double x, double y, double width, double height,
                         double dx, double dy, EnemyMovementStrategy initialMovementStrategy) {
        super(x, y, width, height, dx, dy);
        this.isActive = true;
        this.hasEnteredScreen = false;
        this.movementStrategy = initialMovementStrategy;

        Image sheet = AssetManager.getInstance().getImage("enemyExplode");
        if (sheet != null) {
            this.explosionAnim = new SpriteAnimation(sheet, 7, 7, 0.5, false);
        }
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
        if (lifeState == LifeState.DYING) {
            if (explosionAnim != null) {
                explosionAnim.update(deltaTime);
                if (explosionAnim.isFinished()) {
                    this.isActive = false;
                }
            } else {
                this.isActive = false;
            }
            return;
        }

        if (!this.hasEnteredScreen) {
            handleEntry(deltaTime);
        } else {
            if (this.movementStrategy != null) {
                this.movementStrategy.move(this, deltaTime);
            }
        }
        if (this.y > GameConstants.SCREEN_HEIGHT) {
            this.setActive(false);
        }
    }

    @Override
    public abstract void render(GraphicsContext gc);

    public abstract void handleEntry(double deltaTime);

    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setHasEnteredScreen(boolean hasEnteredScreen) {
        this.hasEnteredScreen = hasEnteredScreen;
    }

    public void setMovementStrategy(EnemyMovementStrategy newStrategy) {
        this.movementStrategy = newStrategy;
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

    public double getCenterX() {
        return this.x + (this.width / 2);
    }

    public double getCenterY() {
        return this.y + (this.height / 2);
    }

    @Override
    public int getHealth() {
        return (int) this.health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }
}

