package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.BulletType;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.events.BrickDamagedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Quản lý các tia laser/đạn: tạo, cập nhật, render và truy xuất danh sách.
 */
public final class LaserManager {
    private static class SingletonHolder {
        private static final LaserManager INSTANCE = new LaserManager();
    }

    /**
     * Lấy instance đơn của LaserManager.
     *
     * @return singleton LaserManager
     */
    public static LaserManager getInstance() {
        return LaserManager.SingletonHolder.INSTANCE;
    }

    private final List<LaserBullet> lasers=new ArrayList<>();

    /**
     * Tạo một tia laser mới và thêm vào danh sách quản lý.
     *
     * @param x toạ độ x bắt đầu
     * @param y toạ độ y bắt đầu
     * @param dx vận tốc x
     * @param dy vận tốc y
     * @param type loại viên đạn
     * @param faction phe bắn (player hoặc enemy)
     */
    public void createBullet(double x, double y, double dx, double dy, BulletType type, BulletFrom faction) {
        lasers.add(new LaserBullet(x, y, dx, dy, type, faction));
    }

    /**
     * Cập nhật trạng thái tất cả tia laser và xoá các tia không còn active.
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
    public void update(double deltaTime) {
        Iterator<LaserBullet> iterator= lasers.iterator();
        while (iterator.hasNext()) {
            LaserBullet laser=iterator.next();
            laser.update(deltaTime);

            if (!laser.isActive()) {
                iterator.remove();
            }
        }
    }

    /**
     * Vẽ tất cả tia laser lên canvas.
     *
     * @param gc GraphicsContext (kỳ vọng không null)
     */
    public void render(GraphicsContext gc) {
        for (LaserBullet laser : lasers) {
            laser.render(gc);
        }
    }

    /**
     * Lấy danh sách tia laser đang tồn tại.
     *
     * @return danh sách LaserBullet (mutable)
     */
    public List<LaserBullet> getLasers() {
        return lasers;
    }

    /**
     * Xóa tất cả tia laser.
     */
    public void clear() {
        lasers.clear();
    }
}
