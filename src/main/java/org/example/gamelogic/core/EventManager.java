package org.example.gamelogic.core;

import org.example.gamelogic.events.GameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Hệ thống phát và lắng nghe sự kiện (pub/sub) nội bộ của game.
 *
 * <p>Thread-safe cho các thao tác subscribe/unsubscribe/publish bằng synchronized.
 */
public final class EventManager {
    private static class SingletonHolder {
        private static final EventManager INSTANCE = new EventManager();
    }

    /**
     * Lấy instance đơn của EventManager.
     *
     * @return singleton EventManager
     */
    public static EventManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final Map<Class<? extends GameEvent>, List<Consumer>> listeners = new HashMap<>();

    /**
     * Đăng ký một listener cho kiểu sự kiện cụ thể.
     *
     * @param eventType lớp sự kiện cần lắng nghe
     * @param listener callback sẽ được gọi khi sự kiện được publish
     * @param <T> kiểu sự kiện
     */
    public synchronized <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer> eventListeners = listeners.computeIfAbsent(eventType, k -> new ArrayList<>());
        eventListeners.add(listener);
    }

    /**
     * Hủy đăng ký listener cho kiểu sự kiện.
     *
     * @param eventType lớp sự kiện
     * @param listener listener cần hủy
     * @param <T> kiểu sự kiện
     */
    public synchronized <T extends GameEvent> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * Phát một sự kiện tới tất cả listener đã đăng ký cho kiểu sự kiện tương ứng.
     *
     * @param event instance của GameEvent (kỳ vọng không null)
     */
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
