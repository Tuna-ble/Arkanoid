package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class ExpandPaddlePowerUp extends AbstractPowerUp {
    public ExpandPaddlePowerUp(double x, double y, double width, double height,
                               double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
    }

    @Override
    public PowerUp clone() {
        return new ExpandPaddlePowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, super.getStrategy());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }
}