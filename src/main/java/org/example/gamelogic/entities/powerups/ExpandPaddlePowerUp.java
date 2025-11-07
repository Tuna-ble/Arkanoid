package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class ExpandPaddlePowerUp extends AbstractPowerUp {
    /// type: E
    public ExpandPaddlePowerUp(double x, double y, double width, double height,
                               double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
    }

    @Override
    public PowerUp clone() {
        return new ExpandPaddlePowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
    }

    @Override
    public double getSpriteRow() {
        return 3;
    }
}
