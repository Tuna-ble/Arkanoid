package org.example.gamelogic.strategy.bossbehavior;

import org.example.config.GameConstants;
import org.example.gamelogic.entities.enemy.Boss;
import org.example.gamelogic.strategy.movement.LRMovementStrategy;

public class BossEnrageStrategy implements BossBehaviorStrategy {
    private double enrageTimer = 0.0;
    private final double ENRAGE_DURATION = 1.5;
    private double startX;

    public BossEnrageStrategy(double x) {
        this.startX = x;
    }

    @Override
    public void update(Boss boss, double deltaTime) {
        enrageTimer += deltaTime;

        if (enrageTimer <= ENRAGE_DURATION) {
            double shakeOffset = Math.sin(enrageTimer * 50) * 5;
            boss.setX(startX + shakeOffset);
        } else {
            boss.setX(startX);

            double padding = 100.0;
            double minX = padding;
            double maxX = GameConstants.SCREEN_WIDTH - padding;
            boss.setMovementStrategy(new LRMovementStrategy(minX, maxX));
            boss.setStrategy(new BossPhase2Strategy());
        }
    }
}
