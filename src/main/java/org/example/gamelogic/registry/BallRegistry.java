package org.example.gamelogic.registry;

import org.example.gamelogic.entities.IBall;

import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý (registry) các mẫu (prototypes) của Ball (IBall).
 * <p>
 * Lớp này sử dụng mẫu Singleton và Prototype,
 * cho phép đăng ký và truy xuất các đối tượng IBall mẫu
 * (dùng để nhân bản - clone).
 */
public class BallRegistry {
    private final Map<String, IBall> prototypes = new HashMap<>();

    /**
     * Lớp nội tĩnh (static inner class)
     * để giữ instance duy nhất của Singleton.
     */
    private static class SingletonHolder {
        private static final BallRegistry INSTANCE = new BallRegistry();
    }

    /**
     * Lấy về instance duy nhất của BallRegistry.
     * <p>
     * <b>Định nghĩa:</b> Cung cấp quyền truy cập toàn cục
     * vào Singleton instance.
     * <p>
     * <b>Expected:</b> Trả về đối tượng {@code BallRegistry} duy nhất.
     *
     * @return Instance của BallRegistry.
     */
    public static BallRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Đăng ký một Ball mẫu (prototype) với một khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một đối tượng {@code prototype}
     * vào Map với {@code key} (được chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> {@code prototype} được thêm vào Map
     * và có thể được truy xuất bằng {@code key}.
     *
     * @param key       Khóa (String) để định danh prototype.
     * @param prototype Đối tượng IBall mẫu.
     */
    public void register(String key, IBall prototype) {
        prototypes.put(key.toUpperCase(), prototype);
    }

    /**
     * Lấy về một Ball mẫu (prototype) dựa trên khóa (key).
     * <p>
     * <b>Định nghĩa:</b> Truy xuất đối tượng {@code IBall}
     * từ Map dựa trên {@code key} (đã chuyển thành chữ hoa).
     * <p>
     * <b>Expected:</b> Trả về {@code IBall} mẫu
     * tương ứng với {@code key}, hoặc {@code null} nếu không tìm thấy.
     *
     * @param key Khóa (String) của prototype cần lấy.
     * @return Đối tượng IBall mẫu.
     */
    public IBall getPrototype(String key) {
        return prototypes.get(key.toUpperCase());
    }
}