package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UnbreakableBrick extends AbstractBrick {
    public UnbreakableBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void takeDamage(double damage) {

    }

    @Override
    public boolean isBreakable() {
        return false;
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            gc.setFill(Color.BLACK);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    @Override
    public Brick clone() {
        return new UnbreakableBrick(0, 0, this.width, this.height);
    }
}
