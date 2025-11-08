package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.BallManager;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.states.PlayingState;

public class FastBallStrategy implements PowerUpStrategy {
    private final double speedMultiplier = 2.0;
    private double remainingTime = 10.0;

    @Override
    public void apply(PlayingState playingState) {
        BallManager ballManager = playingState.getBallManager();
        if (ballManager != null) {
            for (IBall ball : ballManager.getActiveBalls()) {
                ball.multiplySpeed(speedMultiplier);
            }
        }
    }

    @Override
    public void update(PlayingState playingState, double deltatime) {
        remainingTime -= deltatime;
        if (remainingTime <= 0) remove(playingState);
    }

    @Override
    public void remove(PlayingState playingState) {
        BallManager ballManager = playingState.getBallManager();
        if (ballManager != null) {
            for (IBall ball : ballManager.getActiveBalls()) {
                ball.multiplySpeed( 1 / speedMultiplier);
            }
        }
        remainingTime = 0;
    }

    @Override
    public boolean isExpired() {
        return remainingTime <= 0;
    }

    @Override
    public void reset() {
        remainingTime = 10.0;
    }

    @Override
    public PowerUpStrategy clone() {
        return new FastBallStrategy();
    }
}
