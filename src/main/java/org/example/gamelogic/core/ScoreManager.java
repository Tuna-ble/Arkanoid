package org.example.gamelogic.core;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.bricks.*;
import org.example.gamelogic.events.BrickDestroyedEvent;

/**
 * Quản lý điểm số người chơi (singleton).
 *
 * <p>Subscribe vào sự kiện {@code BrickDestroyedEvent} để cộng điểm tương ứng.
 */
public final class ScoreManager {

    private static class SingletonHolder {
        private static final ScoreManager INSTANCE = new ScoreManager();
    }

    /**
     * Lấy instance đơn của ScoreManager.
     *
     * @return singleton ScoreManager
     */
    public static ScoreManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private int currentScore;

    private ScoreManager() {
        this.currentScore = 0;
        EventManager.getInstance().subscribe(
                BrickDestroyedEvent.class,
                this::onBrickDestroyed 
        );
    }

    /**
     * Xử lý khi gạch bị phá để cộng điểm tương ứng.
     * Phương thức này là package-private và được đăng ký làm listener trong constructor.
     *
     * @param event sự kiện BrickDestroyedEvent chứa thông tin viên gạch bị phá
     */
    void onBrickDestroyed(BrickDestroyedEvent event) {
        Brick brick = event.getHitBrick();

        if (brick instanceof HardBrick) {
            addScore(GameConstants.POINTS_PER_HARD_BRICK);
        }
        else if (brick instanceof ExplosiveBrick) {
            addScore(GameConstants.POINTS_PER_EXPLOSIVE_BRICK);
        }
        else if (brick instanceof NormalBrick) {
            addScore(GameConstants.POINTS_PER_BRICK);
        }
        // Thêm các loại gạch khác (nếu có) ở đây
    }

    /**
     * Thêm điểm vào tổng điểm hiện tại (chỉ thêm nếu score > 0).
     *
     * @param score số điểm để cộng
     */
    public void addScore(int score) {
        if (score > 0) {
            this.currentScore += score;
        }
    }

    /**
     * Thiết lập trực tiếp tổng điểm (sử dụng cẩn trọng, ví dụ khi load save).
     *
     * @param newScore điểm mới
     */
    public void setScore(int newScore) {
        this.currentScore = newScore;
    }

    /**
     * Lấy tổng điểm hiện tại.
     *
     * @return điểm hiện tại
     */
    public int getScore() {
        return this.currentScore;
    }

    /**
     * Reset điểm về 0.
     */
    public void resetScore() {
        this.currentScore = 0;
    }
}
