package org.example.gamelogic.core;

public final class PowerUpManager {
    private static class SingletonHolder {
        private static final PowerUpManager INSTANCE = new PowerUpManager();
    }

    public static PowerUpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
