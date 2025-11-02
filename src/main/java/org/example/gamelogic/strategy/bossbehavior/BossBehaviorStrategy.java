package org.example.gamelogic.strategy.bossbehavior;

import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.enemy.Boss;

public interface BossBehaviorStrategy {
    void update(Boss boss, double deltaTime);
}
