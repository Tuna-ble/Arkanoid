package org.example.gamelogic.strategy.movement;

import org.example.gamelogic.entities.enemy.Enemy;

public class LRMovementStrategy implements EnemyMovementStrategy {
    private double minX;
    private double maxX;

    public LRMovementStrategy(double minX, double maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    @Override
    public void move(Enemy enemy, double deltaTime) {
        double newX = enemy.getX() + enemy.getDx() * deltaTime;

        if (newX < minX) {
            newX = minX;
            enemy.setDx(Math.abs(enemy.getDx()));

        } else if (newX + enemy.getWidth() > maxX) {
            newX = maxX - enemy.getWidth();
            enemy.setDx(-Math.abs(enemy.getDx()));
        }
        enemy.setX(newX);
    }
}
