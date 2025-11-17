package org.example.gamelogic.registry;

import org.example.gamelogic.entities.powerups.PowerUp;

import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý (registry) các mẫu (prototypes) của PowerUp.
 * <p>
 * Lớp này sử dụng mẫu Singleton và Prototype,
 * cho phép đăng ký và truy xuất các đối tượng PowerUp mẫu
 * (dùng để nhân bản - clone).
 */
public class PowerUpRegistry {
    private final Map<String, PowerUp> prototypes = new HashMap<>();

    /**
     * Lớp nội tĩnh (static inner class)
     * để giữ instance duy nhất của Singleton.
     */
    private static class SingletonHolder {
        private static final PowerUpRegistry INSTANCE = new PowerUpRegistry();
    }

    /**
     * Lấy về instance duy nhất của PowerUpRegistry.
     * <p>
     * <b>Định nghĩa:</b> Cung cấp quyền truy cập toàn cục
     * vào Singleton instance.
     * <p>
     * <b>Expected:</b> Trả về đối tượng {@code PowerUpRegistry} duy nhất.
     *
     * @return Instance của PowerUpRegistry.
     */
    public static PowerUpRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Đăng ký một PowerUp mẫu (prototype) với một khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một đối tượng {@code prototype}
     * vào Map với {@code key} (được chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> {@code prototype} được thêm vào Map
     * và có thể được truy xuất bằng {@code key}.
     *
     * @param key       Khóa (String) để định danh prototype.
     * @param prototype Đối tượng PowerUp mẫu.
     */
    public void register(String key, PowerUp prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    /**
     * Lấy về một PowerUp mẫu (prototype) dựa trên khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Truy xuất đối tượng {@code PowerUp}
     * từ Map dựa trên {@code key} (đã chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> Trả về {@code PowerUp} mẫu
     * tương ứng với {@code key}, hoặc {@code null} nếu không tìm thấy.
     *
     * @param key Khóa (String) của prototype cần lấy.
     * @return Đối tượng PowerUp mẫu.
     */
    public PowerUp getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}