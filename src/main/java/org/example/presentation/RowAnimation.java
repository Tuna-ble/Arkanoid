package org.example.presentation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;

/**
 * Quản lý và render một hoạt ảnh dựa trên một hàng (row) của spritesheet.
 * <p>
 * Lớp này xử lý việc chọn đúng frame từ một hàng cụ thể trên ảnh spritesheet
 * và vẽ nó lên canvas.
 */
public class RowAnimation {
    private final Image spriteSheet;
    private final int spriteRow;
    private final int totalFrames;
    private final double frameDuration;

    private int currentFrame = 0;
    private double frameTimer = 0.0;

    private final double SPRITE_OFFSET = GameConstants.POWERUP_SPRITE_OFFSET;
    private final double SPRITE_WIDTH = GameConstants.POWERUP_SPRITE_WIDTH;
    private final double SPRITE_HEIGHT = GameConstants.POWERUP_SPRITE_HEIGHT;
    private final double SPRITE_PADDING = GameConstants.POWERUP_SPRITE_PADDING;

    /**
     * Khởi tạo đối tượng hoạt ảnh hàng.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ spritesheet và các thông số (hàng,
     * số frame, thời lượng) của hoạt ảnh.
     * <p>
     * <b>Expected:</b> Đối tượng được tạo, sẵn sàng chạy hoạt ảnh từ frame 0.
     *
     * @param spriteSheet   Ảnh spritesheet chứa tất cả frame.
     * @param spriteRow     Chỉ số của hàng (Y) cần lấy frame.
     * @param totalFrames   Tổng số frame trong hàng đó.
     * @param frameDuration Thời gian (giây) mà mỗi frame được hiển thị.
     */
    public RowAnimation(Image spriteSheet, int spriteRow, int totalFrames, double frameDuration) {
        this.spriteSheet = spriteSheet;
        this.spriteRow = spriteRow;
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
    }

    /**
     * Cập nhật logic hoạt ảnh.
     * <p>
     * <b>Định nghĩa:</b> Tính toán thời gian trôi qua (`deltaTime`) để
     * quyết định khi nào cần chuyển sang frame tiếp theo.
     * <p>
     * <b>Expected:</b> `currentFrame` được cập nhật (tăng lên 1 hoặc quay về 0)
     * nếu `frameTimer` vượt quá `frameDuration`.
     *
     * @param deltaTime Thời gian (giây) kể từ lần update cuối cùng.
     */
    public void update(double deltaTime) {
        frameTimer += deltaTime;
        if (frameTimer >= frameDuration) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % totalFrames;
        }
    }

    /**
     * Vẽ frame hiện tại của hoạt ảnh lên canvas.
     * <p>
     * <b>Định nghĩa:</b> "Cắt" (lấy) hình ảnh của frame hiện tại
     * (`currentFrame`) từ `spriteSheet` và vẽ nó lên `gc`.
     * <p>
     * <b>Expected:</b> Frame hoạt ảnh hiện tại được vẽ tại vị trí (x, y)
     * với kích thước (w, h) trên canvas.
     *
     * @param gc Context để vẽ.
     * @param x  Tọa độ X đích (trên canvas).
     * @param y  Tọa độ Y đích (trên canvas).
     * @param w  Chiều rộng đích (trên canvas).
     * @param h  Chiều cao đích (trên canvas).
     */
    public void render(GraphicsContext gc, double x, double y, double w, double h) {
        if (spriteSheet == null) return;

        double sourceX = SPRITE_OFFSET + currentFrame * (SPRITE_WIDTH + SPRITE_PADDING);
        double sourceY = SPRITE_OFFSET + spriteRow * (SPRITE_HEIGHT + SPRITE_PADDING);

        gc.drawImage(
                spriteSheet,
                sourceX, sourceY, SPRITE_WIDTH, SPRITE_HEIGHT,
                x, y, w, h
        );
    }
}