package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.strategy.bossbehavior.BossBehaviorStrategy;
import org.example.gamelogic.strategy.bossbehavior.BossEnrageStrategy;
import org.example.gamelogic.strategy.bossbehavior.BossEntryStrategy;
import org.example.gamelogic.strategy.bossbehavior.BossPhase2Strategy;
import org.example.gamelogic.strategy.movement.StaticMovementStrategy;

public class Boss extends AbstractEnemy {
    private BossBehaviorStrategy currentStrategy;
    private double startX;

    public Boss(double x, double y, double dx, double dy) {
        super(x, y, GameConstants.BOSS_WIDTH, GameConstants.BOSS_HEIGHT,
                dx, dy, new StaticMovementStrategy());

        this.health = GameConstants.BOSS_HEATLTH;
        this.scoreValue = 1000;
        this.startX = GameConstants.SCREEN_WIDTH / 2 - GameConstants.BOSS_WIDTH / 2;

        this.currentStrategy = new BossEntryStrategy();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (currentStrategy != null) {
            currentStrategy.update(this, deltaTime);
        }

        checkPhaseChange();
    }

    private void checkPhaseChange() {
        if (currentStrategy instanceof BossEntryStrategy) {
            return;
        }
        if (this.health <= GameConstants.BOSS_HEATLTH / 2.0
                && !(currentStrategy instanceof BossEnrageStrategy || currentStrategy instanceof BossPhase2Strategy)) {
            setStrategy(new BossEnrageStrategy(startX));
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

    @Override
    public void handleEntry(double deltaTime) {

    }
}
