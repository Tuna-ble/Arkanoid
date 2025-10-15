package org.example.gamelogic.entities.bricks;

import org.example.gamelogic.entities.GameObject;

public abstract class AbstractBrick extends GameObject implements Brick {
    public AbstractBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
    @Override
    public boolean isDestroyed() {
        return !this.isAlive();
    }

    @Override
    public abstract Brick clone();

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
}