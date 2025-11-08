package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.enemy.Enemy;

public final class BallHitEnemyEvent extends GameEvent {
    private final Enemy enemy;
    private final IBall ball;

    public BallHitEnemyEvent(Enemy enemy, IBall ball) {
        this.enemy = enemy;
        this.ball = ball;
    }

    public Enemy getEnemy() {
        return this.enemy;
    }

    public IBall getBall() {
        return ball;
    }
}
