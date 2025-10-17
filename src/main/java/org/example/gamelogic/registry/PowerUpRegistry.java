package org.example.gamelogic.registry;

import org.example.gamelogic.entities.powerups.PowerUp;

import java.util.HashMap;
import java.util.Map;

public class PowerUpRegistry {
    private final Map<String, PowerUp> prototypes = new HashMap<>();

    private static class SingletonHolder {
        private static final PowerUpRegistry INSTANCE = new PowerUpRegistry();
    }

    public static PowerUpRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void register(String key, PowerUp prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    public PowerUp getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}
