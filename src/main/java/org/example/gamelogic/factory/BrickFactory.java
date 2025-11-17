package org.example.gamelogic.factory;

import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.registry.BrickRegistry;

/**
 * Quản lý việc tạo (sản xuất) các đối tượng {@link Brick}.
 * <p>
 * Lớp này sử dụng ({@link BrickRegistry})
 * để nhân bản (clone) các mẫu (prototypes) Brick
 * theo mẫu thiết kế (design pattern) Factory và Prototype.
 */
public class BrickFactory {
    private final BrickRegistry registry;

    /**
     * Khởi tạo BrickFactory.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một tham chiếu (instance)
     * của {@link BrickRegistry} (Dependency Injection).
     * <p>
     * <b>Expected:</b> Đối tượng Factory được tạo,
     * liên kết với Registry,
     * và sẵn sàng để gọi {@code createBrick}.
     *
     * @param registry Instance của BrickRegistry
     * chứa các mẫu (prototypes).
     */
    public BrickFactory(BrickRegistry registry) {
        this.registry = registry;
    }

    /**
     * Tạo một instance Brick mới dựa trên loại (type)
     * và vị trí (x, y).
     * <p>
     * <b>Định nghĩa:</b> Lấy mẫu (prototype) từ Registry
     * dựa trên {@code brickType},
     * gọi {@code clone()} để nhân bản,
     * và đặt vị trí (x, y) cho đối tượng mới.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Brick} mới
     * đã được khởi tạo tại (x, y).
     * Ném {@link IllegalArgumentException}
     * nếu {@code brickType} không tồn tại.
     *
     * @param brickType Loại Brick (String key)
     * đã đăng ký trong Registry.
     * @param x         Tọa độ X (đích) để tạo.
     * @param y         Tọa độ Y (đích) để tạo.
     * @return Một instance {@code Brick} mới.
     * @throws IllegalArgumentException Nếu {@code brickType}
     * không được tìm thấy trong Registry.
     */
    public Brick createBrick(String brickType, double x, double y) {
        Brick prototype = registry.getPrototype(brickType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found: " + brickType);
        }
        Brick newBrick = prototype.clone();
        newBrick.setPosition(x,y);
        return newBrick;
    }
}