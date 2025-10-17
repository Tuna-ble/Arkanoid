package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class NormalBrick extends AbstractBrick {
    public void takeDamage() {

    }

    public int getScore() {
        return 0;
    }

    public void update() {

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

    public NormalBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public Brick clone() {
        return new NormalBrick(0,0,this.width,this.height);
    }
}
