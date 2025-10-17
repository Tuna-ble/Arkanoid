package org.example.gamelogic.core;

public final class BallManager {
    private static class SingletonHolder {
        private static final BallManager INSTANCE = new BallManager();
    }

    public static BallManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
