package org.example.gamelogic.strategy.bossbehavior;

import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.BulletType;
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
            boss.requestShoot(0, 400, BulletType.BOSS_LASER);
        }
    }
}
