package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.enemy.Enemy;

public final class BallHitEnemyEvent extends GameEvent {
    private final Enemy enemy;
    private final IBall ball;

    /**
     * Tạo event khi bóng va chạm với enemy.
     *
     * @param enemy enemy bị bóng đập trúng
     * @param ball  bóng gây ra va chạm
     */
    public BallHitEnemyEvent(Enemy enemy, IBall ball) {
        this.enemy = enemy;
        this.ball = ball;
    }

    /**
     * @return enemy bị bóng va chạm
     */
    public Enemy getEnemy() {
        return this.enemy;
    }

    /**
     * @return bóng gây ra va chạm
     */
    public IBall getBall() {
        return ball;
    }
}
