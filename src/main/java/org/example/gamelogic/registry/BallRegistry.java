package org.example.gamelogic.registry;

import org.example.gamelogic.entities.IBall;

import java.util.HashMap;
import java.util.Map;

public class BallRegistry {
    private final Map<String, IBall> prototypes = new HashMap<>();

    private static class SingletonHolder {
        private static final BallRegistry INSTANCE = new BallRegistry();
    }

    public static BallRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void register(String key, IBall prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    public IBall getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}