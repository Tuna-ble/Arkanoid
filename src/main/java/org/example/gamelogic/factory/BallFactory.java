package org.example.gamelogic.factory;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.registry.BallRegistry;


/**
 * Quản lý việc tạo (sản xuất) các đối tượng {@link IBall}.
 * <p>
 * Lớp này sử dụng ({@link BallRegistry})
 * để nhân bản (clone) các mẫu (prototypes) Ball
 * theo mẫu thiết kế (design pattern) Factory và Prototype.
 */
public class BallFactory {
    private final BallRegistry registry;

    /**
     * Khởi tạo BallFactory.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một tham chiếu (instance)
     * của {@link BallRegistry} (Dependency Injection).
     * <p>
     * <b>Expected:</b> Đối tượng Factory được tạo,
     * liên kết với Registry,
     * và sẵn sàng để gọi {@code createBall}.
     *
     * @param registry Instance của BallRegistry
     * chứa các mẫu (prototypes).
     */
    public BallFactory(BallRegistry registry) {
        this.registry = registry;
    }

    /**
     * Tạo một instance Ball mới dựa trên loại (type)
     * và vị trí (x, y).
     * <p>
     * <b>Định nghĩa:</b> Lấy mẫu (prototype) từ Registry
     * dựa trên {@code ballType},
     * gọi {@code clone()} để nhân bản,
     * và đặt vị trí (x, y) cho đối tượng mới.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link IBall} mới
     * đã được khởi tạo tại (x, y).
     * Ném {@link IllegalArgumentException}
     * nếu {@code ballType} không tồn tại.
     *
     * @param ballType Loại Ball (String key)
     * đã đăng ký trong Registry.
     * @param x        Tọa độ X (đích) để tạo.
     * @param y        Tọa độ Y (đích) để tạo.
     * @return Một instance {@code IBall} mới.
     * @throws IllegalArgumentException Nếu {@code ballType}
     * không được tìm thấy trong Registry.
     */
    public IBall createBall(String ballType, double x, double y) {
        IBall prototype = registry.getPrototype(ballType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found: " + ballType);
        }
        IBall newBall = prototype.clone();
        newBall.setPosition(x,y);
        return newBall;
    }
}