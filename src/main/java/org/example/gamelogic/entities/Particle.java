package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle extends MovableObject {

    private double lifeSpan;
    private final double maxLifeSpan;
    private final Color color;
    private final double gravity = 980.0;
    public Particle(double x, double y, double dx, double dy,
                    double width, double height, double lifeSpan, Color color) {

        super(x, y, width, height, dx, dy);

        this.maxLifeSpan = lifeSpan;
        this.lifeSpan = lifeSpan;
        this.color = color;
        this.isActive = true;
    }

    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        lifeSpan -= deltaTime;
        if (lifeSpan <= 0) {
            this.isActive = false;
            return;
        }

        this.x += this.dx * deltaTime;
        this.y += this.dy * deltaTime;

        this.dy += gravity * deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        double alpha = Math.max(0, lifeSpan / maxLifeSpan);

        gc.save();
        try {
            gc.setGlobalAlpha(alpha);
            gc.setFill(this.color);
            gc.fillRect(this.x, this.y, this.width, this.height);
        } finally {
            gc.restore();
        }
    }

    public boolean isDestroyed() {
        return !this.isActive;
    }
}