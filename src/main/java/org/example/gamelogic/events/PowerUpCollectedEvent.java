package org.example.gamelogic.events;

import org.example.gamelogic.entities.powerups.PowerUp;

public final class PowerUpCollectedEvent extends GameEvent {
    private final PowerUp collectedPowerUp;

    public PowerUpCollectedEvent(PowerUp powerUp) {
        this.collectedPowerUp = powerUp;
    }

    public PowerUp getPowerUpCollected() {
        return collectedPowerUp;
    }
}
