package org.example.gamelogic.strategy.movement;

import org.example.config.GameConstants;
import org.example.gamelogic.entities.enemy.Enemy;

public class DownMovementStrategy implements EnemyMovementStrategy {
    @Override
    public void move(Enemy enemy, double deltaTime) {
        enemy.setX(enemy.getX() +enemy.getDx() * deltaTime);
        enemy.setY(enemy.getY() +enemy.getDy() * deltaTime);
    }
}
