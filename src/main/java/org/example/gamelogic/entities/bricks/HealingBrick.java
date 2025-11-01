package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HealingBrick extends AbstractBrick {

    private enum State {
        VISIBLE,
        DAMAGED
    }

    private State currentState = State.VISIBLE;
    private double healingTimer = 0.0;
    private final double HEAL_TIME = 5.0;

    private Color visibleColor = Color.LIGHTGREEN;
    private Color damagedColor = Color.DARKGREEN;

    public HealingBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.isActive = true;
    }

    @Override
    public void takeDamage(double damage) {
        if (currentState == State.VISIBLE) {

            currentState = State.DAMAGED;
            healingTimer = HEAL_TIME;


        } else if (currentState == State.DAMAGED) {
            this.isActive = false;

        }
    }

    @Override
    public boolean isDestroyed() {
        return !this.isActive;
    }

    @Override
    public boolean isBreakable() {

        return true;
    }

    @Override
    public void update(double deltaTime) {

        if (currentState == State.DAMAGED) {
            healingTimer -= deltaTime;

            if (healingTimer <= 0) {

                currentState = State.VISIBLE;
                healingTimer = 0;

            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        if (currentState == State.VISIBLE) {
            gc.setFill(visibleColor);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.DARKGREEN);
            gc.strokeRect(x, y, width, height);
        } else {

            gc.setFill(damagedColor);
            gc.fillRect(x, y, width, height);
            gc.setStroke(Color.LIGHTGREEN);
            gc.strokeRect(x, y, width, height);

            double remainingRatio = healingTimer / HEAL_TIME;
            gc.setFill(Color.WHITE);
            gc.fillRect(x + 2, y + height - 7, (width - 4) * remainingRatio, 5);
        }
    }

    @Override
    public Brick clone() {
        return new HealingBrick(0, 0, this.width, this.height);
    }
}