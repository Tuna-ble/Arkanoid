package org.example.gamelogic.events;

import org.example.gamelogic.entities.enemy.Enemy;

public final class EnemyDestroyedEvent {
    private final Enemy destroyedEnemy;

    public EnemyDestroyedEvent(Enemy enemy) {
        this.destroyedEnemy = enemy;
    }

    public Enemy getDestroyedEnemy() {
        return this.destroyedEnemy;
    }
}
