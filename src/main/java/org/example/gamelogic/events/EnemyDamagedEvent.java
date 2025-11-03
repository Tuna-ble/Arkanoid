package org.example.gamelogic.events;

import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.enemy.Enemy;

public final class EnemyDamagedEvent extends GameEvent {
    private final Enemy damagedEnemy;
    private final GameObject damageSource;

    public EnemyDamagedEvent(Enemy enemy, GameObject obj) {
        this.damagedEnemy = enemy;
        this.damageSource = obj;
    }

    public Enemy getDamagedEnemy() {
        return damagedEnemy;
    }

    public GameObject getDamageSource() {
        return damageSource;
    }
}
