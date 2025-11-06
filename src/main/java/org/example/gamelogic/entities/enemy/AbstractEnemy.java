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
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.events.*;
import org.example.gamelogic.strategy.movement.EnemyMovementStrategy;

public abstract class AbstractEnemy extends MovableObject implements Enemy {
    private boolean isHit = false;
    protected boolean outOfBounds = false;
    protected double health;
    protected double scoreValue;
    protected boolean hasEnteredScreen;
    protected Image enemySprites;

    protected EnemyMovementStrategy movementStrategy;

    public AbstractEnemy(double x, double y, double width, double height,
                         double dx, double dy, EnemyMovementStrategy initialMovementStrategy) {
        super(x, y, width, height, dx, dy);
        this.isActive = true;
        this.hasEnteredScreen = false;
        this.movementStrategy = initialMovementStrategy;
        this.enemySprites = AssetManager.getInstance().getImage("enemies");

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
        if (!hasEnteredScreen) {
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

    /**
     * Creates a tinted copy of the given grayscale image.
     */
    public Image tintImage(Image src, Color tint) {
        int w = (int) src.getWidth();
        int h = (int) src.getHeight();
        WritableImage tinted = new WritableImage(w, h);
        PixelReader pr = src.getPixelReader();
        PixelWriter pw = tinted.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = pr.getColor(x, y);
                if (c.getOpacity() > 0.0) {
                    // multiply tint hue with brightness of grayscale pixel
                    double brightness = c.getBrightness();
                    Color newColor = tint.deriveColor(0, 1, brightness, c.getOpacity());
                    pw.setColor(x, y, newColor);
                } else {
                    pw.setColor(x, y, Color.TRANSPARENT);
                }
            }
        }
        return tinted;
    }
}
