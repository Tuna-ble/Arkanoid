package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;
import org.example.presentation.SpriteAnimation;

public class Enemy1 extends AbstractEnemy {
    private Image enemyImage;
    private SpriteAnimation idleAnimation;
    public Enemy1(double x, double y, double width, double height,
                            double dx, double dy) {
        super(x, y, width, height, dx, dy, new DownMovementStrategy());
        this.health = 1;
        this.scoreValue = 100;
        AssetManager am = AssetManager.getInstance();
        this.enemyImage = am.getImage("enemy1");
        if (enemyImage != null) {
            int frameCount = 5;
            int columns = 8;
            double duration = 2;
            boolean loops = true;
            this.idleAnimation = new SpriteAnimation(
                    enemyImage, frameCount, columns, duration, loops
            );
        }
    }

    @Override
    public Enemy clone() {
        return new Enemy1(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (idleAnimation != null) {
            idleAnimation.update(deltaTime);
        }
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
        idleAnimation.render(gc, x, y, width, height);
    }

    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        this.isActive = false;
    }
    @Override
    public String getType() {
        return "E1";
    }
}
