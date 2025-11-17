package org.example.gamelogic.events;

import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.enemy.Enemy;

public final class EnemyHitBrickEvent extends GameEvent {
    private final Brick brick;
    private final Enemy enemy;

    /**
     * Tạo event khi enemy va chạm với một viên gạch.
     *
     * @param brick gạch bị enemy đập trúng
     * @param enemy enemy gây ra va chạm
     */
    public EnemyHitBrickEvent(Brick brick, Enemy enemy) {
        this.brick = brick;
        this.enemy = enemy;
    }

    /**
     * @return gạch bị enemy va vào
     */
    public Brick getBrick() {
        return brick;
    }

    /**
     * @return enemy gây ra va chạm
     */
    public Enemy getEnemy() {
        return enemy;
    }
}
