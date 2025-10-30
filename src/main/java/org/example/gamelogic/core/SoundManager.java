package org.example.gamelogic.core;

public class SoundManager {
    private static class SingletonHolder {
        private static final SoundManager INSTANCE = new SoundManager();
    }

    public static SoundManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
