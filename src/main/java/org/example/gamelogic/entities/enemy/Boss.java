package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.strategy.bossbehavior.BossAttackStrategy;
import org.example.gamelogic.strategy.bossbehavior.BossBehaviorStrategy;
import org.example.gamelogic.strategy.bossbehavior.BossEntryStrategy;
import org.example.gamelogic.strategy.bossbehavior.BossSpawnStrategy;

public class Boss extends AbstractEnemy {
    private BossBehaviorStrategy currentStrategy;

    public Boss(double x, double y, double dx, double dy) {
        super(x, y, GameConstants.BOSS_WIDTH, GameConstants.BOSS_HEIGHT, dx, dy);

        this.health = GameConstants.BOSS_HEATLTH;
        this.scoreValue = 1000;

        this.currentStrategy = new BossEntryStrategy();
    }

    @Override
    public void update(double deltaTime) {
        if (currentStrategy != null) {
            currentStrategy.update(this, deltaTime);
        }

        checkPhaseChange();
    }

    private void checkPhaseChange() {
        if (this.health <= GameConstants.BOSS_HEATLTH / 2.0 && !(currentStrategy instanceof BossSpawnStrategy)) {
            setStrategy(new BossSpawnStrategy());
        }
    }

    public void setStrategy(BossBehaviorStrategy newStrategy) {
        this.currentStrategy = newStrategy;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y, width, height);

        gc.setFill(Color.BLACK);
        gc.fillRect(x, y - 10, width, 8);
        gc.setFill(Color.GREEN);
        gc.fillRect(x, y - 10, width * (this.health / GameConstants.BOSS_HEATLTH), 8);
    }

    @Override
    public Enemy clone() {
        return new Boss(0, 0, this.dx, this.dy);
    }

    @Override
    public void takeDamage(double damage) {
        if (isDestroyed()) {
            return;
        }
        this.health -= damage;
        if (health <= 0) {
            this.isActive = false;
        }
    }
}
