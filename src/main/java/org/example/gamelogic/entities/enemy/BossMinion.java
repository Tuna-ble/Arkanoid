package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.BulletType;
import org.example.gamelogic.events.EnemyDestroyedEvent;
import org.example.gamelogic.strategy.movement.DashMovementStrategy;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;
import org.example.gamelogic.strategy.movement.LRMovementStrategy;

public class BossMinion extends AbstractEnemy {
    private boolean isShooting;
    private double shootTimer;
    private final double SHOOT_COOLDOWN = 2.5;

    private Image idleImage;
    private Image shootImage;

    public BossMinion(double x, double y, double width, double height,
                      double dx, double dy) {
        super(x, y, width, height, dx, dy, new DashMovementStrategy());
        this.shootTimer = Math.random() * SHOOT_COOLDOWN;
        this.hasEnteredScreen = true;
        AssetManager am = AssetManager.getInstance();
        this.idleImage = am.getImage("minion");
        this.shootImage = am.getImage("minionShoot");
    }

    @Override
    public Enemy clone() {
        return new BossMinion(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        shootTimer += deltaTime;

        if (shootTimer >= SHOOT_COOLDOWN) {
            shootTimer = 0.0;

            double bulletX = this.x + (this.width / 2.0) - 2;
            double bulletY = this.y + this.height;

            LaserManager.getInstance().createBullet(
                    bulletX,
                    bulletY,
                    0,
                    300,
                    BulletType.ENEMY_LASER,
                    BulletFrom.ENEMY
            );
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (lifeState == LifeState.DYING) {
            if (explosionAnim != null) {
                explosionAnim.render(gc, x, y, width, height);
            }
            return;
        }
        gc.drawImage(idleImage, this.x, this.y, this.width, this.height);
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        this.lifeState = LifeState.DYING;
        EventManager.getInstance().publish(new EnemyDestroyedEvent(this));
        if (explosionAnim != null) {
            explosionAnim.reset();
        }
        this.setDx(0);
        this.setDy(0);
    }

    @Override
    public void handleEntry(double deltaTime) {

    }

    @Override
    public String getType() {
        return "MINION";
    }
}
