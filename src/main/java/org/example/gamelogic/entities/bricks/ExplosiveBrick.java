package org.example.gamelogic.entities.bricks;

import java.awt.*;

public class ExplosiveBrick extends AbstractBrick {
    public void render(Graphics2D g) {
        g.setColor(Color.RED);
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
    public void update() {

    }

    public ExplosiveBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Brick clone() {
        return new ExplosiveBrick(0,0,this.width,this.height);
    }

}
