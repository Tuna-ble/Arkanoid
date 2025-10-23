package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ExplosiveBrick extends AbstractBrick {
    @Override
    public void render(GraphicsContext g) {
        g.setFill(Color.RED);
    }
    public void takeDamage() {

    }

    public int getScore() {
        return 0;
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    @Override
    public void update(double deltaTime) {

    }

    public ExplosiveBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Brick clone() {
        return new ExplosiveBrick(0,0,this.width,this.height);
    }


}
