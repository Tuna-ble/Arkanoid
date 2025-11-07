package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.gamelogic.strategy.powerup.FastBallStrategy;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class FastBallPowerUp extends AbstractPowerUp {
    /// type: S
    public FastBallPowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
    }

    @Override
    public PowerUp clone() {
        return new FastBallPowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
    }

    @Override
    public double getSpriteRow() {
        return 8;
    }
}
