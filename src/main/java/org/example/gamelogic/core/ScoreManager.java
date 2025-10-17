package org.example.gamelogic.core;

public final class ScoreManager {
    private static class SingletonHolder {
        private static final ScoreManager INSTANCE = new ScoreManager();
    }

    public static ScoreManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
