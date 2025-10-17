package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.entities.Ball;

public class FastBallStrategy implements PowerUpStrategy {
    private final double speedMultiplier = 2.0;

    @Override
    public void apply(GameManager gm) {
        Ball b = gm.getBall();
        b.setSpeed(b.getSpeed() * speedMultiplier);
    }

    @Override
    public void remove(GameManager gm) {
        Ball b = gm.getBall();
        b.setSpeed(b.getSpeed() / speedMultiplier);
    }
}

