package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.GameManager;

public interface PowerUpStrategy {
    void apply(GameManager gm);
    void remove(GameManager gm);
}
