package org.example.gamelogic.registry;

import org.example.gamelogic.entities.enemy.Enemy;

import java.util.HashMap;
import java.util.Map;

public class EnemyRegistry {
    private final Map<String, Enemy> prototypes = new HashMap<>();

    private static class SingletonHolder {
        private static final EnemyRegistry INSTANCE = new EnemyRegistry();
    }

    public static EnemyRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void register(String key, Enemy prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    public Enemy getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}
