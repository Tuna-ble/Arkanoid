package org.example.business.entities;

import javafx.scene.canvas.GraphicsContext;

public abstract class MovableObject extends GameObject {
    protected double dx, dy;

    public MovableObject(double x, double y, double width, double height) {
        super(x, y, width, height); // gọi constructor lớp ông
        this.dx = 0;
        this.dy = 0;
    }
}
