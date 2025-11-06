package org.example.gamelogic.strategy.bossbehavior;

import org.example.gamelogic.core.EnemyManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.core.ObjectAccess;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.BulletType;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.enemy.Boss;

public class BossPhase2Strategy implements BossBehaviorStrategy {
    private final double SPAWN_COOLDOWN = 5.0;
    private double spawnTimer = SPAWN_COOLDOWN;

    private double laserTimer = 0.0;
    private final double LASER_COOLDOWN = 1.0;

    private double homingAttackTimer = 0.0;
    private final double HOMING_ATTACK_COOLDOWN = 2.5; // Cứ 2.5 giây lại bắt đầu 1 đợt

    private int bulletsLeft = 0;
    private double timerPerShot = 0.0;

    private final int BULLET_BURST_COUNT = 5;
    private final double TIME_BETWEEN_SHOTS = 0.1;
    private final double HOMING_BULLET_SPEED = 400.0;

    @Override
    public void update(Boss boss, double deltaTime) {
        //
        laserTimer += deltaTime;
        if (laserTimer >= LASER_COOLDOWN) {
            laserTimer = 0.0;

            double x = boss.getX() + boss.getWidth() / 2 - 2;
            double y = boss.getY() + boss.getHeight();

            LaserManager.getInstance().createBullet(x, y, 0, 400, BulletType.BOSS_LASER, BulletFrom.ENEMY);
        }

        //
        spawnTimer += deltaTime;
        if (spawnTimer >= SPAWN_COOLDOWN) {
            spawnTimer = 0.0;
            double bossCenterX = boss.getX() + (boss.getWidth() / 2.0);
            double bossCenterY = boss.getY() + (boss.getHeight() / 2.0);
            EnemyManager.getInstance().spawnEnemy("MINION", bossCenterX, bossCenterY);
        }

        //
        homingAttackTimer += deltaTime;
        if (homingAttackTimer >= HOMING_ATTACK_COOLDOWN && bulletsLeft == 0) {
            homingAttackTimer = 0.0;
            bulletsLeft = BULLET_BURST_COUNT;
            timerPerShot = 0.0;
        }

        if (bulletsLeft > 0) {
            timerPerShot -= deltaTime;

            if (timerPerShot <= 0.0) {
                Paddle paddle = ObjectAccess.getInstance().getPaddle();
                double targetX = paddle.getX() + paddle.getWidth() / 2;
                double targetY = paddle.getY() + paddle.getHeight() / 2;

                double startX = boss.getX() + boss.getWidth() / 2;
                double startY = boss.getY() + boss.getHeight() / 2;

                double dx = targetX - startX;
                double dy = targetY - startY;
                double centerAngle = Math.atan2(dy, dx);

                double velX = Math.cos(centerAngle) * HOMING_BULLET_SPEED;
                double velY = Math.sin(centerAngle) * HOMING_BULLET_SPEED;

                LaserManager.getInstance().createBullet(startX, startY, velX, velY,
                        BulletType.BOSS_HOMING_SQUARE, BulletFrom.ENEMY);

                bulletsLeft--;
                timerPerShot = TIME_BETWEEN_SHOTS;
            }
        }
    }
}
