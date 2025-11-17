package org.example.gamelogic.registry;

import org.example.gamelogic.entities.enemy.Enemy;

import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý (registry) các mẫu (prototypes) của Enemy.
 * <p>
 * Lớp này sử dụng mẫu Singleton và Prototype,
 * cho phép đăng ký và truy xuất các đối tượng Enemy mẫu
 * (dùng để nhân bản - clone).
 */
public class EnemyRegistry {
    private final Map<String, Enemy> prototypes = new HashMap<>();

    /**
     * Lớp nội tĩnh (static inner class)
     * để giữ instance duy nhất của Singleton.
     */
    private static class SingletonHolder {
        private static final EnemyRegistry INSTANCE = new EnemyRegistry();
    }

    /**
     * Lấy về instance duy nhất của EnemyRegistry.
     * <p>
     * <b>Định nghĩa:</b> Cung cấp quyền truy cập toàn cục
     * vào Singleton instance.
     * <p>
     * <b>Expected:</b> Trả về đối tượng {@code EnemyRegistry} duy nhất.
     *
     * @return Instance của EnemyRegistry.
     */
    public static EnemyRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Đăng ký một Enemy mẫu (prototype) với một khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một đối tượng {@code prototype}
     * vào Map với {@code key} (được chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> {@code prototype} được thêm vào Map
     * và có thể được truy xuất bằng {@code key}.
     *
     * @param key       Khóa (String) để định danh prototype.
     * @param prototype Đối tượng Enemy mẫu.
     */
    public void register(String key, Enemy prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    /**
     * Lấy về một Enemy mẫu (prototype) dựa trên khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Truy xuất đối tượng {@code Enemy}
     * từ Map dựa trên {@code key} (đã chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> Trả về {@code Enemy} mẫu
     * tương ứng với {@code key}, hoặc {@code null} nếu không tìm thấy.
     *
     * @param key Khóa (String) của prototype cần lấy.
     * @return Đối tượng Enemy mẫu.
     */
    public Enemy getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}