package org.example.presentation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Quản lý và render một hoạt ảnh từ một spritesheet phức tạp (có thể nhiều hàng).
 * <p>
 * Lớp này xử lý việc chia spritesheet thành các frame dựa trên số cột
 * và tổng số frame, sau đó render frame hiện tại.
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
     * Khởi tạo đối tượng SpriteAnimation.
     * <p>
     * <b>Định nghĩa:</b> Tính toán kích thước frame (width, height) dựa trên
     * spritesheet, số cột và tổng số frame. Lưu trữ thông tin hoạt ảnh.
     * <p>
     * <b>Expected:</b> Đối tượng được tạo, sẵn sàng chạy hoạt ảnh từ frame 0.
     *
     * @param sheet      Ảnh spritesheet chứa tất cả frame.
     * @param frameCount Tổng số frame của hoạt ảnh (có thể ít hơn số ô trên sheet).
     * @param columns    Số cột (X) mà spritesheet được chia.
     * @param duration   Tổng thời lượng (giây) của toàn bộ hoạt ảnh.
     * @param loops      Hoạt ảnh có lặp lại hay không.
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
     * Cập nhật logic hoạt ảnh, chuyển frame.
     * <p>
     * <b>Định nghĩa:</b> Cộng dồn `deltaTime` vào `elapsedTime` để
     * quyết định khi nào chuyển frame. Xử lý việc lặp lại (loops)
     * hoặc kết thúc (isFinished) hoạt ảnh.
     * <p>
     * <b>Expected:</b> `currentFrame` được cập nhật nếu đủ thời gian,
     * `isFinished` thành true nếu hoạt ảnh không lặp và đã chạy xong.
     *
     * @param deltaTime Thời gian (giây) kể từ lần update cuối cùng.
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
     * Vẽ frame hiện tại của hoạt ảnh lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Tính toán tọa độ (sx, sy) của frame hiện tại
     * trên spritesheet và vẽ nó lên `gc` tại vị trí (x, y)
     * với kích thước (w, h).
     * <p>
     * <b>Expected:</b> Frame hoạt ảnh hiện tại được vẽ lên canvas.
     *
     * @param gc Context để vẽ.
     * @param x  Tọa độ X đích (trên canvas).
     * @param y  Tọa độ Y đích (trên canvas).
     * @param w  Chiều rộng đích (trên canvas).
     * @param h  Chiều cao đích (trên canvas).
     */
    public void render(GraphicsContext gc, double x, double y, double w, double h) {
        int sx = (currentFrame % columns) * frameWidth;
        int sy = (currentFrame / columns) * frameHeight;

        gc.drawImage(spriteSheet,
                sx, sy, frameWidth, frameHeight,
                x, y, w, h);
    }

    /**
     * Kiểm tra xem hoạt ảnh đã kết thúc hay chưa.
     * <p>
     * <b>Định nghĩa:</b> Trả về trạng thái của biến `isFinished`.
     * <p>
     * <b>Expected:</b> `true` nếu hoạt ảnh (không lặp) đã chạy xong,
     * ngược lại `false`.
     *
     * @return boolean Trạng thái kết thúc.
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Đặt lại hoạt ảnh về trạng thái ban đầu.
     * <p>
     * <b>Định nghĩa:</b> Đưa `currentFrame`, `elapsedTime`, và `isFinished`
     * về giá trị mặc định.
     * <p>
     * <b>Expected:</b> Hoạt ảnh có thể chạy lại từ đầu.
     */
    public void reset() {
        this.currentFrame = 0;
        this.elapsedTime = 0;
        this.isFinished = false;
    }
}