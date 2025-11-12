package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.data.AssetManager;
import org.example.presentation.SpriteAnimation;

public class UnbreakableBrick extends AbstractBrick {
    /// type: U

    private final Image brickImage;
    private SpriteAnimation hitAnimation;

    private boolean isAnimating = false;

    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        AssetManager am = AssetManager.getInstance();
        this.brickImage = am.getImage("unbreakableBrick");
        Image animationSheet = am.getImage("unbreakableBrickHit");
        if (animationSheet != null) {
            int frameCount = 5;
            int columns = 5;
            double duration = 0.5;
            boolean loops = false;
            this.hitAnimation = new SpriteAnimation(
                    animationSheet, frameCount, columns, duration, loops
            );
        }
    }

    @Override
    public void takeDamage(double damage) {
        if (!isAnimating && hitAnimation != null) {
            isAnimating = true;
            hitAnimation.reset();
        }
    }

    @Override
    public boolean isBreakable() {
        return false;
    }

    @Override
    public void update(double deltaTime) {
        if (isAnimating) {
            hitAnimation.update(deltaTime);
            if (hitAnimation.isFinished()) {
                isAnimating = false;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            if (isAnimating && hitAnimation != null) {
                hitAnimation.render(gc, this.x, this.y, this.width, this.height);
            } else {
                gc.drawImage(brickImage, x, y, width, height);
            }
        }
    }

    @Override
    public Brick clone() {
        return new UnbreakableBrick(0, 0, this.width, this.height);
    }
}
