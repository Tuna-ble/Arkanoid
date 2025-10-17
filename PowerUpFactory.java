package org.example.gamelogic.factory;

import org.example.gamelogic.entities.powerups.PowerUp;
import org.example.gamelogic.registry.PowerUpRegistry;

public class PowerUpFactory {
    private final PowerUpRegistry registry;

    public PowerUpFactory(PowerUpRegistry registry) {
        this.registry = registry;
    }

    public PowerUp createPowerUp(String powerUpType, double x, double y) {
        PowerUp prototype = registry.getPrototype(powerUpType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found" + powerUpType);
        }
        PowerUp newPowerUp = prototype.clone();
        newPowerUp.setPosition(x, y);
        return newPowerUp;
    }
}
