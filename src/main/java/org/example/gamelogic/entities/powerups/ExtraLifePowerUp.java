package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.gamelogic.strategy.powerup.ExtraLifeStrategy;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class ExtraLifePowerUp extends AbstractPowerUp {
    private final Image extra = new Image(
            getClass().getResourceAsStream("/GameIcon/extra.png")
    );

    /// type: H
    public ExtraLifePowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
    }

    @Override
    public PowerUp clone() {
        return new ExtraLifePowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(extra, x, y, 30, 30);
    }
}
