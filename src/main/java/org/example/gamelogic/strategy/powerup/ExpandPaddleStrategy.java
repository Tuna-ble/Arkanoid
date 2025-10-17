package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.entities.Paddle;

public class ExpandPaddleStrategy implements PowerUpStrategy {
    private final double expansionFactor = 1.5;

    @Override
    public void apply(GameManager gm) {
        Paddle p = gm.getPaddle();
        p.setX(p.getX() - p.getWidth() * (expansionFactor - 1) / 2);
        p.setWidth(p.getWidth() * expansionFactor);
    }

    @Override
    public void remove(GameManager gm) {
        Paddle p = gm.getPaddle();
        p.setX(p.getX() + p.getWidth() * (1 - 1 / expansionFactor) / 2);
        p.setWidth(p.getWidth() / expansionFactor);
    }
}

