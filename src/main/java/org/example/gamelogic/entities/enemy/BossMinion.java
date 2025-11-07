package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.graphics.ImageModifier;
import org.example.gamelogic.strategy.bossbehavior.BossEnrageStrategy;
import org.example.gamelogic.strategy.bossbehavior.BossPhase2Strategy;
import org.example.gamelogic.strategy.movement.DashMovementStrategy;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;
import org.example.gamelogic.strategy.movement.LRMovementStrategy;

public class BossMinion extends AbstractEnemy {
    private double shootTimer;
    private final double SHOOT_COOLDOWN = 2.5;

    private Color minionColor;
    private static final double MINION_SPRITE_WIDTH=200;
    private static final double MINION_SPRITE_HEIGHT=160;

    public BossMinion(double x, double y, double width, double height,
                      double dx, double dy) {
        super(x, y, width, height, dx, dy, new DashMovementStrategy());
        this.shootTimer = Math.random() * SHOOT_COOLDOWN;
        this.hasEnteredScreen = true;

        this.minionColor=GameConstants.ENRAGED_BOSS_COLOR;
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
                    -300,
                    BulletFrom.ENEMY
            );
        }
    }

    @Override
    public void render(GraphicsContext gc) {

        // Extract current phase frame from sprite sheet
        Image currentFrame = new WritableImage(
                enemySprites.getPixelReader(),
                480,
                0,
                (int) MINION_SPRITE_WIDTH,
                (int) MINION_SPRITE_HEIGHT
        );

        // Tint the current frame to desired color
        Image tintedFrame = ImageModifier.tintImage(currentFrame, minionColor);

        // Draw the tinted sprite at the desired position and size
        gc.drawImage(tintedFrame, this.x, this.y, this.width, this.height);
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        this.isActive = false;
        /*EventManager.getInstance().publish(new BrickDestroyedEvent(this));
        EventManager.getInstance().publish(new ExplosiveBrickEvent(this));*/
    }

    @Override
    public void handleEntry(double deltaTime) {

    }
}
