package org.example.gamelogic.core;

public final class CollisionManager {
    private static class SingletonHolder {
        private static final CollisionManager INSTANCE = new CollisionManager();
    }

    public static CollisionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
