package org.example.gamelogic.factory;

import org.example.gamelogic.entities.enemy.Enemy;
import org.example.gamelogic.registry.EnemyRegistry;

public class EnemyFactory {
    private final EnemyRegistry registry;

    public EnemyFactory(EnemyRegistry registry) {
        this.registry = registry;
    }

    public Enemy createEnemy(String enemyType, double x, double y) {
        Enemy prototype = registry.getPrototype(enemyType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found" + enemyType);
        }
        Enemy newEnemy = prototype.clone();
        newEnemy.setPosition(x,y);
        return newEnemy;
    }
}
