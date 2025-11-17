package org.example.gamelogic.core;

import org.example.config.GameConstants;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.events.GameOverEvent;
import org.example.gamelogic.events.LifeAddedEvent;
import org.example.gamelogic.events.LifeLostEvent;
import org.example.gamelogic.states.GameStateEnum;

/**
 * Quản lý số mạng (lives) của người chơi.
 *
 * <p>Singleton; cung cấp API để mất/mở mạng, reset và truy xuất số mạng hiện tại.
 */
public final class LifeManager {
    private static class SingletonHolder {
        private static final LifeManager INSTANCE = new LifeManager();
    }

    /**
     * Lấy instance đơn của LifeManager.
     *
     * @return singleton LifeManager
     */
    public static LifeManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private int lives;

    private LifeManager() {
        this.lives = GameConstants.INITIAL_LIVES;
    }

    /**
     * Giảm một mạng. Phát sự kiện tương ứng nếu mất mạng hoặc chuyển sang GAME_OVER khi hết mạng.
     */
    public void loseLife() {
        if (this.lives == 0) {
            return;
        }
        this.lives--;

        if (lives > 0) {
            EventManager.getInstance().publish(new LifeLostEvent(lives));
        } else {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.GAME_OVER)
            );
            EventManager.getInstance().publish(
                    new GameOverEvent()
            );
        }
    }

    /**
     * Tăng thêm một mạng và phát sự kiện LifeAddedEvent.
     */
    public void addLife() {
        this.lives++;
        EventManager.getInstance().publish(new LifeAddedEvent(lives));
    }

    /**
     * Reset số mạng về giá trị khởi tạo.
     */
    public void reset() {
        this.lives = GameConstants.INITIAL_LIVES;
    }

    /**
     * Lấy số mạng hiện tại.
     *
     * @return số mạng (int)
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * Thiết lập trực tiếp số mạng (sử dụng cẩn trọng).
     *
     * @param newLives số mạng mới
     */
    public void setLives(int newLives) {
        this.lives = newLives;
    }
}
