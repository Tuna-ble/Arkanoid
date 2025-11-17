package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.presentation.SpriteAnimation;

/**
 * Quản lý một đối tượng đạn (LaserBullet) được bắn ra (từ Player hoặc Enemy).
 * <p>
 * Kế thừa từ {@link MovableObject}, lớp này xử lý logic di chuyển,
 * tự hủy (deactivate) khi ra khỏi màn hình,
 * và logic render (vẽ) khác nhau
 * tùy thuộc vào phe ({@code faction}) và loại ({@code type}).
 */
public class LaserBullet extends MovableObject {
    private final BulletFrom faction;
    private final BulletType type;
    private final Image normalBulletImage;
    private final Image bossBulletSheet;
    private final SpriteAnimation bossBullet;

    /**
     * Khởi tạo một đối tượng LaserBullet mới.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super` (MovableObject)
     * để thiết lập vị trí, kích thước, vận tốc.
     * Lưu trữ {@code type} và {@code faction}.
     * Tải ảnh/animation (vd: "bullet", "bossBullet")
     * từ {@link AssetManager}.
     * <p>
     * <b>Expected:</b> Một đối tượng đạn được tạo,
     * sẵn sàng di chuyển và render
     * theo phe (faction) và loại (type) của nó.
     *
     * @param x       Tọa độ X ban đầu.
     * @param y       Tọa độ Y ban đầu.
     * @param dx      Vận tốc X (pixel/giây).
     * @param dy      Vận tốc Y (pixel/giây).
     * @param type    Loại đạn ({@link BulletType}),
     * quyết định kích thước/hình ảnh.
     * @param faction Phe bắn ra ({@link BulletFrom}),
     * quyết định hình ảnh (PLAYER/ENEMY).
     */
    public LaserBullet(double x, double y, double dx, double dy, BulletType type, BulletFrom faction) {
        super(x, y, type.width, type.height, dx, dy);
        this.faction = faction;
        this.type = type;

        AssetManager am = AssetManager.getInstance();
        normalBulletImage = am.getImage("bullet");
        bossBulletSheet = am.getImage("bossBullet");
        bossBullet = new SpriteAnimation(bossBulletSheet, 4, 4, 0.5, true);
    }

    /**
     * Cập nhật logic của đạn (vị trí, vòng đời, animation).
     * <p>
     * <b>Định nghĩa:</b> Di chuyển đạn
     * (thay đổi x, y) dựa trên vận tốc (dx, dy).
     * Cập nhật (update) animation của đạn boss.
     * Kiểm tra nếu đạn bay ra khỏi ranh giới màn hình.
     * <p>
     * <b>Expected:</b> Vị trí của đạn được cập nhật.
     * {@code isActive} (từ {@code GameObject})
     * được đặt thành {@code false}
     * nếu đạn bay ra khỏi màn hình,
     * để chuẩn bị bị xóa.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        x += dx * deltaTime;
        y += dy * deltaTime;

        bossBullet.update(deltaTime);

        if (y + height < 0 || y > GameConstants.SCREEN_HEIGHT ||
                x + width < 0 || x > GameConstants.SCREEN_WIDTH) {
            isActive = false;
        }
    }

    /**
     * Vẽ (render) đạn lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Quyết định hình ảnh/animation
     * để vẽ dựa trên {@code faction} và {@code type}.
     * Vẽ ảnh đạn (player) hoặc animation (boss)
     * hoặc hình chữ nhật (fallback cho enemy).
     * <p>
     * <b>Expected:</b> Hình ảnh trực quan của đạn
     * được vẽ lên {@code gc}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (faction == BulletFrom.PLAYER) {
            gc.drawImage(normalBulletImage, x, y, width, height);
        } else {
            if (type == BulletType.BOSS_HOMING_SQUARE) {
                bossBullet.render(gc, x, y, width, height);
            } else {
                gc.setFill(Color.RED);
                gc.fillRect(x, y, width, height);
            }
        }

    }

    /**
     * Lấy phe (faction) của đạn (ai là người bắn).
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của trường {@code faction}.
     * <p>
     * <b>Expected:</b> Trả về {@link BulletFrom}
     * (PLAYER hoặc ENEMY).
     *
     * @return Phe của đạn.
     */
    public BulletFrom getFaction() {
        return this.faction;
    }
}