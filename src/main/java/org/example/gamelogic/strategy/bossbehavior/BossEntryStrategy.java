package org.example.gamelogic.strategy.bossbehavior;

import org.example.gamelogic.entities.enemy.Boss;

public class BossEntryStrategy implements BossBehaviorStrategy {
    @Override
    public void update(Boss boss, double deltaTime) {
        boss.setY(boss.getY() + boss.getDy() * deltaTime);

        if (boss.getY() >= 100) {
            boss.setDy(0);
            boss.setDx(150);
            boss.setHasEnteredScreen(true);
            boss.setStrategy(new BossPhase1Strategy());
        }
    }
}