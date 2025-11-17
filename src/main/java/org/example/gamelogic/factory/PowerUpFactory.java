package org.example.gamelogic.factory;

import org.example.gamelogic.entities.powerups.PowerUp;
import org.example.gamelogic.registry.PowerUpRegistry;

/**
 * Quản lý việc tạo (sản xuất) các đối tượng {@link PowerUp}.
 * <p>
 * Lớp này sử dụng ({@link PowerUpRegistry})
 * để nhân bản (clone) các mẫu (prototypes) PowerUp
 * theo mẫu thiết kế (design pattern) Factory và Prototype.
 */
public class PowerUpFactory {
    private final PowerUpRegistry registry;

    /**
     * Khởi tạo PowerUpFactory.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một tham chiếu (instance)
     * của {@link PowerUpRegistry} (Dependency Injection).
     * <p>
     * <b>Expected:</b> Đối tượng Factory được tạo,
     * liên kết với Registry,
     * và sẵn sàng để gọi {@code createPowerUp}.
     *
     * @param registry Instance của PowerUpRegistry
     * chứa các mẫu (prototypes).
     */
    public PowerUpFactory(PowerUpRegistry registry) {
        this.registry = registry;
    }

    /**
     * Tạo một instance PowerUp mới dựa trên loại (type)
     * và vị trí (x, y).
     * <p>
     * <b>Định nghĩa:</b> Lấy mẫu (prototype) từ Registry
     * dựa trên {@code powerUpType},
     * gọi {@code clone()} để nhân bản,
     * và đặt vị trí (x, y) cho đối tượng mới.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link PowerUp} mới
     * đã được khởi tạo tại (x, y).
     * Ném {@link IllegalArgumentException}
     * nếu {@code powerUpType} không tồn tại.
     *
     * @param powerUpType Loại PowerUp (String key)
     * đã đăng ký trong Registry.
     * @param x           Tọa độ X (đích) để tạo.
     * @param y           Tọa độ Y (đích) để tạo.
     * @return Một instance {@code PowerUp} mới.
     * @throws IllegalArgumentException Nếu {@code powerUpType}
     * không được tìm thấy trong Registry.
     */
    public PowerUp createPowerUp(String powerUpType, double x, double y) {
        PowerUp prototype = registry.getPrototype(powerUpType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found: " + powerUpType);
        }
        PowerUp newPowerUp = prototype.clone();
        newPowerUp.setPosition(x, y);
        return newPowerUp;
    }
}