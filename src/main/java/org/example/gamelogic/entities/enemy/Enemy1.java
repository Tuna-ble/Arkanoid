package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.gamelogic.graphics.ImageModifier;
import org.example.gamelogic.strategy.movement.DownMovementStrategy;

public class Enemy1 extends AbstractEnemy {
    private Color enemy1Color;
    private static final double ENEMY_SPRITE_WIDTH=180;
    private static final double ENEMY_SPRITE_HEIGHT=140;

    public Enemy1(double x, double y, double width, double height,
                            double dx, double dy) {
        super(x, y, width, height, dx, dy, new DownMovementStrategy());
        this.health = 1;
        this.scoreValue = 100;

        this.enemy1Color=Color.BROWN;
    }

    @Override
    public Enemy clone() {
        return new Enemy1(0.0, 0.0, this.width, this.height, this.dx, this.dy);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void handleEntry(double deltaTime) {
        this.y += this.dy * deltaTime;

        if (this.y > 0) {
            this.hasEnteredScreen = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {

        // Extract current phase frame from sprite sheet
        Image currentFrame = new WritableImage(
                enemySprites.getPixelReader(),
                0,
                180,
                (int) ENEMY_SPRITE_WIDTH,
                (int) ENEMY_SPRITE_HEIGHT
        );

        // Tint the current frame to desired color
        Image tintedFrame = ImageModifier.tintImage(currentFrame, enemy1Color);

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
}
