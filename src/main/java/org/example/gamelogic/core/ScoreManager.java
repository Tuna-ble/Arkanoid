package org.example.gamelogic.core;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.bricks.*;
import org.example.gamelogic.entities.enemy.*;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.events.EnemyDestroyedEvent;

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
        EventManager.getInstance().subscribe(
                EnemyDestroyedEvent.class,
                this::onEnemyDestroyed
        );
    }

    private void onBrickDestroyed(BrickDestroyedEvent event) {
        Brick brick = event.getHitBrick();

        if (brick instanceof HardBrick) {
            addScore(GameConstants.POINTS_PER_HARD_BRICK);
        } else if (brick instanceof ExplosiveBrick) {
            addScore(GameConstants.POINTS_PER_EXPLOSIVE_BRICK);
        } else if (brick instanceof NormalBrick) {
            addScore(GameConstants.POINTS_PER_BRICK);
        } else if (brick instanceof HealingBrick) {
            addScore(GameConstants.POINTS_PER_HEALING_BRICK);
        }
    }

    private void onEnemyDestroyed(EnemyDestroyedEvent event) {
        Enemy enemy = event.getDestroyedEnemy();

        if (enemy instanceof Enemy1 || enemy instanceof Enemy2) {
            addScore(GameConstants.POINTS_PER_NORMAL_ENEMY);
        } else if (enemy instanceof BossMinion) {
            addScore(GameConstants.POINTS_PER_MINION);
        } else if (enemy instanceof Boss) {
            addScore(GameConstants.POINTS_PER_BOSS);
        }
    }

    public void addScore(int score) {
        if (score > 0) {
            this.currentScore += score;
        }
    }

    public void setScore(int newScore) {
        this.currentScore = newScore;
    }

    public int getScore() {
        return this.currentScore;
    }

    public void resetScore() {
        this.currentScore = 0;
    }
}
