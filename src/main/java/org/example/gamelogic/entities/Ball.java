package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;

public class Ball extends MovableObject {
    public Ball(double x, double y, double width, double height, double dx, double dy) {
        super(x, y, width, height, dx, dy);
    }
    public void update() {}
    public void render(GraphicsContext gc) {}
}
