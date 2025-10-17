package org.example.gamelogic.core;

//Observer pattern
public final class EventManager {
    private static class SingletonHolder {
        private static final EventManager INSTANCE = new EventManager();
    }

    public static EventManager getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
