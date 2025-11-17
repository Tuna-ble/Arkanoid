package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.data.AssetManager;
import org.example.gamelogic.strategy.powerup.ExtraLifeStrategy;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class ExtraLifePowerUp extends AbstractPowerUp {
    private Image extra;

    /// type: L
    public ExtraLifePowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
        extra = AssetManager.getInstance().getImage("icon_extra_life");
    }

    @Override
    public PowerUp clone() {
        ExtraLifePowerUp newClone = new ExtraLifePowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
        newClone.animation = this.animation;
        return newClone;
    }

    @Override
    public int getSpriteRow() {
        return 5;
    }
}
