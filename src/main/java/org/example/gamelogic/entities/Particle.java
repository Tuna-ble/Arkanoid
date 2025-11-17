package org.example.gamelogic.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Quản lý một đối tượng hạt (Particle) dùng cho hiệu ứng (visual effect).
 * <p>
 * Lớp này kế thừa từ {@link MovableObject},
 * thêm logic về "vòng đời" ({@code lifeSpan}),
 * trọng lực ({@code gravity}),
 * và hiệu ứng mờ dần (fade-out) khi render.
 */
public class Particle extends MovableObject {

    private double lifeSpan;
    private final double maxLifeSpan;
    private final Color color;
    private final double gravity = 980.0;

    /**
     * Khởi tạo một hạt (Particle) mới.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí,
     * vận tốc ban đầu (dx, dy), kích thước,
     * thời gian sống tối đa, và màu sắc cho hạt.
     * <p>
     * <b>Expected:</b> Một đối tượng Particle được tạo,
     * {@code isActive} là {@code true},
     * và sẵn sàng để update/render.
     *
     * @param x         Tọa độ X ban đầu.
     * @param y         Tọa độ Y ban đầu.
     * @param dx        Vận tốc X ban đầu (pixel/giây).
     * @param dy        Vận tốc Y ban đầu (pixel/giây).
     * @param width     Chiều rộng.
     * @param height    Chiều cao.
     * @param lifeSpan  Thời gian sống (giây).
     * @param color     Màu sắc ({@link Color}) của hạt.
     */
    public Particle(double x, double y, double dx, double dy,
                    double width, double height, double lifeSpan, Color color) {

        super(x, y, width, height, dx, dy);

        this.maxLifeSpan = lifeSpan;
        this.lifeSpan = lifeSpan;
        this.color = color;
        this.isActive = true;
    }

    /**
     * Cập nhật logic của hạt (vị trí, vòng đời, trọng lực).
     * <p>
     * <b>Định nghĩa:</b> Giảm {@code lifeSpan} theo {@code deltaTime}.
     * Cập nhật vị trí (x, y) dựa trên vận tốc (dx, dy).
     * Áp dụng {@code gravity} vào vận tốc Y ({@code dy}).
     * <p>
     * <b>Expected:</b> {@code isActive} đặt thành {@code false}
     * nếu {@code lifeSpan} <= 0.
     * Hạt di chuyển và bị kéo xuống dưới bởi trọng lực.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        if (!isActive) return;

        lifeSpan -= deltaTime;
        if (lifeSpan <= 0) {
            this.isActive = false;
            return;
        }

        this.x += this.dx * deltaTime;
        this.y += this.dy * deltaTime;
        this.dy += gravity * deltaTime;
    }

    /**
     * Vẽ (render) hạt lên canvas với hiệu ứng mờ dần (fade-out).
     * <p>
     * <b>Định nghĩa:</b> Tính toán độ mờ (alpha)
     * dựa trên {@code lifeSpan} còn lại.
     * Vẽ một hình chữ nhật (fillRect)
     * với màu sắc và độ mờ đã tính toán.
     * <p>
     * <b>Expected:</b> Hạt được vẽ lên {@code gc}.
     * Hạt sẽ mờ dần (giảm alpha) khi {@code lifeSpan} giảm.
     * Không vẽ gì nếu {@code isActive} là {@code false}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;
        double alpha = Math.max(0, lifeSpan / maxLifeSpan);

        gc.save();
        try {
            gc.setGlobalAlpha(alpha);
            gc.setFill(this.color);
            gc.fillRect(this.x, this.y, this.width, this.height);
        } finally {
            gc.restore();
        }
    }

    /**
     * Kiểm tra xem hạt đã bị "hủy" (hết vòng đời) hay chưa.
     * <p>
     * <b>Định nghĩa:</b> Trả về trạng thái
     * ngược của {@code isActive}.
     * <p>
     * <b>Expected:</b> {@code true} nếu hạt đã hết
     * thời gian sống, ngược lại {@code false}.
     *
     * @return boolean Trạng thái đã bị hủy.
     */
    public boolean isDestroyed() {
        return !this.isActive;
    }
}