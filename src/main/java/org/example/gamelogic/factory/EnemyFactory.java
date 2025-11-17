package org.example.gamelogic.factory;

import org.example.gamelogic.entities.enemy.Enemy;
import org.example.gamelogic.registry.EnemyRegistry;

/**
 * Quản lý việc tạo (sản xuất) các đối tượng {@link Enemy}.
 * <p>
 * Lớp này sử dụng ({@link EnemyRegistry})
 * để nhân bản (clone) các mẫu (prototypes) Enemy
 * theo mẫu thiết kế (design pattern) Factory và Prototype.
 */
public class EnemyFactory {
    private final EnemyRegistry registry;

    /**
     * Khởi tạo EnemyFactory.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ một tham chiếu (instance)
     * của {@link EnemyRegistry} (Dependency Injection).
     * <p>
     * <b>Expected:</b> Đối tượng Factory được tạo,
     * liên kết với Registry,
     * và sẵn sàng để gọi {@code createEnemy}.
     *
     * @param registry Instance của EnemyRegistry
     * chứa các mẫu (prototypes).
     */
    public EnemyFactory(EnemyRegistry registry) {
        this.registry = registry;
    }

    /**
     * Tạo một instance Enemy mới dựa trên loại (type)
     * và vị trí (x, y).
     * <p>
     * <b>Định nghĩa:</b> Lấy mẫu (prototype) từ Registry
     * dựa trên {@code enemyType},
     * gọi {@code clone()} để nhân bản,
     * và đặt vị trí (x, y) cho đối tượng mới.
     * <p>
     * <b>Expected:</b> Trả về một đối tượng {@link Enemy} mới
     * đã được khởi tạo tại (x, y).
     * Ném {@link IllegalArgumentException}
     * nếu {@code enemyType} không tồn tại.
     *
     * @param enemyType Loại Enemy (String key)
     * đã đăng ký trong Registry.
     * @param x         Tọa độ X (đích) để tạo.
     * @param y         Tọa độ Y (đích) để tạo.
     * @return Một instance {@code Enemy} mới.
     * @throws IllegalArgumentException Nếu {@code enemyType}
     * không được tìm thấy trong Registry.
     */
    public Enemy createEnemy(String enemyType, double x, double y) {
        Enemy prototype = registry.getPrototype(enemyType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found: " + enemyType);
        }
        Enemy newEnemy = prototype.clone();
        newEnemy.setPosition(x,y);
        return newEnemy;
    }
}