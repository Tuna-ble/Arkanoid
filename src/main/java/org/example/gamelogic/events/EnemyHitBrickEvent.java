package org.example.gamelogic.events;

import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.enemy.Enemy;

public class EnemyHitBrickEvent extends GameEvent {
    private final Brick brick;
    private final Enemy enemy;

    public EnemyHitBrickEvent(Brick brick, Enemy enemy) {
        this.brick = brick;
        this.enemy = enemy;
    }

    public Brick getBrick() {
        return brick;
    }

    public Enemy getEnemy() {
        return enemy;
    }
}
