package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.states.PlayingState;

public class LaserPaddleStrategy implements PowerUpStrategy {
    private double remainingTime = 5.0;
    private double timeSinceLastShot = 1;
    private final double interval = 0.5;

    @Override
    public void apply(PlayingState playingState) {

    }

    @Override
    public void update(PlayingState playingState, double deltatime) {
        remainingTime -= deltatime;
        timeSinceLastShot += deltatime;

        if (timeSinceLastShot >= interval) {
            timeSinceLastShot = 0;
            LaserManager.getInstance().shoot(playingState.getPaddle());
        }

        if (remainingTime <= 0) {
            remove(playingState);
        }
    }

    @Override
    public void remove(PlayingState playingState) {
        remainingTime = 0;
    }

    @Override
    public boolean isExpired() {
        return remainingTime <= 0;
    }

    @Override
    public void reset() {
        remainingTime = 5.0;
    }

    @Override
    public PowerUpStrategy clone() {
        return new LaserPaddleStrategy();
    }
}
