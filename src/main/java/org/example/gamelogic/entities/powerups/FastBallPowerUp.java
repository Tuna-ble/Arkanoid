package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class FastBallPowerUp extends AbstractPowerUp {
    public FastBallPowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
    }

    @Override
    public PowerUp clone() {
        return new FastBallPowerUp(0.0, 0.0, this.width, this.height, 0.0, 3.0, super.getStrategy());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }
}
