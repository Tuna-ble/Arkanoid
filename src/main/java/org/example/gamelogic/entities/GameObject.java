package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;

import java.awt.geom.Rectangle2D;

public abstract class GameObject {
    protected double x, y, width, height;
    protected boolean isActive;
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isActive = true;
    }

    public abstract void update();
    public abstract void render(GraphicsContext gc);


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

    public boolean isAlive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean intersects(GameObject other) {
        if (other == null) {
            return false;
        }
        return this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y;
    }

    public GameObject getGameObject() {
        return this;
    }
}
