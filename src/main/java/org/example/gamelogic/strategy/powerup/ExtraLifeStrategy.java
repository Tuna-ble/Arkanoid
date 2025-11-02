package org.example.gamelogic.strategy.powerup;

import org.example.config.GameConstants;
import org.example.gamelogic.core.BallManager;
import org.example.gamelogic.core.LifeManager;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.states.PlayingState;

import java.util.ArrayList;
import java.util.List;

public class ExtraLifeStrategy implements PowerUpStrategy {
    @Override
    public void apply(PlayingState playingState) {
        LifeManager.getInstance().addLife();
    }

    @Override
    public void update(PlayingState playingState, double deltatime) {

    }

    @Override
    public void remove(PlayingState playingState) {

    }

    @Override
    public boolean isExpired() {
        return true;
    }

    @Override
    public void reset() {

    }

    @Override
    public PowerUpStrategy clone() {
        return new ExtraLifeStrategy();
    }
}
