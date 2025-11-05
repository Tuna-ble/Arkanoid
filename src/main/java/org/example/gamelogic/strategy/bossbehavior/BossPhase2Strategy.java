package org.example.gamelogic.strategy.bossbehavior;

import org.example.config.GameConstants;
import org.example.gamelogic.core.EnemyManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.enemy.Boss;
import org.example.gamelogic.strategy.movement.EnemyMovementStrategy;
import org.example.gamelogic.strategy.movement.LRMovementStrategy;

public class BossPhase2Strategy implements BossBehaviorStrategy {
    private final double SPAWN_COOLDOWN = 5.0;
    private double spawnTimer = SPAWN_COOLDOWN;

    private double laserTimer = 0.0;
    private final double LASER_COOLDOWN = 1.0;

    @Override
    public void update(Boss boss, double deltaTime) {
        laserTimer += deltaTime;
        if (laserTimer >= LASER_COOLDOWN) {
            laserTimer = 0.0;

            double x = boss.getX() + boss.getWidth() / 2 - 2;
            double y = boss.getY() + boss.getHeight();

            LaserManager.getInstance().createBullet(x, y, -400, BulletFrom.ENEMY);
        }

        spawnTimer += deltaTime;
        if (spawnTimer >= SPAWN_COOLDOWN) {
            spawnTimer = 0.0;
            double bossCenterX = boss.getX() + (boss.getWidth() / 2.0);
            double bossCenterY = boss.getY() + (boss.getHeight() / 2.0);
            EnemyManager.getInstance().spawnEnemy("MINION", bossCenterX, bossCenterY);
        }
    }
}
