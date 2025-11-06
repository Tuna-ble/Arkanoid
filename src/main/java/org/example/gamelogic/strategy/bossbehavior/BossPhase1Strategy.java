package org.example.gamelogic.strategy.bossbehavior;

import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.enemy.Boss;
import org.example.gamelogic.strategy.movement.EnemyMovementStrategy;
import org.example.gamelogic.strategy.movement.StaticMovementStrategy;

public class BossPhase1Strategy implements BossBehaviorStrategy {
    private double laserTimer = 0.0;
    private final double LASER_COOLDOWN = 3.0;

    @Override
    public void update(Boss boss, double deltaTime) {
        laserTimer += deltaTime;
        if (laserTimer >= LASER_COOLDOWN) {
            laserTimer = 0.0;

            double x = boss.getX() + boss.getWidth() / 2 - 2;
            double y = boss.getY() + boss.getHeight();

            LaserManager.getInstance().createBullet(x, y, -400, BulletFrom.ENEMY);
        }
    }
}
