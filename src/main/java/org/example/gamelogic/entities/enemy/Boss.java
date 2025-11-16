package org.example.gamelogic.entities.enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.BulletType;
import org.example.gamelogic.strategy.bossbehavior.*;
import org.example.gamelogic.strategy.movement.StaticMovementStrategy;
import org.example.presentation.SpriteAnimation;

public class Boss extends AbstractEnemy {
    private BossBehaviorStrategy currentStrategy;
    private double startX;
    private Image image;

    private enum AnimState {
        IDLE,
        PREPARING_TO_SHOOT,
        HIT_REACTION
    }

    private AnimState animState = AnimState.IDLE;

    private Image idle, enraged;
    private SpriteAnimation shootAnimP1, shootAnimP2;
    private SpriteAnimation hitAnimP1, hitAnimP2;

    private BulletType bulletToFire_Type;
    private double bulletToFire_velX, bulletToFire_velY;

    public Boss(double x, double y, double dx, double dy) {
        super(x, y, GameConstants.BOSS_WIDTH, GameConstants.BOSS_HEIGHT,
                dx, dy, new StaticMovementStrategy());

        this.health = GameConstants.BOSS_HEALTH;
        this.scoreValue = 1000;
        this.startX = GameConstants.PLAY_AREA_X + GameConstants.PLAY_AREA_WIDTH / 2 - GameConstants.BOSS_WIDTH / 2;
        this.image = AssetManager.getInstance().getImage("boss");

        AssetManager am = AssetManager.getInstance();

        this.idle = am.getImage("boss");
        this.enraged = am.getImage("bossEnraged");

        this.shootAnimP1 = new SpriteAnimation(am.getImage("bossShoot"), 3, 3, 0.5, false);
        this.shootAnimP2 = new SpriteAnimation(am.getImage("bossEnragedShoot"), 3, 3, 0.5, false);

        this.hitAnimP1 = new SpriteAnimation(am.getImage("bossHit"), 8, 8, 0.3, false);
        this.hitAnimP2 = new SpriteAnimation(am.getImage("bossEnragedHit"), 8, 8, 0.3, false);

        this.currentStrategy = new BossEntryStrategy();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (currentStrategy instanceof BossDyingStrategy) {
            currentStrategy.update(this, deltaTime);
            return;
        }

        if (animState == AnimState.IDLE && currentStrategy != null) {
            currentStrategy.update(this, deltaTime);
        }

        if (animState == AnimState.PREPARING_TO_SHOOT) {
            SpriteAnimation anim = isPhase2() ? shootAnimP2 : shootAnimP1;
            if (anim != null) {
                anim.update(deltaTime);
                if (anim.isFinished()) {
                    fireBullet();
                    animState = AnimState.IDLE;
                }
            } else {
                fireBullet();
                animState = AnimState.IDLE;
            }
        } else if (animState == AnimState.HIT_REACTION) {
            SpriteAnimation anim = isPhase2() ? hitAnimP2 : hitAnimP1;
            if (anim != null) {
                anim.update(deltaTime);
                if (anim.isFinished()) {
                    animState = AnimState.IDLE;
                }
            } else { // Failsafe
                animState = AnimState.IDLE;
            }
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
        }
    }

    public void setStrategy(BossBehaviorStrategy newStrategy) {
        this.currentStrategy = newStrategy;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (currentStrategy instanceof BossDyingStrategy) {
            gc.drawImage(enraged, x, y, width, height);
            return;
        }

        if (animState == AnimState.PREPARING_TO_SHOOT) {
            SpriteAnimation anim = isPhase2() ? shootAnimP2 : shootAnimP1;
            if (anim != null) anim.render(gc, x, y, width, height);

        } else if (animState == AnimState.HIT_REACTION) {
            SpriteAnimation anim = isPhase2() ? hitAnimP2 : hitAnimP1;
            if (anim != null) anim.render(gc, x, y, width, height);

        } else {
            Image idleImg = isPhase2() ? enraged : idle;
            gc.drawImage(idleImg, x, y, width, height);
        }

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
        if (isDestroyed() || animState == AnimState.HIT_REACTION || currentStrategy instanceof BossDyingStrategy) {
            return;
        }

        health -= damage;

        if (health <= 0) {
            setStrategy(new BossDyingStrategy());
        } else {
            this.animState = AnimState.HIT_REACTION;

            SpriteAnimation currentHitAnim = (currentStrategy instanceof BossPhase1Strategy) ? hitAnimP1 : hitAnimP2;
            if (currentHitAnim != null) {
                currentHitAnim.reset();
            }
        }
    }

    @Override
    public void handleEntry(double deltaTime) {

    }

    private void fireBullet() {
        double x = this.getX() + this.getWidth() / 2 - 2;
        double y = this.getY() + this.getHeight() / 2 + 20;

        LaserManager.getInstance().createBullet(x, y,
                bulletToFire_velX, bulletToFire_velY,
                bulletToFire_Type, BulletFrom.ENEMY
        );
    }

    public void requestShoot(double velX, double velY, BulletType type) {
        if (animState == AnimState.IDLE) {
            this.animState = AnimState.PREPARING_TO_SHOOT;

            this.bulletToFire_velX = velX;
            this.bulletToFire_velY = velY;
            this.bulletToFire_Type = type;

            SpriteAnimation currentShootAnim = isPhase2() ? shootAnimP2 : shootAnimP1;
            if (currentShootAnim != null) {
                currentShootAnim.reset();
            }
        }
    }

    private boolean isPhase2() {
        return (currentStrategy instanceof BossPhase2Strategy ||
                currentStrategy instanceof BossEnrageStrategy);
    }

    public BossBehaviorStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    @Override
    public String getType() {
        return "BOSS";
    }
}
