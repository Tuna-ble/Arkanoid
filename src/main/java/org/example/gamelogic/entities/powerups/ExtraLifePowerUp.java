package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.gamelogic.strategy.powerup.ExtraLifeStrategy;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class ExtraLifePowerUp extends AbstractPowerUp {
    /// type: L
    public ExtraLifePowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
    }

    @Override
    public PowerUp clone() {
        return new ExtraLifePowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
    }

    @Override
    public double getSpriteRow() {
        return 5;
    }
}
