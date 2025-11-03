package org.example.gamelogic.strategy.bossbehavior;

import org.example.gamelogic.core.EnemyManager;
import org.example.gamelogic.entities.enemy.Boss;

public class BossSpawnStrategy implements BossBehaviorStrategy {
    private double spawnTimer = 0.0;
    private final double SPAWN_COOLDOWN = 3.0;

    @Override
    public void update(Boss boss, double deltaTime) {
        boss.setX(boss.getX() + boss.getDx() * deltaTime);

        spawnTimer += deltaTime;
        if (spawnTimer >= SPAWN_COOLDOWN) {
            spawnTimer = 0.0;
            double bossCenterX = boss.getX() + (boss.getWidth() / 2.0);
            double bossCenterY = boss.getY() + (boss.getHeight() / 2.0);
            EnemyManager.getInstance().spawnEnemy("MINION", bossCenterX, bossCenterY);
        }
    }
}
