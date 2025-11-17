package org.example.gamelogic.events;

import org.example.gamelogic.entities.enemy.Enemy;

public final class EnemyDestroyedEvent {
    private final Enemy destroyedEnemy;

    /**
     * Tạo event khi một enemy bị phá hủy.
     *
     * @param enemy enemy vừa bị tiêu diệt
     */
    public EnemyDestroyedEvent(Enemy enemy) {
        this.destroyedEnemy = enemy;
    }

    /**
     * @return enemy đã bị tiêu diệt
     */
    public Enemy getDestroyedEnemy() {
        return this.destroyedEnemy;
    }
}
