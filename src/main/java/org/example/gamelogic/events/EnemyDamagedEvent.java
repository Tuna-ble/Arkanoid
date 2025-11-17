package org.example.gamelogic.events;

import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.enemy.Enemy;

public final class EnemyDamagedEvent extends GameEvent {
    private final Enemy damagedEnemy;
    private final GameObject damageSource;

    /**
     * Tạo event khi enemy bị gây sát thương.
     *
     * @param enemy enemy bị damage
     * @param obj   đối tượng gây sát thương (ball, laser, brick explode...)
     */
    public EnemyDamagedEvent(Enemy enemy, GameObject obj) {
        this.damagedEnemy = enemy;
        this.damageSource = obj;
    }

    /**
     * @return enemy vừa bị damage
     */
    public Enemy getDamagedEnemy() {
        return damagedEnemy;
    }

    /**
     * @return đối tượng gây ra sát thương
     */
    public GameObject getDamageSource() {
        return damageSource;
    }
}
