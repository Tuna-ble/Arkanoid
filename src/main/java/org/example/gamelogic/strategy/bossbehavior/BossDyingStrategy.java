package org.example.gamelogic.strategy.bossbehavior;

import javafx.scene.paint.Color;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ParticleManager;
import org.example.gamelogic.entities.enemy.Boss;
import org.example.gamelogic.events.EnemyDestroyedEvent;

import java.util.Random;

public class BossDyingStrategy implements BossBehaviorStrategy {
    private double duration = 5.0;
    private double explosionTimer = 0.0;
    private final double TIME_BETWEEN_EXPLOSIONS = 0.2;

    private Random random = new Random();

    public BossDyingStrategy() {
        // SoundManager.getInstance().playSound("bomb");
    }

    @Override
    public void update(Boss boss, double deltaTime) {
        EventManager.getInstance().publish(new EnemyDestroyedEvent(boss));
        boss.setDx(0);
        boss.setDy(0);

        duration -= deltaTime;

        explosionTimer -= deltaTime;
        if (explosionTimer <= 0.0) {
            explosionTimer = TIME_BETWEEN_EXPLOSIONS;

            double explosionX = boss.getX() + random.nextDouble() * boss.getWidth();
            double explosionY = boss.getY() + random.nextDouble() * boss.getHeight();

            ParticleManager.getInstance().spawnBrickDebris(explosionX, explosionY, Color.ORANGERED);
        }

        if (duration <= 0.0) {
            ParticleManager.getInstance().spawnBrickDebris(boss.getCenterX(), boss.getCenterY(), Color.RED);
            boss.setActive(false);
        }
    }
}
