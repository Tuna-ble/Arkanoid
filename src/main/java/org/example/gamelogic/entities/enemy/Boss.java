package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.strategy.bossbehavior.*;
import org.example.data.AssetManager;
import org.example.gamelogic.graphics.ImageModifier;
import org.example.gamelogic.strategy.bossbehavior.*;
import org.example.gamelogic.strategy.movement.StaticMovementStrategy;

public class Boss extends AbstractEnemy {
    private BossBehaviorStrategy currentStrategy;
    private double startX;

    private Color bossColor;
    private static final double BOSS_SPRITE_WIDTH = 220;
    private static final double BOSS_SPRITE_HEIGHT = 160;
    private static final double BOSS_SPRITE_PADDING = 20;

    public Boss(double x, double y, double dx, double dy) {
        super(x, y, GameConstants.BOSS_WIDTH, GameConstants.BOSS_HEIGHT,
                dx, dy, new StaticMovementStrategy());

        this.health = GameConstants.BOSS_HEALTH;
        this.scoreValue = 1000;
        this.startX = GameConstants.SCREEN_WIDTH / 2 - GameConstants.BOSS_WIDTH / 2;

        this.currentStrategy = new BossEntryStrategy();

        this.bossColor = GameConstants.NORMAL_BOSS_COLOR;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (currentStrategy != null) {
            currentStrategy.update(this, deltaTime);
        }

        if (!(currentStrategy instanceof BossDyingStrategy)) {
            checkPhaseChange();
        }
    }

    private void checkPhaseChange() {
        if (currentStrategy instanceof BossEntryStrategy) {
            return;
        }
        if (this.health <= GameConstants.BOSS_HEALTH / 2.0
                && !(currentStrategy instanceof BossEnrageStrategy || currentStrategy instanceof BossPhase2Strategy)) {
            setStrategy(new BossEnrageStrategy(startX));
            bossColor = GameConstants.ENRAGED_BOSS_COLOR;
        }
    }

    public void setStrategy(BossBehaviorStrategy newStrategy) {
        this.currentStrategy = newStrategy;
    }

    @Override
    public void render(GraphicsContext gc) {
        double bossSpriteX = 0;
        if (currentStrategy instanceof BossEnrageStrategy ||
                currentStrategy instanceof BossPhase2Strategy) {
            bossSpriteX += BOSS_SPRITE_WIDTH + BOSS_SPRITE_PADDING;
        }

        double bossSpriteY = 0;

        // Extract current phase frame from sprite sheet
        Image currentFrame = new WritableImage(
                enemySprites.getPixelReader(),
                (int) bossSpriteX,
                (int) bossSpriteY,
                (int) BOSS_SPRITE_WIDTH,
                (int) BOSS_SPRITE_HEIGHT
        );

        // Tint the current frame to desired color
        Image tintedFrame = ImageModifier.tintImage(currentFrame, bossColor);

        // Draw the tinted sprite at the desired position and size
        gc.drawImage(tintedFrame, this.x, this.y, this.width, this.height);

        gc.setFill(Color.BLACK);
        gc.fillRect(x, y - 10, width, 8);
        gc.setFill(Color.GREEN);
        gc.fillRect(x, y - 10, width * (this.health / GameConstants.BOSS_HEALTH), 8);
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
        if (!(currentStrategy instanceof BossDyingStrategy)) {
            this.health -= damage;

            if (health <= 0) {
                setStrategy(new BossDyingStrategy());
            }
        }
    }

    @Override
    public void handleEntry(double deltaTime) {

    }

    public BossBehaviorStrategy getCurrentStrategy() {
        return currentStrategy;
    }
}
