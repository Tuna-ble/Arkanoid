package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConfig;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.entities.MovableObject;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

import java.awt.geom.Rectangle2D;

public abstract class AbstractPowerUp extends MovableObject implements PowerUp {
    protected PowerUpStrategy strategy;

    // Kiểm tra powerup đã rơi khỏi cửa sổ chưa
    protected boolean outOfBounds = false;

    public AbstractPowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy);
        this.strategy = strategy;
        this.isActive = false;
    }

    @Override
    public abstract PowerUp clone();

    @Override
    public void update() {
        if (!outOfBounds) y += dy;
        if (y > GameConfig.SCREEN_HEIGHT) outOfBounds = true;
    }

    @Override
    public abstract void render(GraphicsContext gc);

    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public boolean isOutOfBounds() {
        return outOfBounds;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        this.isActive=active;
    }

    @Override
    public PowerUpStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PowerUpStrategy strategy) {
        this.strategy = strategy;
    }
}
