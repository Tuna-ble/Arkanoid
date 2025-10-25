package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.BrickDestroyedEvent;

public class NormalBrick extends AbstractBrick {
    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void takeDamage() {
        if (isDestroyed()) {
            return;
        }
        this.isActive = false;
        EventManager.getInstance().publish(new BrickDestroyedEvent(this));
    }

    public int getScore() {
        return 0;
    }

    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isDestroyed()) {
            gc.setFill(Color.AQUAMARINE);
            gc.fillRect(x, y, width, height);
            //duong vien
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    @Override
    public Brick clone() {
        return new NormalBrick(0,0,this.width,this.height);
    }
}
