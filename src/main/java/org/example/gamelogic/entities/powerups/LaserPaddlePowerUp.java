package org.example.gamelogic.entities.powerups;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.data.AssetManager;
import org.example.gamelogic.strategy.powerup.LaserPaddleStrategy;
import org.example.gamelogic.strategy.powerup.PowerUpStrategy;

public class LaserPaddlePowerUp extends AbstractPowerUp {
    /// type: L
    private Image laser;
    public LaserPaddlePowerUp(double x, double y, double width, double height,
                           double dx, double dy, PowerUpStrategy strategy) {
        super(x, y, width, height, dx, dy, strategy);
        laser = AssetManager.getInstance().getImage("laser");
    }

    @Override
    public PowerUp clone() {
        return new LaserPaddlePowerUp(0.0, 0.0, this.width, this.height, this.dx, this.dy, getStrategy().clone());
    }


    /*
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }

     */


    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(laser, x, y, 30, 30);
    }

}
