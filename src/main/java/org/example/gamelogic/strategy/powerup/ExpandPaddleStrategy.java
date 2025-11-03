package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.states.GameState;
import org.example.gamelogic.states.PlayingState;

public class ExpandPaddleStrategy implements PowerUpStrategy {
    private final double expansionFactor = 1.5;
    private double remainingTime = 10.0;
    private double originalWidth;

    @Override
    public void apply(PlayingState playingState) {
        Paddle p = playingState.getPaddle();
        if (p != null) {
            originalWidth = p.getWidth();
            p.setX(p.getX() - p.getWidth() * (expansionFactor - 1) / 2);
            p.setWidth(p.getWidth() * expansionFactor);
        }
    }

    @Override
    public void update(PlayingState playingState, double deltatime) {
        remainingTime -= deltatime;
        if (remainingTime <= 0) remove(playingState);
    }

    @Override
    public void remove(PlayingState playingState) {
        Paddle p = playingState.getPaddle();
        if (p != null) {
            p.setX(p.getX() + p.getWidth() * (1 - 1 / expansionFactor) / 2);
            p.setWidth(originalWidth);
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
        return new ExpandPaddleStrategy();
    }
}
