package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.gamelogic.strategy.powerup.MultiBallStrategy;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class MultiBallPowerUp extends AbstractPowerUp {
    /// type: M
    public MultiBallPowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
    }

    @Override
    public PowerUp clone() {
        MultiBallPowerUp newClone = new MultiBallPowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
        newClone.animation = this.animation;
        return newClone;
    }

    @Override
    public int getSpriteRow() {
        return 6;
    }
}
