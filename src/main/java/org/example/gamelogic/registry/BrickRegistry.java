package org.example.gamelogic.registry;

import org.example.gamelogic.core.PowerUpManager;
import org.example.gamelogic.entities.bricks.Brick;
import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý (registry) các mẫu (prototypes) của Brick.
 * <p>
 * Lớp này sử dụng mẫu Singleton và Prototype,
 * cho phép đăng ký và truy xuất các đối tượng Brick mẫu
 * (dùng để nhân bản - clone).
 */
public class BrickRegistry {
    private final Map<String, Brick> prototypes = new HashMap<>();

    /**
     * Lớp nội tĩnh (static inner class)
     * để giữ instance duy nhất của Singleton.
     */
    private static class SingletonHolder {
        private static final BrickRegistry INSTANCE = new BrickRegistry();
    }

    /**
     * Lấy về instance duy nhất của BrickRegistry.
     * <p>
     * <b>Định nghĩa:</b> Cung cấp quyền truy cập toàn cục
     * vào Singleton instance.
     * <p>
     * <b>Expected:</b> Trả về đối tượng {@code BrickRegistry} duy nhất.
     *
     * @return Instance của BrickRegistry.
     */
    public static BrickRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Đăng ký một Brick mẫu (prototype) với một khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một đối tượng {@code prototype}
     * vào Map với {@code key} (được chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> {@code prototype} được thêm vào Map
     * và có thể được truy xuất bằng {@code key}.
     *
     * @param key       Khóa (String) để định danh prototype.
     * @param prototype Đối tượng Brick mẫu.
     */
    public void register(String key, Brick prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    /**
     * Lấy về một Brick mẫu (prototype) dựa trên khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Truy xuất đối tượng {@code Brick}
     * từ Map dựa trên {@code key} (đã chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> Trả về {@code Brick} mẫu
     * tương ứng với {@code key}, hoặc {@code null} nếu không tìm thấy.
     *
     * @param key Khóa (String) của prototype cần lấy.
     * @return Đối tượng Brick mẫu.
     */
    public Brick getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}