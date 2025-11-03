package org.example.gamelogic.strategy.bossbehavior;

import org.example.gamelogic.core.EnemyManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.enemy.Boss;

public class BossAttackStrategy implements BossBehaviorStrategy {
    private double attackTimer = 0.0;

    @Override
    public void update(Boss boss, double deltaTime) {
        boss.setX(boss.getX() + boss.getDx() * deltaTime);

        attackTimer += deltaTime;
        if (attackTimer >= 2.0) {
            attackTimer = 0.0;

            double x = boss.getX() + boss.getWidth() / 2 - 2;
            double y = boss.getY() + boss.getHeight();

            LaserManager.getInstance().createBullet(x, y, -400, BulletFrom.ENEMY);
        }
    }
}
