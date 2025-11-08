package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.states.PlayingState;

public interface PowerUpStrategy {
    void apply(PlayingState playingState);
    void update(PlayingState playingState, double deltaTime);
    void remove(PlayingState playingState);
    boolean isExpired();
    void reset();
    PowerUpStrategy clone();
}
