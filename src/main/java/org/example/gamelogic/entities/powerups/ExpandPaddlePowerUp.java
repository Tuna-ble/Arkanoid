package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.data.AssetManager;
import org.example.gamelogic.strategy.powerup.ExpandPaddleStrategy;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class ExpandPaddlePowerUp extends AbstractPowerUp {
    private Image expand;

    /// type: E
    public ExpandPaddlePowerUp(double x, double y, double width, double height,
                               double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
        expand = AssetManager.getInstance().getImage("icon_expand");
    }

    @Override
    public PowerUp clone() {
        return new ExpandPaddlePowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(expand, x, y, 30, 30);
    }
}
