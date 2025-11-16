package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.ImageModifier;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;

public class Enemy2 extends AbstractEnemy {
    private Image enemyImage;
    private static final double ENEMY_SPRITE_WIDTH = 180;
    private static final double ENEMY_SPRITE_HEIGHT = 140;

    public Enemy2(double x, double y, double width, double height,
                  double dx, double dy) {
        super(x, y, width, height, dx, dy, new DownMovementStrategy());

        AssetManager am = AssetManager.getInstance();
        this.enemyImage = am.getImage("enemy2");
    }

    @Override
    public Enemy clone() {
        return new Enemy2(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void handleEntry(double deltaTime) {
        this.y += this.dy * deltaTime;

        if (this.y > GameConstants.PLAY_AREA_Y) {
            this.hasEnteredScreen = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(enemyImage, x, y, width, height);
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        this.isActive = false;
    }
    @Override
    public String getType() {
        return "E2";
    }
}
