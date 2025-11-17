package org.example.presentation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;

/**
 * Điều khiển hoạt ảnh theo hàng (row) trong sprite sheet.
 * Dùng cho animation power-up hoặc hiệu ứng có nhiều frame nằm trên cùng một hàng.
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
     * Tạo hoạt ảnh sử dụng một hàng trong sprite sheet.
     *
     * @param spriteSheet   ảnh sprite sheet chứa các frame
     * @param spriteRow     hàng (row) chứa animation
     * @param totalFrames   tổng số frame trong animation
     * @param frameDuration thời gian hiển thị mỗi frame (giây)
     */
    public RowAnimation(Image spriteSheet, int spriteRow, int totalFrames, double frameDuration) {
        this.spriteSheet = spriteSheet;
        this.spriteRow = spriteRow;
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
    }

    /**
     * Cập nhật frame theo thời gian.
     *
     * @param deltaTime thời gian trôi qua giữa hai cập nhật (giây)
     */
    public void update(double deltaTime) {
        frameTimer += deltaTime;
        if (frameTimer >= frameDuration) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % totalFrames;
        }
    }

    /**
     * Vẽ frame hiện tại của animation lên canvas.
     *
     * @param gc context để vẽ lên
     * @param x  vị trí X để vẽ
     * @param y  vị trí Y để vẽ
     * @param w  chiều rộng hiển thị
     * @param h  chiều cao hiển thị
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
