package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.entities.Ball;

public class FastBallStrategy implements PowerUpStrategy {
    private final double speedMultiplier = 2.0;
    private double remainingTime=10.0;

    @Override
    public void apply(GameManager gm) {
//        Ball b = gm.getBall();
//        b.setSpeed(b.getSpeed() * speedMultiplier);
    }

    @Override
    public void update(GameManager gm, double deltatime) {
        remainingTime-=deltatime;
        if (remainingTime<=0) remove(gm);
    }

    @Override
    public void remove(GameManager gm) {
//        Ball b = gm.getBall();
//        b.setSpeed(b.getSpeed() / speedMultiplier);
        remainingTime=0;
    }

    @Override
    public boolean isExpired() {
        return remainingTime<=0;
    }
}

