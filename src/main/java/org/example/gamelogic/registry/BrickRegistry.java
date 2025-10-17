package org.example.gamelogic.registry;

import org.example.gamelogic.core.PowerUpManager;
import org.example.gamelogic.entities.bricks.Brick;
import java.util.HashMap;
import java.util.Map;

public class BrickRegistry {
    private final Map<String, Brick> prototypes = new HashMap<>();

    private static class SingletonHolder {
        private static final BrickRegistry INSTANCE = new BrickRegistry();
    }

    public static BrickRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void register(String key, Brick prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    public Brick getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}
