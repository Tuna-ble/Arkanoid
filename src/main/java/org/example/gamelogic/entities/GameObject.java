package org.example.gamelogic.entities;

import java.awt.*;

public abstract class GameObject {
    protected double x, y, width, height;
    protected double dx, dy;
    protected boolean isActive;
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isActive = true;
        this.dx = 0;
        this.dy = 0;
    }

    public abstract void update();
    public abstract void render(Graphics2D g);


    public boolean isActive() {
        return isActive;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public double getDx() { return dx; }
    public void setDx(double dx) { this.dx = dx; }

    public double getDy() { return dy; }
    public void setDy(double dy) { this.dy = dy; }

    public boolean isAlive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

}
