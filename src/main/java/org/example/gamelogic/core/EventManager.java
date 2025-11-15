package org.example.gamelogic.core;

import org.example.gamelogic.events.GameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class EventManager {
    private static class SingletonHolder {
        private static final EventManager INSTANCE = new EventManager();
    }

    public static EventManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final Map<Class<? extends GameEvent>, List<Consumer>> listeners = new HashMap<>();

    public synchronized <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer> eventListeners = listeners.computeIfAbsent(eventType, k -> new ArrayList<>());
        eventListeners.add(listener);
    }

    public synchronized <T extends GameEvent> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public synchronized void publish(GameEvent event) {
        Class<?> eventType = event.getClass();
        List<Consumer> eventListeners = listeners.get(eventType);

        if (eventListeners == null) return;

        List<Consumer> listenersCopy = new ArrayList<>(eventListeners);

        for (Consumer listener : listenersCopy) {
            listener.accept(event);
        }
    }
}
