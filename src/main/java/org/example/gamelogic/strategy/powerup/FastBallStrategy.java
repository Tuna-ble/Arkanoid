package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.entities.IBall;

import java.util.Iterator;
import java.util.List;

public class FastBallStrategy implements PowerUpStrategy {
    private final double speedMultiplier = 2.0;
    private double remainingTime = 10.0;

    @Override
    public void apply(GameManager gm) {
        List<IBall> balls = gm.getBallManager().getActiveBalls();
        Iterator<IBall> iterator = balls.iterator();
        while (iterator.hasNext()) {
            IBall b = iterator.next();
            b.setSpeed(b.getSpeed() * speedMultiplier);
        }
    }

    @Override
    public void update(GameManager gm, double deltatime) {
        remainingTime -= deltatime;
    }

    @Override
    public void remove(GameManager gm) {
        List<IBall> balls = gm.getBallManager().getActiveBalls();
        Iterator<IBall> iterator = balls.iterator();
        while (iterator.hasNext()) {
            IBall b = iterator.next();
            b.setSpeed(b.getSpeed() / speedMultiplier);
        }
        remainingTime = 0;
    }

    @Override
    public boolean isExpired() {
        return remainingTime <= 0;
    }
}
