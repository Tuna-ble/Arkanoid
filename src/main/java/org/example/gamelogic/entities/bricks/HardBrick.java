package org.example.gamelogic.entities.bricks;

import java.awt.*;

public class HardBrick extends AbstractBrick {
    public void update() {

    }

    public void render(Graphics2D g) {

    }

    public void takeDamage() {

    }

    public int getScore() {
        return 0;
    }

    public HardBrick(double x, double y,double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Brick clone() {
        return new HardBrick(0,0,this.width,this.height);
    }

}
