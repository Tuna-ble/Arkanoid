package org.example.presentation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Hoạt ảnh theo dạng sprite sheet nhiều hàng/cột.
 * Cho phép loop hoặc chạy một lần rồi kết thúc.
 */
public class SpriteAnimation {
    private final Image spriteSheet;
    private final int frameCount;
    private final int frameWidth;
    private final int frameHeight;
    private final int columns;
    private final double frameDuration;

    private double elapsedTime = 0;
    private int currentFrame = 0;
    private boolean loops = true;
    private boolean isFinished = false;

    /**
     * Tạo một sprite animation từ sprite sheet.
     *
     * @param sheet       ảnh sprite sheet
     * @param frameCount  tổng số frame animation
     * @param columns     số cột trong sprite sheet
     * @param duration    tổng thời gian chạy toàn bộ animation (giây)
     * @param loops       true nếu animation lặp lại
     */
    public SpriteAnimation(Image sheet, int frameCount, int columns, double duration, boolean loops) {
        this.spriteSheet = sheet;
        this.frameCount = frameCount;
        this.columns = columns;
        this.frameWidth = (int) (sheet.getWidth() / columns);
        this.frameHeight = (int) (sheet.getHeight() / (Math.ceil((double) frameCount / columns)));
        this.frameDuration = duration / frameCount;
        this.loops = loops;
    }

    /**
     * Cập nhật tiến trình animation theo thời gian.
     *
     * @param deltaTime thời gian trôi qua giữa hai frame (giây)
     */
    public void update(double deltaTime) {
        if (isFinished) return;

        elapsedTime += deltaTime;
        if (elapsedTime >= frameDuration) {
            elapsedTime -= frameDuration;
            currentFrame++;
            if (currentFrame >= frameCount) {
                if (loops) {
                    currentFrame = 0;
                } else {
                    isFinished = true;
                    currentFrame = frameCount - 1;
                }
            }
        }
    }

    /**
     * Vẽ frame hiện tại của animation lên canvas.
     *
     * @param gc context dùng để vẽ
     * @param x  vị trí X hiển thị
     * @param y  vị trí Y hiển thị
     * @param w  chiều rộng hiển thị
     * @param h  chiều cao hiển thị
     */
    public void render(GraphicsContext gc, double x, double y, double w, double h) {
        int sx = (currentFrame % columns) * frameWidth;
        int sy = (currentFrame / columns) * frameHeight;

        gc.drawImage(
                spriteSheet,
                sx, sy, frameWidth, frameHeight,
                x, y, w, h
        );
    }

    /**
     * Kiểm tra animation đã kết thúc hay chưa (chỉ áp dụng khi loops = false).
     *
     * @return true nếu animation đã chạy xong
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Đặt lại animation về frame đầu tiên.
     * <br>Dùng khi muốn chạy lại từ đầu.
     */
    public void reset() {
        this.currentFrame = 0;
        this.elapsedTime = 0;
        this.isFinished = false;
    }
}
