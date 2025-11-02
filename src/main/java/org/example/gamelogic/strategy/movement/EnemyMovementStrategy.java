package org.example.gamelogic.strategy.movement;

import org.example.gamelogic.entities.enemy.Enemy;

public interface EnemyMovementStrategy {
    void move(Enemy enemy, double deltaTime);
}
