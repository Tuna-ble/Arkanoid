package org.example.gamelogic.core;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.bricks.*;
import org.example.gamelogic.events.BrickDestroyedEvent;

public final class ScoreManager {


    private static class SingletonHolder {
        private static final ScoreManager INSTANCE = new ScoreManager();
    }

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
    public void addScore(int score) {
        if (score > 0) {
            this.currentScore += score;
        }
    }
    public int getScore() {
        return this.currentScore;
    }

    public void resetScore() {
        this.currentScore = 0;
    }
}
