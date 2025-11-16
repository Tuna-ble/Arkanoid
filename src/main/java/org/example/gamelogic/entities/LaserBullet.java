package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.presentation.SpriteAnimation;

public class LaserBullet extends MovableObject {
    private final BulletFrom faction;
    private final BulletType type;
    private final Image normalBulletImage;
    private final Image bossBulletSheet;
    private final SpriteAnimation bossBullet;

    public LaserBullet(double x, double y, double dx, double dy, BulletType type, BulletFrom faction) {
        super(x, y, type.width, type.height, dx, dy);
        this.faction = faction;
        this.type = type;

        AssetManager am = AssetManager.getInstance();
        normalBulletImage = am.getImage("bullet");
        bossBulletSheet = am.getImage("bossBullet");
        bossBullet = new SpriteAnimation(bossBulletSheet, 4, 4, 0.5, true);
    }

    @Override
    public void update(double deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;

        bossBullet.update(deltaTime);

        if (y + height < 0 || y > GameConstants.SCREEN_HEIGHT ||
                x + width < 0 || x > GameConstants.SCREEN_WIDTH) {
            isActive = false;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (faction == BulletFrom.PLAYER) {
            gc.drawImage(normalBulletImage, x, y, width, height);
        } else {
            if (type == BulletType.BOSS_HOMING_SQUARE) {
                bossBullet.render(gc, x, y, width, height);
            } else {
                gc.setFill(Color.RED);
                gc.fillRect(x, y, width, height);
            }
        }

    }

    public BulletFrom getFaction() {
        return this.faction;
    }
}
