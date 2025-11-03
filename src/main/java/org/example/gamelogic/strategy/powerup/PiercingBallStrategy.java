package org.example.gamelogic.strategy.powerup;

import java.util.List;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.states.PlayingState;

public class PiercingBallStrategy implements PowerUpStrategy {
    private final int pierceLimit = 3;
    private PlayingState playingState; // stored reference

    @Override
    public void apply(PlayingState playingState) {
        this.playingState = playingState;
        List<IBall> activeBalls = playingState.getBallManager().getActiveBalls();
        for (IBall ball : activeBalls) {
            ball.setPierceLeft(pierceLimit);
        }
    }

    @Override
    public void update(PlayingState playingState, double deltaTime) {

    }

    @Override
    public void remove(PlayingState playingState) {

    }

    @Override
    public boolean isExpired() {
        if (playingState == null) return true;

        List<IBall> activeBalls = playingState.getBallManager().getActiveBalls();
        for (IBall ball : activeBalls) {
            if (ball.getPierceLeft() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void reset() {
        if (playingState == null) return;

        List<IBall> activeBalls = playingState.getBallManager().getActiveBalls();
        for (IBall ball : activeBalls) {
            ball.setPierceLeft(pierceLimit);
        }
    }

    @Override
    public PowerUpStrategy clone() {
        return new PiercingBallStrategy();
    }
}
